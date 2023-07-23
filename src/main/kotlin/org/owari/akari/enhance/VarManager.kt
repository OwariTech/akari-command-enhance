package org.owari.akari.enhance

import org.bukkit.command.CommandSender

object VarManager {
    private val variables: MutableMap<String, String> = mutableMapOf(
        "_akari" to "AKARI IS THE BEST!"
    )
    private val providers: MutableList<(String) -> String> = mutableListOf()

    fun getVar(name: String): String {
        return variables[name] ?: providers.firstNotNullOfOrNull { it(name) } ?: "null"
    }

    fun putVar(name: String, value: String) {
        variables[name] = value
    }

    fun registerProvider(provider: (String) -> String) {
        providers.add(provider)
    }
}
