package magwer.dolphin.game.sceneobject

import android.os.SystemClock.sleep
import android.util.Log
import magwer.dolphin.animation.Animation
import magwer.dolphin.animation.BitmapHolder
import magwer.dolphin.api.loadBitmapAsset
import magwer.dolphin.game.GameScene
import magwer.dolphin.graphics.GLSquare
import magwer.dolphin.graphics.getAngle
import magwer.dolphin.physics.CircleBox
import magwer.dolphin.physics.Collider
import magwer.dolphin.physics.CollisionRule
import magwer.dolphin.ui.JoyStickControlTouchListener

class MainBulletObject(
    scene: GameScene,
    controller: JoyStickControlTouchListener,
    x: Double, y: Double) :
    BulletObject<CircleBox>(scene, x, y, 0.9, 0.9, "texture/main_character.png", 1.5) {

    override val collider =
        Collider(
            this,
            scene,
            CircleBox(x - width * 0.5, y - height * 0.5, width),
            CollisionRule.PLAYER_CHANNEL
        )
    private var angle = getAngle(controller.strengthX, controller.strengthY)
    private fun launch() {
        move(angle.first, angle.second)
        updateLoc()
    }

    override fun updateLoc() {
        super.updateLoc()
        glShape.screenX = gameToScreen(x - width * 0.5)
        glShape.screenY = gameToScreen(y - height * 0.5)
    }

    override fun addToScene() {
        super.addToScene()
        scene.animationManager.startAnimation(
            glShape.texture, Animation(
                2000, true,
                loadBitmapAsset(scene.context, "texture/main_character.png"),
                loadBitmapAsset(scene.context, "texture/main_character.png"),
                loadBitmapAsset(scene.context, "texture/main_character.png")
            )
        )
    }

    override fun removeFromScene() {
        super.removeFromScene()
        scene.animationManager.stopAnimation(glShape.texture)
    }

    override fun onTick(deltaTime: Long) {
        launch()
        if (this.collider.checkCollision().first.isNotEmpty()) {
            this.removeFromScene()
        }
        glShape.tintColor = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    }

}
