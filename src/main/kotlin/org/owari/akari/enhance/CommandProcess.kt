package org.owari.akari.enhance

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

object Enhancer {
    fun isBlackListCommand(label: String): Boolean {
        return label in conf.blacklist
    }

    fun isPluginCommand(label: String): Boolean {
        println(Bukkit.getPluginCommand(label))
        return Bukkit.getPluginCommand(label) != null
    }

    val selectors: List<String> = listOf("@a", "@p", "@r", "@s")
    val varAccess = Regex.fromLiteral("\$[a-zA-Z0-9_]+")
    val exprEval = Regex.fromLiteral("\$\\{.*?\\}")

    /**
     * 返回 null 表示不干预命令执行
     * 返回 0 条命令时, 取消命令执行
     * 返回 1 条命令时, 会修改命令 message
     * 可能返回复数条命令, 这是使用 @a 选择器时的特殊情况, 原事件会被取消并为每个玩家执行一次
     */
    fun enhance(cmd: String, sender: CommandSender): List<String>? {
        var result = cmd

        // 直接变量调用
        // 内置变量 $loc 替换为 x y z
        if ("\$loc" in result) {
            when(sender) {
                is BlockCommandSender -> {
                    val loc = sender.block.location
                    result = result.replace("\$loc", "${loc.x} ${loc.y} ${loc.z}")
                }
                is Entity -> {
                    val loc = sender.location
                    result = result.replace("\$loc", "${loc.x} ${loc.y} ${loc.z}")
                }
                else -> return emptyList()
            }
        }

        varAccess.replace(result) { VarManager.getVar(it.value.substring(1)) }

        // 表达式求值
        exprEval.replace(result) {
            " "
        }

        // PAPI 变量调用
        PlaceholderAPI.setPlaceholders(sender as? Player, result)

        // 选择器
        if ("@r" in result) {
            val ps = Bukkit.getOnlinePlayers()
            if(ps.isEmpty()) return emptyList()
            val pn = ps.random().name
            result = result.replace("@r", pn)
        }

        if ("@p" in result) {
            when(sender) {
                is Player -> result = result.replace("@p", sender.name)
                is BlockCommandSender -> {
                    val loc = sender.block.location
                    val np = loc.world?.players?.map { it to it.location.distanceSquared(loc) }?.minByOrNull { it.second }
                    if (np != null) result = result.replace("@p", np.first.name)
                    else return emptyList()
                }
                is Entity -> {
                    val loc = sender.location
                    val np = loc.world?.players?.map { it to it.location.distanceSquared(loc) }?.minByOrNull { it.second }
                    if (np != null) result = result.replace("@p", np.first.name)
                    else return emptyList()
                }
                // 行为是与 wiki 定义相符的.. 离世界出生点最近的玩家
                is ConsoleCommandSender -> {
                    val worlds = Bukkit.getWorlds()
                    if(worlds.isEmpty()) return emptyList()
                    val world = worlds[0]!!
                    val np = Bukkit.getOnlinePlayers().minByOrNull { it.location.distanceSquared(world.spawnLocation) }
                    if (np != null) result = result.replace("@p", np.name)
                    else return emptyList()
                }
                else -> return null
            }
        }

        if ("@s" in result) {
            if (sender is Player) result = result.replace("@s", sender.name)
            else return emptyList()
        }

        return if ("@a" in result) Bukkit.getOnlinePlayers().map { result.replace("@a", it.name) }
        else if (result == cmd) return null
        else listOf(result)
    }
}

