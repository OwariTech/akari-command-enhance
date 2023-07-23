package org.owari.akari.enhance

import com.typesafe.config.ConfigFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.decodeFromConfig
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var main: Main
lateinit var conf: Config

class Main : JavaPlugin() {
    @OptIn(ExperimentalSerializationApi::class)
    override fun onEnable() {
        main = this

        logger.info("Akari Command Enhance is enabling.")

        // load config
        val df = dataFolder
        if (!df.exists()) df.mkdirs()
        val f = File(df, "config.conf")
        conf = Hocon.decodeFromConfig(ConfigFactory.parseFile(f))

        // register listeners
        server.pluginManager.registerEvents(CommandListener(), this)



        logger.info("Akari Command Enhance is enabled successfully.")
    }

    override fun onDisable() {
        logger.info("Akari Command Enhance is disabling.")

        logger.info("Akari Command Enhance is disabled successfully.")
    }
}