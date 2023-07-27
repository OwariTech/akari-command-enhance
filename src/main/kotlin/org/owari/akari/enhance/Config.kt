package org.owari.akari.enhance

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.owari.shigure.Expression
import java.util.UUID

@Serializable
data class Config(
    @SerialName("enable-for-chat")
    val enableChat: Boolean = false,
    val blacklist: List<String> = listOf(),
)
