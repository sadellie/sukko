package io.github.sadellie.sukko.core.model.basic

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import io.github.sadellie.sukko.resources.Res
import io.github.sadellie.sukko.resources.core_model_m3color_background
import io.github.sadellie.sukko.resources.core_model_m3color_error
import io.github.sadellie.sukko.resources.core_model_m3color_error_container
import io.github.sadellie.sukko.resources.core_model_m3color_inverse_on_surface
import io.github.sadellie.sukko.resources.core_model_m3color_inverse_primary
import io.github.sadellie.sukko.resources.core_model_m3color_inverse_surface
import io.github.sadellie.sukko.resources.core_model_m3color_on_background
import io.github.sadellie.sukko.resources.core_model_m3color_on_error
import io.github.sadellie.sukko.resources.core_model_m3color_on_error_container
import io.github.sadellie.sukko.resources.core_model_m3color_on_primary
import io.github.sadellie.sukko.resources.core_model_m3color_on_primary_container
import io.github.sadellie.sukko.resources.core_model_m3color_on_primary_fixed
import io.github.sadellie.sukko.resources.core_model_m3color_on_primary_fixed_variant
import io.github.sadellie.sukko.resources.core_model_m3color_on_secondary
import io.github.sadellie.sukko.resources.core_model_m3color_on_secondary_container
import io.github.sadellie.sukko.resources.core_model_m3color_on_secondary_fixed
import io.github.sadellie.sukko.resources.core_model_m3color_on_secondary_fixed_variant
import io.github.sadellie.sukko.resources.core_model_m3color_on_surface
import io.github.sadellie.sukko.resources.core_model_m3color_on_surface_variant
import io.github.sadellie.sukko.resources.core_model_m3color_on_tertiary
import io.github.sadellie.sukko.resources.core_model_m3color_on_tertiary_container
import io.github.sadellie.sukko.resources.core_model_m3color_on_tertiary_fixed
import io.github.sadellie.sukko.resources.core_model_m3color_on_tertiary_fixed_variant
import io.github.sadellie.sukko.resources.core_model_m3color_outline
import io.github.sadellie.sukko.resources.core_model_m3color_outline_variant
import io.github.sadellie.sukko.resources.core_model_m3color_primary
import io.github.sadellie.sukko.resources.core_model_m3color_primary_container
import io.github.sadellie.sukko.resources.core_model_m3color_primary_fixed
import io.github.sadellie.sukko.resources.core_model_m3color_primary_fixed_dim
import io.github.sadellie.sukko.resources.core_model_m3color_scrim
import io.github.sadellie.sukko.resources.core_model_m3color_secondary
import io.github.sadellie.sukko.resources.core_model_m3color_secondary_container
import io.github.sadellie.sukko.resources.core_model_m3color_secondary_fixed
import io.github.sadellie.sukko.resources.core_model_m3color_secondary_fixed_dim
import io.github.sadellie.sukko.resources.core_model_m3color_surface
import io.github.sadellie.sukko.resources.core_model_m3color_surface_bright
import io.github.sadellie.sukko.resources.core_model_m3color_surface_container
import io.github.sadellie.sukko.resources.core_model_m3color_surface_container_high
import io.github.sadellie.sukko.resources.core_model_m3color_surface_container_highest
import io.github.sadellie.sukko.resources.core_model_m3color_surface_container_low
import io.github.sadellie.sukko.resources.core_model_m3color_surface_container_lowest
import io.github.sadellie.sukko.resources.core_model_m3color_surface_dim
import io.github.sadellie.sukko.resources.core_model_m3color_surface_tint
import io.github.sadellie.sukko.resources.core_model_m3color_surface_variant
import io.github.sadellie.sukko.resources.core_model_m3color_tertiary
import io.github.sadellie.sukko.resources.core_model_m3color_tertiary_container
import io.github.sadellie.sukko.resources.core_model_m3color_tertiary_fixed
import io.github.sadellie.sukko.resources.core_model_m3color_tertiary_fixed_dim
import org.jetbrains.compose.resources.StringResource

/** [isContentColor] if color is suitable for texts and icons (contrast on background) */
enum class M3Color(val displayName: StringResource, val isContentColor: Boolean) {
  PRIMARY(Res.string.core_model_m3color_primary, false),
  ON_PRIMARY(Res.string.core_model_m3color_on_primary, true),
  PRIMARY_CONTAINER(Res.string.core_model_m3color_primary_container, false),
  ON_PRIMARY_CONTAINER(Res.string.core_model_m3color_on_primary_container, true),
  INVERSE_PRIMARY(Res.string.core_model_m3color_inverse_primary, false),
  SECONDARY(Res.string.core_model_m3color_secondary, false),
  ON_SECONDARY(Res.string.core_model_m3color_on_secondary, true),
  SECONDARY_CONTAINER(Res.string.core_model_m3color_secondary_container, false),
  ON_SECONDARY_CONTAINER(Res.string.core_model_m3color_on_secondary_container, true),
  TERTIARY(Res.string.core_model_m3color_tertiary, false),
  ON_TERTIARY(Res.string.core_model_m3color_on_tertiary, true),
  TERTIARY_CONTAINER(Res.string.core_model_m3color_tertiary_container, false),
  ON_TERTIARY_CONTAINER(Res.string.core_model_m3color_on_tertiary_container, true),
  BACKGROUND(Res.string.core_model_m3color_background, false),
  ON_BACKGROUND(Res.string.core_model_m3color_on_background, true),
  SURFACE(Res.string.core_model_m3color_surface, false),
  ON_SURFACE(Res.string.core_model_m3color_on_surface, true),
  SURFACE_VARIANT(Res.string.core_model_m3color_surface_variant, false),
  ON_SURFACE_VARIANT(Res.string.core_model_m3color_on_surface_variant, true),
  SURFACE_TINT(Res.string.core_model_m3color_surface_tint, false),
  INVERSE_SURFACE(Res.string.core_model_m3color_inverse_surface, false),
  INVERSE_ON_SURFACE(Res.string.core_model_m3color_inverse_on_surface, true),
  ERROR(Res.string.core_model_m3color_error, false),
  ON_ERROR(Res.string.core_model_m3color_on_error, true),
  ERROR_CONTAINER(Res.string.core_model_m3color_error_container, false),
  ON_ERROR_CONTAINER(Res.string.core_model_m3color_on_error_container, true),
  OUTLINE(Res.string.core_model_m3color_outline, true),
  OUTLINE_VARIANT(Res.string.core_model_m3color_outline_variant, true),
  SCRIM(Res.string.core_model_m3color_scrim, false),
  SURFACE_BRIGHT(Res.string.core_model_m3color_surface_bright, false),
  SURFACE_DIM(Res.string.core_model_m3color_surface_dim, false),
  SURFACE_CONTAINER(Res.string.core_model_m3color_surface_container, false),
  SURFACE_CONTAINER_HIGH(Res.string.core_model_m3color_surface_container_high, false),
  SURFACE_CONTAINER_HIGHEST(Res.string.core_model_m3color_surface_container_highest, false),
  SURFACE_CONTAINER_LOW(Res.string.core_model_m3color_surface_container_low, false),
  SURFACE_CONTAINER_LOWEST(Res.string.core_model_m3color_surface_container_lowest, false),
  PRIMARY_FIXED(Res.string.core_model_m3color_primary_fixed, false),
  PRIMARY_FIXED_DIM(Res.string.core_model_m3color_primary_fixed_dim, false),
  ON_PRIMARY_FIXED(Res.string.core_model_m3color_on_primary_fixed, true),
  ON_PRIMARY_FIXED_VARIANT(Res.string.core_model_m3color_on_primary_fixed_variant, true),
  SECONDARY_FIXED(Res.string.core_model_m3color_secondary_fixed, false),
  SECONDARY_FIXED_DIM(Res.string.core_model_m3color_secondary_fixed_dim, false),
  ON_SECONDARY_FIXED(Res.string.core_model_m3color_on_secondary_fixed, true),
  ON_SECONDARY_FIXED_VARIANT(Res.string.core_model_m3color_on_secondary_fixed_variant, true),
  TERTIARY_FIXED(Res.string.core_model_m3color_tertiary_fixed, false),
  TERTIARY_FIXED_DIM(Res.string.core_model_m3color_tertiary_fixed_dim, false),
  ON_TERTIARY_FIXED(Res.string.core_model_m3color_on_tertiary_fixed, true),
  ON_TERTIARY_FIXED_VARIANT(Res.string.core_model_m3color_on_tertiary_fixed_variant, true);

  @Suppress("CyclomaticComplexMethod")
  fun extractFromScheme(colorScheme: ColorScheme): Color =
    when (this) {
      PRIMARY -> colorScheme.primary
      ON_PRIMARY -> colorScheme.onPrimary
      PRIMARY_CONTAINER -> colorScheme.primaryContainer
      ON_PRIMARY_CONTAINER -> colorScheme.onPrimaryContainer
      INVERSE_PRIMARY -> colorScheme.inversePrimary
      SECONDARY -> colorScheme.secondary
      ON_SECONDARY -> colorScheme.onSecondary
      SECONDARY_CONTAINER -> colorScheme.secondaryContainer
      ON_SECONDARY_CONTAINER -> colorScheme.onSecondaryContainer
      TERTIARY -> colorScheme.tertiary
      ON_TERTIARY -> colorScheme.onTertiary
      TERTIARY_CONTAINER -> colorScheme.tertiaryContainer
      ON_TERTIARY_CONTAINER -> colorScheme.onTertiaryContainer
      BACKGROUND -> colorScheme.background
      ON_BACKGROUND -> colorScheme.onBackground
      SURFACE -> colorScheme.surface
      ON_SURFACE -> colorScheme.onSurface
      SURFACE_VARIANT -> colorScheme.surfaceVariant
      ON_SURFACE_VARIANT -> colorScheme.onSurfaceVariant
      SURFACE_TINT -> colorScheme.surfaceTint
      INVERSE_SURFACE -> colorScheme.inverseSurface
      INVERSE_ON_SURFACE -> colorScheme.inverseOnSurface
      ERROR -> colorScheme.error
      ON_ERROR -> colorScheme.onError
      ERROR_CONTAINER -> colorScheme.errorContainer
      ON_ERROR_CONTAINER -> colorScheme.onErrorContainer
      OUTLINE -> colorScheme.outline
      OUTLINE_VARIANT -> colorScheme.outlineVariant
      SCRIM -> colorScheme.scrim
      SURFACE_BRIGHT -> colorScheme.surfaceBright
      SURFACE_DIM -> colorScheme.surfaceDim
      SURFACE_CONTAINER -> colorScheme.surfaceContainer
      SURFACE_CONTAINER_HIGH -> colorScheme.surfaceContainerHigh
      SURFACE_CONTAINER_HIGHEST -> colorScheme.surfaceContainerHighest
      SURFACE_CONTAINER_LOW -> colorScheme.surfaceContainerLow
      SURFACE_CONTAINER_LOWEST -> colorScheme.surfaceContainerLowest
      PRIMARY_FIXED -> colorScheme.primaryFixed
      PRIMARY_FIXED_DIM -> colorScheme.primaryFixedDim
      ON_PRIMARY_FIXED -> colorScheme.onPrimaryFixed
      ON_PRIMARY_FIXED_VARIANT -> colorScheme.onPrimaryFixedVariant
      SECONDARY_FIXED -> colorScheme.secondaryFixed
      SECONDARY_FIXED_DIM -> colorScheme.secondaryFixedDim
      ON_SECONDARY_FIXED -> colorScheme.onSecondaryFixed
      ON_SECONDARY_FIXED_VARIANT -> colorScheme.onSecondaryFixedVariant
      TERTIARY_FIXED -> colorScheme.tertiaryFixed
      TERTIARY_FIXED_DIM -> colorScheme.tertiaryFixedDim
      ON_TERTIARY_FIXED -> colorScheme.onTertiaryFixed
      ON_TERTIARY_FIXED_VARIANT -> colorScheme.onTertiaryFixedVariant
    }
}
