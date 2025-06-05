package io.github.sadellie.sukko.core.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class ColorSerializer : KSerializer<Color> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.LONG)

  override fun serialize(encoder: Encoder, value: Color) = encoder.encodeLong(value.value.toLong())

  override fun deserialize(decoder: Decoder): Color = Color(decoder.decodeLong().toULong())
}

class DpSerializer : KSerializer<Dp> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Dp", PrimitiveKind.FLOAT)

  override fun serialize(encoder: Encoder, value: Dp) = encoder.encodeFloat(value.value)

  override fun deserialize(decoder: Decoder): Dp = Dp(decoder.decodeFloat())
}

class SpSerializer : KSerializer<TextUnit> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Sp", PrimitiveKind.FLOAT)

  override fun serialize(encoder: Encoder, value: TextUnit) = encoder.encodeFloat(value.value)

  override fun deserialize(decoder: Decoder): TextUnit = decoder.decodeFloat().sp
}
