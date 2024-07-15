package me.dolphin2410.mazegen

import io.github.monun.tap.fake.FakeEntityServer
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashMap

class MazeDistributor(plugin: MazeGenPlugin) {
    // MAZE_ID to MAZE
    private val mazeList = HashMap<Int, Maze>() // The length is always 4 since it is initialized at initializeDistributor()

    // PLAYER_UUID TO MAZE_ID
    private val occupiedList = HashMap<UUID, Int>()

    // MAZE_ID to MAZE_LOCATION
    private val mazeStartLocationMap = hashMapOf(
        1 to MazeData.location1.toLocation(plugin.server.worlds[0]),
        2 to MazeData.location2.toLocation(plugin.server.worlds[0]),
        3 to MazeData.location3.toLocation(plugin.server.worlds[0]),
        4 to MazeData.location4.toLocation(plugin.server.worlds[0]),
    )

    private val hasEmptySlot: Boolean
        get() = occupiedList.size < 4

    // initialize the distributor
    fun initializeDistributor() {
        for (mazeIndex in 1 until 5) {
            mazeList[mazeIndex] = Maze(mazeStartLocationMap[mazeIndex]!!)
        }
    }

    private fun getEmptySlot(): Int? {
        if (!hasEmptySlot) {
            return null
        }

        // Return the first mazeID that is not occupied
        return mazeList.keys.filterNot { mazeID ->
            occupiedList.values.any { occupiedID -> occupiedID == mazeID }
        }[0]
    }

    // returns true if successfully distributed the fakeserver
    fun distribute(player: Player): Boolean {
        if (!hasEmptySlot) { return false }

        val slotToApply = getEmptySlot()!!

        // Make player own the maze
        occupiedList[player.uniqueId] = slotToApply

        // Get the maze of the slot
        val mazeToApply = mazeList[slotToApply]

        mazeToApply!!.addPlayer(player)

        return true
    }

    fun getServerOf(player: Player): Maze {
        return mazeList[occupiedList[player.uniqueId]]!!
    }

    fun removePlayer(player: Player) {
        val removedIndex = occupiedList.remove(player.uniqueId)
        val mazeToReset = mazeList[removedIndex]
        mazeToReset!!.reset()
    }

    fun hasDistributedSlot(player: Player): Boolean {
        return occupiedList.containsKey(player.uniqueId)
    }
}