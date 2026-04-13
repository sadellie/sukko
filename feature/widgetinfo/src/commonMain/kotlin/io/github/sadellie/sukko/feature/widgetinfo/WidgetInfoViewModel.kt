package io.github.sadellie.sukko.feature.widgetinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import io.github.sadellie.sukko.core.data.WidgetDataRepository
import io.github.sadellie.sukko.core.data.WidgetSubscriptionsRepository
import io.github.sadellie.sukko.core.model.WidgetSubscriptionInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@AssistedInject
class WidgetInfoViewModel(
  @Assisted private val appWidgetId: Int,
  private val widgetSubscriptionInfoRepository: WidgetSubscriptionsRepository,
  private val widgetDataRepository: WidgetDataRepository,
) : ViewModel() {
  internal val uiState = MutableStateFlow<WidgetInfoUIState?>(null)

  init {
    viewModelScope.launch {
      val widgetData = widgetDataRepository.loadByAppWidgetId(appWidgetId) ?: return@launch
      val subscriptionInfo = widgetSubscriptionInfoRepository.getSubscriptionInfo(appWidgetId)
      val subscriptions = subscriptionInfo.toSubscriptions()
      uiState.update {
        WidgetInfoUIState(
          name = widgetData.name,
          appWidgetId = widgetData.appWidgetId,
          widgetSubscriptions = subscriptions,
        )
      }
    }
  }

  private fun WidgetSubscriptionInfo.toSubscriptions(): List<Subscription> {
    val subscriptions = mutableListOf<Subscription>()
    if (isTime) subscriptions.add(Subscription.Time)
    if (isBattery) subscriptions.add(Subscription.Battery)
    if (isMedia) subscriptions.add(Subscription.Media)
    return subscriptions
  }

  @AssistedFactory
  @ManualViewModelAssistedFactoryKey
  @ContributesIntoMap(AppScope::class)
  fun interface Factory : ManualViewModelAssistedFactory {
    fun create(appWidgetId: Int): WidgetInfoViewModel
  }
}
