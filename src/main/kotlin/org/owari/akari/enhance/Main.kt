package org.owari.akari.enhance

import com.typesafe.config.ConfigFactory
import kotlinx.serialization.*
import kotlinx.serialization.hocon.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.owari.shigure.Context
import java.io.File

lateinit var main: Main
lateinit var conf: Config
val ctx = Context.empty()

class Main : JavaPlugin() {
    @OptIn(ExperimentalSerializationApi::class)
    override fun onEnable() {
        main = this

        logger.info("Akari Command Enhance is enabling.")

        // load config
        val df = dataFolder
        if (!df.exists()) df.mkdirs()
        val f = File(df, "config.conf")
        if (!f.exists()) saveResource("config.conf", true)
        conf = Hocon.decodeFromConfig(ConfigFactory.parseFile(f))

        // register listeners
        server.pluginManager.registerEvents(CommandListener(), this)
        server.getPluginCommand("akari_test")?.setExecutor(this)

        logger.info("Akari Command Enhance is enabled successfully.")
    }

    override fun onDisable() {
        logger.info("Akari Command Enhance is disabling.")

        logger.info("Akari Command Enhance is disabled successfully.")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage(args.joinToString())
        return true
    }
}