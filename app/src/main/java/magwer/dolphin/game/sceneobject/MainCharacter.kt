package magwer.dolphin.game.sceneobject

import magwer.dolphin.animation.Animation
import magwer.dolphin.api.Coord
import magwer.dolphin.api.loadBitmapAsset
import magwer.dolphin.game.Accelerator
import magwer.dolphin.game.GameScene
import magwer.dolphin.game.room.RoomGrid
import magwer.dolphin.physics.Collider
import magwer.dolphin.physics.RectangleBox
import magwer.dolphin.ui.JoyStickControlTouchListener

class MainCharacter(scene: GameScene, private val controller: JoyStickControlTouchListener) :
    Character<RectangleBox>(scene, 0.0, 0.0, 1.0, 1.0, "texture/dolphin-idle/dolphin-idle-1.png") {

    override val collider =
        Collider(this, scene, RectangleBox(x - width * 0.5, y - height * 0.5, width, height), 0)

    private val moveAccX = Accelerator(-0.2, 0.2, 0.1)
    private val moveAccY = Accelerator(-0.2, 0.2, 0.1)

    override fun addToScene() {
        super.addToScene()
        scene.animationManager.startAnimation(glShape.texture, Animation(
            1500L,
            true,
            loadBitmapAsset(scene.context, "texture/dolphin-idle/dolphin-idle-1.png"),
            loadBitmapAsset(scene.context, "texture/dolphin-idle/dolphin-idle-2.png")
        ))
    }

    override fun removeFromScene() {
        super.removeFromScene()
        scene.animationManager.stopAnimation(glShape.texture)
    }

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