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
        var applied = Enhancer.applyVars(cmd, e.sender)
        if(applied.first == 1) {
            e.isCancelled = true
            return
        }
        // 不对原版命令启用选择器功能
        if (isPluginCommand(label)) {
            applied = Enhancer.applySelectors(applied.second, e.sender)
        }
        when(applied.first) {
            1 -> e.isCancelled = true
            2 -> e.command = applied.second
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
        var applied = Enhancer.applyVars(msg, e.player)
        if(applied.first == 1) {
            e.isCancelled = true
            return
        }
        // 不对原版命令启用选择器功能
        if (isPluginCommand(label)) {
            applied = Enhancer.applySelectors(applied.second, e.player)
        }
        when(applied.first) {
            1 -> e.isCancelled = true
            2 -> e.message = applied.second
            else -> return
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun on(e: AsyncPlayerChatEvent) {
        if(!conf.enableChat) return
        val msg = e.message
        var applied = Enhancer.applyVars(msg, e.player)
        if(applied.first == 1) {
            e.isCancelled = true
            return
        }
        // 不对原版命令启用选择器功能
        applied = Enhancer.applySelectors(applied.second, e.player)
        when(applied.first) {
            1 -> e.isCancelled = true
            2 -> e.message = applied.second
            else -> return
        }
    }
}


