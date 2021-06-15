package magwer.dolphin.game.sceneobject

import magwer.dolphin.api.Coord
import magwer.dolphin.game.Accelerator
import magwer.dolphin.game.GameScene
import magwer.dolphin.game.room.RoomGrid
import magwer.dolphin.physics.Collider
import magwer.dolphin.physics.RectangleBox
import magwer.dolphin.ui.JoyStickControlTouchListener

class MainCharacter(scene: GameScene, private val controller: JoyStickControlTouchListener) :
    Character<RectangleBox>(scene, 0.0, 0.0, 1.0, 1.0, "texture/main_character.png") {

    override val collider =
        Collider(this, scene, RectangleBox(x - width * 0.5, y - height * 0.5, width, height), 0)

    private val moveAccX = Accelerator(-0.2, 0.2, 0.1)
    private val moveAccY = Accelerator(-0.2, 0.2, 0.1)

    override fun onTick(deltaTime: Long) {
        move(moveAccX.onTick(deltaTime, controller.strengthX), moveAccY.onTick(deltaTime, -controller.strengthY))
    }

    override fun copyCollision(x: Double, y: Double): RectangleBox {
        return collider.box.copyTo(x - width * 0.5, y - height * 0.5)
    }

    override fun getRoomCoords(roomGrid: RoomGrid): ArrayList<Coord>? {
        return arrayListOf(roomGrid.gameToRoom(x, y))
    }

}