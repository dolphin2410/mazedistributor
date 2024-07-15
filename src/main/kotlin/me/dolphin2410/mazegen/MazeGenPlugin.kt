package me.dolphin2410.mazegen

import io.github.monun.kommand.kommand
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin

class MazeGenPlugin: JavaPlugin(), Listener {
    private lateinit var mazeDistributor: MazeDistributor
    private var managerIsParticipant = false

    override fun onEnable() {
        kommand {
            register("ignoreTestWarn") {
                executes {
                    managerIsParticipant = true
                }
            }
        }

        mazeDistributor = MazeDistributor(this)
        mazeDistributor.initializeDistributor()

        server.pluginManager.registerEvents(this, this)
    }

    override fun onDisable() {
        server.consoleSender.sendMessage("Plugin Exit")
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (e.player.name == "dolphin2410" && !managerIsParticipant) {
            e.player.sendMessage(text("You are excluded from the tests. Remove the line of code to participate in the test (/ignoreTestWarn)", NamedTextColor.AQUA))
            return
        }

        // Try to distribute a maze to a player
        if (!mazeDistributor.distribute(e.player)) {
            e.player.kick(text("The Maze Server is Currently Full"))
        }
    }

    @EventHandler
    fun onPlayerExit(e: PlayerQuitEvent) {
         mazeDistributor.removePlayer(e.player)
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (e.player.name == "dolphin2410" && !managerIsParticipant) {
            e.player.sendMessage(text("You are excluded from the tests. Remove the line of code to participate in the test (/ignoreTestWarn)", NamedTextColor.AQUA))
            return
        }
        if (e.player.inventory.itemInMainHand.type == Material.TORCH) {
            e.player.sendBlockChange(e.blockPlaced.location, Material.TORCH.createBlockData())
        }
        e.isCancelled = true
    }
}