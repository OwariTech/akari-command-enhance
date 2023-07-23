package org.owari.akari.enhance

import org.bukkit.Bukkit
import org.bukkit.event.*
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent

class CommandListener : Listener {
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
        if (Enhancer.isBlackListCommand(label)) return
        if (!Enhancer.isPluginCommand(label)) return

        val enhanced = Enhancer.enhance(cmd, e.sender)
        enhanced ?: return
        when (enhanced.size) {
            0 -> e.isCancelled = true
            1 -> e.command = if(slashed) "/${enhanced[0]}" else enhanced[0]
            else -> {
                e.isCancelled = true
                for (c in enhanced) Bukkit.dispatchCommand(e.sender, c)
            }
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
        if (!Enhancer.isPluginCommand(label)) return

        val enhanced = Enhancer.enhance(msg, e.player)
        enhanced ?: return
        when (enhanced.size) {
            0 -> e.isCancelled = true
            1 -> e.message = if(slashed) "/${enhanced[0]}" else enhanced[0]
            else -> {
                e.isCancelled = true
                for (c in enhanced) Bukkit.dispatchCommand(e.player, c)
            }
        }
    }
}


