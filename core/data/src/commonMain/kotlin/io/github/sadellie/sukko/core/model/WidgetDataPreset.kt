package io.github.sadellie.sukko.core.model

import androidx.compose.ui.unit.dp
import io.github.sadellie.sukko.core.common.ASSET_PATH
import io.github.sadellie.sukko.core.iconfiles.IconFile
import io.github.sadellie.sukko.core.iconfiles.IconPack
import io.github.sadellie.sukko.core.model.basic.AlignmentSource
import io.github.sadellie.sukko.core.model.basic.ArrangementSource
import io.github.sadellie.sukko.core.model.basic.BrushSource
import io.github.sadellie.sukko.core.model.basic.ClickAction
import io.github.sadellie.sukko.core.model.basic.ContentScaleSource
import io.github.sadellie.sukko.core.model.basic.GlobalValue
import io.github.sadellie.sukko.core.model.basic.ImageUriSource
import io.github.sadellie.sukko.core.model.basic.M3Color
import io.github.sadellie.sukko.core.model.basic.ScriptableBoolean
import io.github.sadellie.sukko.core.model.basic.ScriptableColor
import io.github.sadellie.sukko.core.model.basic.ScriptableDouble
import io.github.sadellie.sukko.core.model.basic.ScriptableString
import io.github.sadellie.sukko.core.model.basic.ShapeSource
import io.github.sadellie.sukko.core.model.basic.TextStyleSource
import io.github.sadellie.sukko.core.model.layer.ColdBoxLayer
import io.github.sadellie.sukko.core.model.layer.ColdColumnLayer
import io.github.sadellie.sukko.core.model.layer.ColdImageLayer
import io.github.sadellie.sukko.core.model.layer.ColdProgressBarLayer
import io.github.sadellie.sukko.core.model.layer.ColdRowLayer
import io.github.sadellie.sukko.core.model.layer.ColdTextLayer
import io.github.sadellie.sukko.core.model.layer.Layer
import io.github.sadellie.sukko.core.model.modifier.ColdAlphaModifier
import io.github.sadellie.sukko.core.model.modifier.ColdBackgroundColorModifier
import io.github.sadellie.sukko.core.model.modifier.ColdBoxAlignmentModifier
import io.github.sadellie.sukko.core.model.modifier.ColdClipModifier
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxHeightModifier
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxSizeModifier
import io.github.sadellie.sukko.core.model.modifier.ColdFillMaxWidthModifier
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingAllSidesModifier
import io.github.sadellie.sukko.core.model.modifier.ColdPaddingAxisModifier
import io.github.sadellie.sukko.core.model.modifier.ColdRowWeightModifier
import io.github.sadellie.sukko.core.model.modifier.ColdSizeModifier
import kotlinx.serialization.Serializable
import okio.Path

@Serializable
sealed interface WidgetDataPreset {
  val presetId: Long
  val name: String
  val layers: List<Layer.Cold>
  val globals: Globals

  @Serializable
  data class Custom(
    override val presetId: Long,
    override val name: String,
    override val layers: List<Layer.Cold>,
    override val globals: Globals,
  ) : WidgetDataPreset

  @Serializable
  sealed interface BuiltIn : WidgetDataPreset {
    override fun getDataPath(filesDirPath: Path): Path = ASSET_PATH / DIR_PATH / presetId.toString()
  }

  data object MaterialBatteryPreset : BuiltIn {
    override val presetId: Long = -1L
    override val name: String = "Material battery"
    override val layers: List<Layer.Cold>
      get() =
        listOf(
          ColdBoxLayer(
            id = 0,
            name = "root box",
            widgetModifiers =
              listOf(
                ColdBackgroundColorModifier(
                  id = 0,
                  color = BrushSource.SolidColor(ScriptableColor.Global(2)),
                ),
                ColdFillMaxSizeModifier(id = 1),
                ColdPaddingAllSidesModifier(id = 2, all = ScriptableDouble.Global(0)),
                ColdClipModifier(id = 4),
                ColdBackgroundColorModifier(
                  id = 3,
                  color = BrushSource.SolidColor(ScriptableColor.Global(3)),
                ),
              ),
          ),
          ColdImageLayer(
            id = 2,
            parentId = 1,
            name = "device icon",
            widgetModifiers =
              listOf(ColdSizeModifier(id = 0, size = ScriptableDouble.Fixed(value = 24.0))),
            imageUriSource =
              ImageUriSource.IconPack(
                IconFile(fileName = "mobile_24px.svg", iconPack = IconPack.MaterialSymbolsRounded)
              ),
            tint = ScriptableColor.Global(0),
          ),
          ColdTextLayer(
            id = 3,
            parentId = 1,
            name = "device name",
            text = ScriptableString.Script(script = "deviceModel"),
            textColor = BrushSource.SolidColor(ScriptableColor.Global(0)),
          ),
          ColdTextLayer(
            id = 6,
            parentId = 5,
            name = "percentage",
            text = ScriptableString.Script(script = """batteryLevel"%""""),
            textColor = BrushSource.SolidColor(ScriptableColor.Global(0)),
          ),
          ColdBoxLayer(
            id = 7,
            parentId = 0,
            name = "charge indicator",
            widgetModifiers =
              listOf(
                ColdFillMaxHeightModifier(id = 0),
                ColdFillMaxWidthModifier(
                  id = 1,
                  fraction = ScriptableDouble.Script(script = "batteryLevel / 100"),
                ),
                ColdBackgroundColorModifier(
                  id = 2,
                  color = BrushSource.SolidColor(ScriptableColor.Global(1)),
                ),
              ),
          ),
          ColdRowLayer(
            id = 1,
            parentId = 0,
            name = "device row",
            widgetModifiers =
              listOf(ColdPaddingAllSidesModifier(id = 0, all = ScriptableDouble.Global(0))),
            arrangementSource = ArrangementSource.SpacedBy(space = ScriptableDouble.Fixed(8.0)),
            alignmentSource = AlignmentSource.CenterVertically,
          ),
          ColdTextLayer(
            id = 4,
            parentId = 0,
            name = "charge time",
            widgetModifiers =
              listOf(
                ColdBoxAlignmentModifier(id = 0, alignmentSource = AlignmentSource.BottomStart),
                ColdPaddingAllSidesModifier(id = 1, all = ScriptableDouble.Global(0)),
              ),
            text =
              ScriptableString.Script(
                script =
                  """
                  chargeTime = currentTimestamp + batteryFullEmpty
                  prefix = if(batteryStatus == "CHARGING", "Done by", "Until")
                  prefix" "formatTimestamp(chargeTime, "HH:mm")
                  """
                    .trimIndent()
              ),
            textColor = BrushSource.SolidColor(ScriptableColor.Global(0)),
          ),
          ColdRowLayer(
            id = 5,
            parentId = 0,
            name = "percentage row",
            widgetModifiers =
              listOf(
                ColdBoxAlignmentModifier(id = 0, alignmentSource = AlignmentSource.BottomEnd),
                ColdPaddingAllSidesModifier(id = 1, all = ScriptableDouble.Global(0)),
              ),
          ),
        )

    override val globals: Globals
      get() =
        Globals(
          colors =
            listOf(
              GlobalValue.GlobalColor(
                id = 0,
                label = "content color",
                initialValue = ScriptableColor.FixedM3(M3Color.ON_SECONDARY_CONTAINER),
              ),
              GlobalValue.GlobalColor(
                id = 1,
                label = "charge indicator",
                initialValue = ScriptableColor.FixedM3(M3Color.SECONDARY_CONTAINER),
              ),
              GlobalValue.GlobalColor(
                id = 2,
                label = "outer color",
                initialValue = ScriptableColor.FixedM3(M3Color.SURFACE_CONTAINER),
              ),
              GlobalValue.GlobalColor(
                id = 3,
                label = "charge indicator color",
                initialValue = ScriptableColor.FixedM3(M3Color.SURFACE_VARIANT),
              ),
            ),
          doubles =
            listOf(
              GlobalValue.GlobalDouble(
                id = 0,
                label = "item paddings",
                initialValue = ScriptableDouble.Fixed(12.0),
              )
            ),
        )
  }

  data object MaterialPlayerPreset : BuiltIn {
    override val presetId: Long = -2
    override val name: String = "Material player"
    override val layers: List<Layer.Cold>
      get() =
        listOf(
          ColdRowLayer(
            id = 2,
            parentId = 1,
            name = "player info",
            widgetModifiers = listOf(ColdFillMaxWidthModifier(id = 0)),
            arrangementSource = ArrangementSource.SpaceBetween,
          ),
          ColdImageLayer(
            id = 3,
            parentId = 2,
            name = "player icon",
            widgetModifiers =
              listOf(
                ColdSizeModifier(id = 0, size = ScriptableDouble.Fixed(value = 24.0)),
                ColdClipModifier(id = 2, shapeSource = ShapeSource.Circle),
                ColdBackgroundColorModifier(
                  id = 1,
                  color = BrushSource.SolidColor(color = ScriptableColor.Global(id = 1)),
                ),
                ColdPaddingAllSidesModifier(id = 3, all = ScriptableDouble.Fixed(value = 4.0)),
              ),
            imageUriSource = ImageUriSource.PlayerIcon,
            tint = ScriptableColor.Global(id = 2),
          ),
          ColdTextLayer(
            id = 4,
            parentId = 2,
            name = "player name",
            widgetModifiers =
              listOf(
                ColdClipModifier(id = 1),
                ColdBackgroundColorModifier(
                  id = 0,
                  color = BrushSource.SolidColor(color = ScriptableColor.Global(id = 1)),
                ),
                ColdPaddingAxisModifier(
                  id = 2,
                  horizontal = ScriptableDouble.Fixed(value = 8.0),
                  vertical = ScriptableDouble.Fixed(value = 4.0),
                ),
              ),
            clickActions = listOf(ClickAction.MediaOpenPlayer(id = 0)),
            textStyleSource =
              TextStyleSource.Local(fontSize = ScriptableDouble.Fixed(value = 12.0)),
            text = ScriptableString.Script(script = "playerName"),
            textColor = BrushSource.SolidColor(color = ScriptableColor.Global(id = 2)),
          ),
          ColdRowLayer(
            id = 5,
            parentId = 1,
            name = "media info and play button",
            widgetModifiers = listOf(ColdFillMaxWidthModifier(id = 0)),
            arrangementSource = ArrangementSource.SpaceBetween,
          ),
          ColdColumnLayer(
            id = 6,
            parentId = 5,
            name = "media info",
            widgetModifiers = listOf(ColdRowWeightModifier(id = 0)),
          ),
          ColdTextLayer(
            id = 7,
            parentId = 6,
            name = "title",
            text = ScriptableString.Script(script = "mediaTitle"),
            textColor = BrushSource.SolidColor(color = ScriptableColor.Global(id = 0)),
          ),
          ColdTextLayer(
            id = 8,
            parentId = 6,
            name = "artist",
            widgetModifiers =
              listOf(ColdAlphaModifier(id = 0, alpha = ScriptableDouble.Fixed(value = 0.7))),
            text = ScriptableString.Script(script = "mediaArtist"),
            textColor = BrushSource.SolidColor(color = ScriptableColor.Global(id = 0)),
          ),
          ColdRowLayer(
            id = 10,
            parentId = 1,
            name = "song controls",
            arrangementSource = ArrangementSource.SpacedBy(space = ScriptableDouble.Fixed(16.0)),
            alignmentSource = AlignmentSource.CenterVertically,
          ),
          ColdImageLayer(
            id = 11,
            parentId = 10,
            name = "rewind",
            widgetModifiers =
              listOf(ColdSizeModifier(id = 0, size = ScriptableDouble.Global(id = 1))),
            clickActions = listOf(ClickAction.MediaSkipToPrevious(id = 0)),
            imageUriSource =
              ImageUriSource.IconPack(
                iconFile =
                  IconFile(
                    fileName = "arrow_back_24px.svg",
                    iconPack = IconPack.MaterialSymbolsRounded,
                  )
              ),
            tint = ScriptableColor.Global(id = 0),
          ),
          ColdProgressBarLayer(
            id = 12,
            parentId = 10,
            name = "seek",
            widgetModifiers = listOf(ColdRowWeightModifier(id = 0)),
            progress = ScriptableDouble.Script(script = "mediaPosition/mediaDuration"),
            color = ScriptableColor.Global(id = 0),
            trackColor = ScriptableColor.Global(id = 0),
            amplitude = ScriptableDouble.Fixed(value = 0.6),
            waveLength = ScriptableDouble.Fixed(value = 25.0),
          ),
          ColdImageLayer(
            id = 13,
            parentId = 10,
            name = "forward",
            widgetModifiers =
              listOf(ColdSizeModifier(id = 0, size = ScriptableDouble.Global(id = 1))),
            clickActions = listOf(ClickAction.MediaSkipToNext(id = 0)),
            imageUriSource =
              ImageUriSource.IconPack(
                iconFile =
                  IconFile(
                    fileName = "arrow_forward_24px.svg",
                    iconPack = IconPack.MaterialSymbolsRounded,
                  )
              ),
            tint = ScriptableColor.Global(id = 0),
          ),
          ColdImageLayer(
            id = 0,
            name = "cover",
            widgetModifiers =
              listOf(ColdFillMaxSizeModifier(id = 0), ColdBackgroundColorModifier(id = 1)),
            imageUriSource =
              ImageUriSource.Link(value = ScriptableString.Script(script = "mediaCover")),
            contentScale = ContentScaleSource.Crop,
          ),
          ColdBoxLayer(
            id = 14,
            name = "overlay",
            widgetModifiers =
              listOf(
                ColdFillMaxSizeModifier(id = 0),
                ColdAlphaModifier(id = 1, alpha = ScriptableDouble.Fixed(value = 0.7)),
                ColdBackgroundColorModifier(
                  id = 2,
                  color =
                    BrushSource.SolidColor(
                      color =
                        ScriptableColor.Script(script = "colorScheme(\"PRIMARY\", mediaCover)")
                    ),
                ),
                ColdAlphaModifier(id = 4, alpha = ScriptableDouble.Fixed(value = 0.5)),
                ColdBackgroundColorModifier(
                  id = 3,
                  color =
                    BrushSource.SolidColor(color = ScriptableColor.FixedM3(value = M3Color.SCRIM)),
                ),
              ),
          ),
          ColdColumnLayer(
            id = 1,
            name = "overlay content",
            widgetModifiers =
              listOf(
                ColdPaddingAllSidesModifier(id = 0, all = ScriptableDouble.Fixed(value = 16.0)),
                ColdFillMaxSizeModifier(id = 1),
              ),
            arrangementSource = ArrangementSource.SpaceBetween,
          ),
          ColdImageLayer(
            id = 15,
            parentId = 5,
            name = "play button",
            widgetModifiers =
              listOf(
                ColdClipModifier(id = 2, shapeSource = ShapeSource.Circle),
                ColdSizeModifier(id = 0, size = ScriptableDouble.Global(id = 0)),
                ColdBackgroundColorModifier(
                  id = 1,
                  color = BrushSource.SolidColor(color = ScriptableColor.Global(id = 1)),
                ),
                ColdPaddingAllSidesModifier(id = 3, all = ScriptableDouble.Fixed(value = 8.0)),
              ),
            clickActions = listOf(ClickAction.MediaPlay(id = 0)),
            isEnabled = ScriptableBoolean.Script(script = "playerState!=\"PLAYING\""),
            imageUriSource =
              ImageUriSource.IconPack(
                iconFile =
                  IconFile(
                    fileName = "play_arrow_fill1_24px.svg",
                    iconPack = IconPack.MaterialSymbolsRounded,
                  )
              ),
            tint = ScriptableColor.Global(id = 2),
          ),
          ColdImageLayer(
            id = 16,
            parentId = 5,
            name = "pause button",
            widgetModifiers =
              listOf(
                ColdSizeModifier(id = 0, size = ScriptableDouble.Global(id = 0)),
                ColdClipModifier(id = 1, shapeSource = ShapeSource.CutCornersDp(size = 8.dp)),
                ColdBackgroundColorModifier(
                  id = 2,
                  color = BrushSource.SolidColor(color = ScriptableColor.Global(id = 1)),
                ),
                ColdPaddingAllSidesModifier(id = 3, all = ScriptableDouble.Fixed(value = 8.0)),
              ),
            clickActions = listOf(ClickAction.MediaPause(id = 0)),
            isEnabled = ScriptableBoolean.Script(script = "playerState==\"PLAYING\""),
            imageUriSource =
              ImageUriSource.IconPack(
                iconFile =
                  IconFile(
                    fileName = "pause_fill1_24px.svg",
                    iconPack = IconPack.MaterialSymbolsRounded,
                  )
              ),
            tint = ScriptableColor.Global(id = 2),
          ),
        )

    override val globals: Globals
      get() =
        Globals(
          doubles =
            listOf(
              GlobalValue.GlobalDouble(
                id = 0,
                label = "primary button size",
                initialValue = ScriptableDouble.Fixed(value = 46.0),
              ),
              GlobalValue.GlobalDouble(
                id = 1,
                label = "secondary button size",
                initialValue = ScriptableDouble.Fixed(value = 24.0),
              ),
            ),
          colors =
            listOf(
              GlobalValue.GlobalColor(
                id = 0,
                label = "content color",
                initialValue = ScriptableColor.FixedM3(value = M3Color.ON_SURFACE),
              ),
              GlobalValue.GlobalColor(
                id = 1,
                label = "container color",
                initialValue =
                  ScriptableColor.Script(script = "colorScheme(\"PRIMARY_CONTAINER\", mediaCover)"),
              ),
              GlobalValue.GlobalColor(
                id = 2,
                label = "on container color",
                initialValue =
                  ScriptableColor.Script(
                    script = "colorScheme(\"ON_PRIMARY_CONTAINER\", mediaCover)"
                  ),
              ),
            ),
        )
  }

  fun getDataPath(filesDirPath: Path) = filesDirPath / DIR_PATH / presetId.toString()

  fun getPreviewPath(filesDirPath: Path) = getDataPath(filesDirPath) / PREVIEW_IMAGE_NAME

  companion object {
    const val PREVIEW_IMAGE_NAME = "preview.png"
    private const val DIR_PATH = "widgetDataPreset"

    fun builtIns(): List<BuiltIn> = listOf(MaterialBatteryPreset, MaterialPlayerPreset)
  }
}
