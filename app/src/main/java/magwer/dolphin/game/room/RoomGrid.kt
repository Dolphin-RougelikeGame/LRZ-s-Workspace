package magwer.dolphin.game.room

import magwer.dolphin.api.Coord
import magwer.dolphin.api.adjacents
import magwer.dolphin.api.conners
import magwer.dolphin.api.distanceSquared
import magwer.dolphin.game.sceneobject.GameObject
import magwer.dolphin.physics.Collider
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class RoomGrid(
    private val startX: Int,
    private val startY: Int
) {

    class PathNode(val coord: Coord, val before: PathNode?, cost: Int) {

        val totalCost: Int = before?.totalCost ?: 0 + cost

    }

    inner class PathFinder(
        val origin: Coord,
        private val collider: Collider<*>,
        private var target: GameObject,
        private val targetRadius: Double
    ) {

        private val rule = collider.scene.collisionRule
        private val queue = ArrayList<PathNode>()
        private val pathMap = HashMap<Coord, PathNode>()
        private var result: PathNode? = null
        private var timeComputed = 0

        val done
            get() = result != null
        val failed
            get() = timeComputed > MAX_PATHFINDER_COMPUTE && result == null

        init {
            queue.add(
                PathNode(
                    origin,
                    null,
                    0
                )
            )
        }

        fun getPath(): ArrayList<Coord> {
            val list = ArrayList<Coord>()
            var node: PathNode? = result!!
            while (node != null) {
                list.add(0, node.coord)
                node = node.before
            }
            return list
        }

        fun computeForTick(): Boolean {
            return compute(TICK_PATHFINDER_COMPUTE)
        }

        private fun compute(times: Int): Boolean {
            for (i in 0 until times)
                if (!compute())
                    return false
            return true
        }

        private fun compute(): Boolean {
            if (timeComputed > MAX_PATHFINDER_COMPUTE)
                return false
            if (queue.isEmpty())
                return false
            val targetcs = target.getRoomCoords(this@RoomGrid)!!
            for (c in targetcs) {
                val res = pathMap[c]
                if (res != null) {
                    result = res
                    return false
                }
            }
            val top = queue.removeAt(0)
            val from = top.coord
            val adjs = from.adjacents
            val goodadjs = booleanArrayOf(false, false, false, false)
            for ((i, c) in adjs.withIndex()) {
                val objs = slotMap[c]
                if (objs == null || objs.all { !rule.blocksWith(it.channel, collider.channel) }) {
                    val next =
                        PathNode(c, top, 10)
                    if (targetcs.any { c.distanceSquared(it) <= targetRadius }) {
                        result = next
                        return false
                    }
                    val origin = pathMap[c]
                    if (origin == null || origin.totalCost > next.totalCost) {
                        pathMap[c] = next
                        queue.add(next)
                        goodadjs[i] = true
                    }
                }
            }
            val conners = from.conners
            for ((i, good) in goodadjs.withIndex()) {
                if (!good)
                    continue
                if (!goodadjs[(i + 1) % goodadjs.size])
                    continue
                val c = conners[i]
                val objs = slotMap[c]
                if (objs == null || objs.all { !rule.blocksWith(it.channel, collider.channel) }) {
                    val next =
                        PathNode(c, top, 14)
                    if (targetcs.any { c.distanceSquared(it) <= targetRadius }) {
                        result = next
                        return false
                    }
                    val origin = pathMap[c]
                    if (origin == null || origin.totalCost > next.totalCost) {
                        pathMap[c] = next
                        queue.add(next)
                    }
                }
            }
            timeComputed++
            return true
        }

    }

    private val objectMap = HashMap<Collider<*>, ArrayList<Coord>>()
    private val slotMap = HashMap<Coord, ArrayList<Collider<*>>>()

    fun gameToRoom(x: Double, y: Double): Pair<Int, Int> {
        return (x - startX).toInt() to (y - startY).toInt()
    }

    fun getColliders(slot: Coord): ArrayList<Collider<*>>? {
        return slotMap[slot]
    }

    fun updateSlot(obj: GameObject, collider: Collider<*>) {
        val coords = obj.getRoomCoords(this) ?: return
        val old = objectMap.put(collider, coords)
        if (old != null)
            for (slot in old)
                slotMap[slot]?.remove(collider)
        for (slot in coords)
            slotMap.getOrPut(slot, { ArrayList() }).add(collider)
        objectMap[collider] = coords
    }

    fun remove(collider: Collider<*>) {
        val coords = objectMap.remove(collider)?: return
        for (c in coords)
            slotMap[c]?.remove(collider)
    }

    fun findPath(
        collider: Collider<*>,
        from: Coord,
        to: GameObject,
        targetRadius: Double
    ): PathFinder {
        return PathFinder(from, collider, to, targetRadius)
    }

    companion object {

        const val TICK_PATHFINDER_COMPUTE = 80
        const val MAX_PATHFINDER_COMPUTE = 600

    }

}