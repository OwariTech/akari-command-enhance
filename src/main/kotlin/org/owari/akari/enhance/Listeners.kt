package org.owari.akari.enhance

import org.bukkit.Bukkit
import org.bukkit.event.*
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent

class CommandListener : Listener {
    fun isBlackListCommand(label: String): Boolean {
        return label in conf.blacklist
    }

    fun isPluginCommand(label: String): Boolean {
        return Bukkit.getPluginCommand(label) != null
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(e: ServerCommandEvent) {
        val cmd = e.command
        val slashed = cmd[0] == '/'
        val label = run {
            var s = cmd
            if (slashed) s = s.substring(1)
            val i = s.indexOf(' ')
            if (i == -1) s else s.substring(0, i)
        }
        if (isBlackListCommand(label)) return
        if (!isPluginCommand(label)) return

        val enhanced = Enhancer.enhance(cmd, e.sender)
        when(enhanced.first) {
            1 -> e.isCancelled = true
            2 -> e.command = enhanced.second
            else -> return
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(e: PlayerCommandPreprocessEvent) {
        val msg = e.message
        val slashed = msg[0] == '/'
        val label = run {
            var s = msg
            if (slashed) s = s.substring(1)
            val i = s.indexOf(' ')
            if (i == -1) s else s.substring(0, i)
        }
        if (isBlackListCommand(label)) return
        if (!isPluginCommand(label)) return

        val enhanced = Enhancer.enhance(msg, e.player)
        when(enhanced.first) {
            1 -> e.isCancelled = true
            2 -> e.message = enhanced.second
            else -> return
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(e: AsyncPlayerChatEvent) {
        if(!conf.enableChat) return
        val msg = e.message
        val enhanced = Enhancer.enhance(msg, e.player)
        when(enhanced.first) {
            1 -> e.isCancelled = true
            2 -> e.message = enhanced.second
            else -> return
        }

    }
}


