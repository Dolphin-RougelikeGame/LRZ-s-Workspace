package magwer.dolphin.game.sceneobject

import magwer.dolphin.api.Coord
import magwer.dolphin.game.GameScene
import magwer.dolphin.game.room.RoomGrid

abstract class GameObject(
    var scene: GameScene
) {

    open fun addToScene() {
        scene.internal_addGameObject(this)
    }

    open fun removeFromScene() {
        scene.internal_removeGameObject(this)
    }

    open fun changeScene(newScene: GameScene) {
        if (scene == newScene)
            return
        scene.internal_removeGameObject(this)
        scene = newScene
    }

    fun gameToScreen(coord: Double): Float {
        return (coord * 0.1).toFloat()
    }

    open fun getRoomCoords(roomGrid: RoomGrid): ArrayList<Coord>? {
        return null
    }

    abstract fun onTick(deltaTime: Long)

}