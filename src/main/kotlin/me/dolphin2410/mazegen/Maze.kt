package me.dolphin2410.mazegen

import org.bukkit.Location
import org.bukkit.entity.Player

class Maze(private val startLocation: Location) {
    private var player: Player? = null

    fun addPlayer(player: Player) {
        this.player = player
        player.teleport(startLocation)
    }

    fun reset() {
        this.player?.kick()
        this.player = null
    }
}