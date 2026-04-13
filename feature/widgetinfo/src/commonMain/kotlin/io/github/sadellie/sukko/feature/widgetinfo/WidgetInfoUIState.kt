package io.github.sadellie.sukko.feature.widgetinfo

import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.widget_info_battery_description
import io.github.sadellie.sukko.resources.widget_info_battery_name
import io.github.sadellie.sukko.resources.widget_info_media_description
import io.github.sadellie.sukko.resources.widget_info_media_name
import io.github.sadellie.sukko.resources.widget_info_time_description
import io.github.sadellie.sukko.resources.widget_info_time_name
import org.jetbrains.compose.resources.StringResource

internal data class WidgetInfoUIState(
  val name: String?,
  val appWidgetId: Int,
  val widgetSubscriptions: List<Subscription>,
)

internal sealed interface Subscription {
  val name: StringResource
  val description: StringResource

  data object Time : Subscription {
    override val name = Res.string.widget_info_time_name
    override val description = Res.string.widget_info_time_description
  }

  data object Battery : Subscription {
    override val name = Res.string.widget_info_battery_name
    override val description = Res.string.widget_info_battery_description
  }

  data object Media : Subscription {
    override val name = Res.string.widget_info_media_name
    override val description = Res.string.widget_info_media_description
  }
}
