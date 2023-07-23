package org.owari.akari.enhance

import kotlinx.serialization.Serializable


@Serializable
data class Config(
    val blacklist: List<String> = listOf(),
)

@Serializable
data class CustomVars(
    val vars: Map<String, String> = mapOf(),
)
