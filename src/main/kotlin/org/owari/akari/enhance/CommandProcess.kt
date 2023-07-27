package org.owari.akari.enhance

import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.Bukkit
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.owari.shigure.Expression

object Enhancer {

    val selectors: List<String> = listOf("@p", "@r", "@s")
    val varAccess = "\$[a-zA-Z0-9_]+".toRegex()
    val exprEval = "\\\$\\{.*?}".toRegex()

    val noImpact = 0 to ""
    val refuse = 1 to ""
    fun edit(s: String) = 2 to s

    /**
     * 返回 (0, *) 表示不干预命令执行
     * 返回 (1, *) 取消命令执行
     * 返回 (2, *) 修改命令 message
     */
    fun applyVars(cmd: String, sender: CommandSender): Pair<Int, String> {
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
                else -> return refuse
            }
        }

        result = varAccess.replace(result) { ctx[it.value.substring(1)].toString() }

        // 表达式求值
        result = exprEval.replace(result) {
            val s = it.value
            println(s.substring(2, s.length - 1))
            try {
                Expression.of(s.substring(2, s.length - 1)).invoke(ctx)
            } catch (e: Exception) {
                0.0
            }.toString()
        }

        // PAPI 变量调用
        result = PlaceholderAPI.setPlaceholders(sender as? Player, result)

        return edit(result)
    }

    fun applySelectors(cmd: String, sender: CommandSender): Pair<Int, String>  {
        var result = cmd

        // 选择器
        if ("@r" in result) {
            val ps = Bukkit.getOnlinePlayers()
            if(ps.isEmpty()) return refuse
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
                    else return refuse
                }
                is Entity -> {
                    val loc = sender.location
                    val np = loc.world?.players?.map { it to it.location.distanceSquared(loc) }?.minByOrNull { it.second }
                    if (np != null) result = result.replace("@p", np.first.name)
                    else return refuse
                }
                // 行为是与 wiki 定义相符的.. 离世界出生点最近的玩家
                is ConsoleCommandSender -> {
                    val worlds = Bukkit.getWorlds()
                    if(worlds.isEmpty()) return refuse
                    val world = worlds[0]!!
                    val np = Bukkit.getOnlinePlayers().minByOrNull { it.location.distanceSquared(world.spawnLocation) }
                    if (np != null) result = result.replace("@p", np.name)
                    else return refuse
                }
                else -> return noImpact
            }
        }

        if ("@s" in result) {
            if (sender is Player) result = result.replace("@s", sender.name)
            else return refuse
        }

        return edit(result)
    }
}

