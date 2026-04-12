package io.github.sadellie.sukko.core.unglance

import android.app.Presentation
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.display.DisplayManager
import android.view.Surface
import android.view.ViewGroup
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.DpSize
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import co.touchlab.kermit.Logger
import io.github.sadellie.sukko.core.common.filesPath
import io.github.sadellie.sukko.core.designsystem.LocalFilesDirPath
import io.github.sadellie.sukko.core.designsystem.LocalImageLoader
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.model.layer.RenderOption
import io.github.sadellie.sukko.core.model.layer.Renderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.compose.koinInject

// "Slightly" modified https://gist.github.com/iamcalledrob/871568679ad58e64959b097d4ef30738

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
fun renderAllWidgetConfigurations(
  applicationContext: Context,
  widgetSizes: List<DpSize>,
  layers: List<Layer.Evaluated>,
): Flow<RenderResult> =
  renderWidget(applicationContext, widgetSizes.toSet(), layers).map { renderSubResults ->
    val distinctSubResults =
      renderSubResults.distinctBy { renderSubResult -> renderSubResult.widgetSize }.toSet()
    RenderResult.Ready(subResults = distinctSubResults, layers = layers)
  }

/** @param widgetSizes Widget sizes, do not apply density */
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
private fun renderWidget(
  applicationContext: Context,
  widgetSizes: Set<DpSize>,
  layers: List<Layer.Evaluated>,
): Flow<Set<RenderSubResult>> = callbackFlow {
  val virtualPresenter = VirtualPresenter(applicationContext)
  val renderSubResults = mutableSetOf<RenderSubResult>()
  val mutex = Mutex()

  fun safeEmit(newItem: RenderSubResult) = launch {
    mutex.withLock {
      // only emit if added
      if (renderSubResults.add(newItem)) send(renderSubResults)
    }
  }
  launch(Dispatchers.Main) {
    virtualPresenter.setContent {
      CompositionLocalProvider(
        LocalImageLoader provides koinInject(),
        LocalFilesDirPath provides applicationContext.filesPath,
      ) {
        for (widgetSize in widgetSizes) {
          val graphicsLayer = rememberGraphicsLayer()
          // language server dies when you specify type in remember
          var allLayerBounds: Map<Int, Rect> by remember { mutableStateOf(emptyMap()) }
          Renderer(
            modifier =
              Modifier.drawWithContent {
                  graphicsLayer.record {
                    this@drawWithContent.drawContent()
                    safeEmit(RenderSubResult(widgetSize, graphicsLayer, allLayerBounds))
                    Logger.d(tag = TAG) { "Sending new result" }
                  }
                }
                .requiredSize(widgetSize),
            layers = layers,
            onGloballyPositioned = { id, layerBounds ->
              allLayerBounds = allLayerBounds + (id to layerBounds)
            },
            renderOption = RenderOption.HomeScreen,
          )
        }
      }
    }
  }
  awaitClose { virtualPresenter.close() }
}

/** Render result for [widgetSize] */
data class RenderSubResult(
  val widgetSize: DpSize,
  val graphicsLayer: GraphicsLayer,
  val bounds: Map<Int, Rect>,
)

sealed interface RenderResult {
  object Ignore : RenderResult

  object Error : RenderResult

  data class Ready(val subResults: Set<RenderSubResult>, val layers: List<Layer.Evaluated>) :
    RenderResult
}

/**
 * ```kotlin
 * val virtualPresenter = VirtualPresenter(applicationContext)
 * val canvasSize = DpSize(200.dp, 400.dp)
 * virtualPresenter.setContent(canvasSize) {
 *    // content here
 * }
 *
 * ...
 * virtualPresenter.close()
 * ```
 */
private class VirtualPresenter(applicationContext: Context) : AutoCloseable {
  val composeView =
    ComposeView(applicationContext).apply { layoutParams = ViewGroup.LayoutParams(1, 1) }

  fun setContent(content: @Composable () -> Unit) {
    presentation.setContentView(composeView, composeView.layoutParams)
    presentation.show()
    composeView.setContent(content = content)
  }

  override fun close() {
    composeView.disposeComposition()
    presentation.dismiss()
    virtualDisplay.release()
    surface.release()
    texture.release()
  }

  private val texture = SurfaceTexture(false)
  private val surface = Surface(texture)

  private val virtualDisplay =
    applicationContext
      .getDisplayManager()
      // Size of virtual display doesn't matter, because images are captured from compose, not the
      // display surface.
      .createVirtualDisplay(
        "virtualDisplay",
        1,
        1,
        72,
        surface,
        DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY,
      )

  private val presentation =
    Presentation(applicationContext, virtualDisplay.display).apply {
      window?.decorView?.let { view ->
        view.setViewTreeLifecycleOwner(ProcessLifecycleOwner.get())
        view.setViewTreeSavedStateRegistryOwner(EmptySavedStateRegistryOwner.shared)
        view.alpha =
          0f // If using default display, to ensure this does not appear on top of content.
      }
    }
}

private fun Context.getDisplayManager(): DisplayManager =
  getSystemService(Context.DISPLAY_SERVICE) as DisplayManager

private class EmptySavedStateRegistryOwner : SavedStateRegistryOwner {
  private val controller = SavedStateRegistryController.create(this).apply { performRestore(null) }

  private val lifecycleOwner: LifecycleOwner? = ProcessLifecycleOwner.get()

  override val lifecycle: Lifecycle
    get() =
      object : Lifecycle() {
        override fun addObserver(observer: LifecycleObserver) {
          lifecycleOwner?.lifecycle?.addObserver(observer)
        }

        override fun removeObserver(observer: LifecycleObserver) {
          lifecycleOwner?.lifecycle?.removeObserver(observer)
        }

        override val currentState = State.INITIALIZED
      }

  override val savedStateRegistry: SavedStateRegistry
    get() = controller.savedStateRegistry

  companion object {
    val shared = EmptySavedStateRegistryOwner()
  }
}

private const val TAG = "Captures"
