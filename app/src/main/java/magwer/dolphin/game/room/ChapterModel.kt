package magwer.dolphin.game.room

import kotlin.random.Random

class ChapterModel {
    // 章节房间数量设定
    private val base = 6
    private val range = 3
    val sum: Int = base + Random.nextInt(range)

    // 传送门及其所在坐标
    data class RoomLoc(val room: RoomModel, val x: Int, val y: Int)

    // 房间计数以及存储
    private var cnt = 0
    val rooms = HashMap<Int, RoomModel>()

    // 房间及其所有传送门
    val portalsOfRooms = HashMap<RoomModel?, ArrayList<RoomLoc>>()

    // 传送门相连关系
    val portals = HashMap<RoomLoc?, RoomLoc?>()

    // 获取房间并排序
    fun getRoom(room: RoomModel) {
        rooms.put(cnt, room)
        cnt++
    }

    // 设计房间传送关系
    val portNum = 4
    fun connectRooms() {
        //存储剩余可用传送点位(上下左右)
        val remainPort = mutableListOf<Int>(0, 1, 2, 3)
        //存储传送门点位
        val ports = mutableListOf<Int>()
        //存储同一房间的传送门
        val doors = ArrayList<RoomLoc>()

        for (i in 1 until sum) {
            // 选择一个在列表中的接口
            val index = Random.nextInt(remainPort.size)
            // 成为传送门从列表中移除
            ports.add(remainPort[index])
            remainPort.removeAt(index)
            // 选择新房间的一个接口成为传送门
            val indexNew = Random.nextInt(portNum)
            ports.add(portNum * i + indexNew)
            // 将其他接口放入列表中
            for (j in 0 until portNum) {
                if (j != indexNew) {
                    remainPort.add(portNum * i + j)
                }
            }
        }

        // 将传送门加入哈希表中
        for (i in 0 until ports.size step 2) {
            val roomLoc = RoomLoc(
                rooms[ports[i] / 4]!!,
                matchPosX(ports[i] % 4),
                matchPosY(ports[i] % 4)
            )

            val roomLocPair = RoomLoc(
                rooms[ports[i + 1] / 4]!!,
                matchPosX(ports[i + 1] % 4),
                matchPosY(ports[i + 1] % 4)
            )

            portals[roomLoc] = roomLocPair
        }

        ports.sort()
        for (i in 0 until ports.size) {
            val roomLoc = RoomLoc(
                rooms[ports[i] / 4]!!,
                matchPosX(ports[i] % 4),
                matchPosY(ports[i] % 4)
            )
            if (i < ports.size && ports[i] / 4 == ports[i + 1] / 4) {
                doors.add(roomLoc)
            } else {
                portalsOfRooms.put(rooms[ports[i] / 4], doors)
            }
        }
    }

    // 余数与坐标对应关系
    private val halfX = 14
    private val halfY = 8
    private val borderX = 5
    private val borderY = 3
    fun matchPosX(port: Int): Int {
        when (port) {
            0, 1 -> return halfX
            2 -> return borderX
        }
        return (halfX - borderX) * 2 + 1
    }

    fun matchPosY(port: Int): Int {
        when (port) {
            0 -> return borderY
            2, 3 -> return halfY
        }
        return (halfY - borderY) * 2 + 1
    }
}