package magwer.dolphin.game.sceneobject

import android.content.Intent
import android.view.View
import android.widget.ProgressBar
import magwer.dolphin.MainActivity
import magwer.dolphin.R
import magwer.dolphin.animation.Animation
import magwer.dolphin.animation.BitmapHolder
import magwer.dolphin.api.Coord
import magwer.dolphin.api.loadBitmapAsset
import magwer.dolphin.game.Accelerator
import magwer.dolphin.game.GameScene
import magwer.dolphin.game.Health
import magwer.dolphin.game.room.RoomGrid
import magwer.dolphin.graphics.GLSquare
import magwer.dolphin.graphics.getAngle
import magwer.dolphin.physics.Collider
import magwer.dolphin.physics.CollisionRule
import magwer.dolphin.physics.RectangleBox
import magwer.dolphin.ui.JoyStickControlTouchListener
import kotlin.concurrent.timerTask
import kotlin.math.roundToInt

class MainCharacter(
    scene: GameScene,
    private val healthBar: ProgressBar,
    private val controller: JoyStickControlTouchListener,
    x: Double, y: Double
) :
    Character<RectangleBox>(scene, x, y, 0.9, 0.9, "texture/dolphin-idle/dolphin-idle-1.png", 1.0) {

    override val collider = Collider(
        this,
        scene,
        RectangleBox(x - width * 0.5, y - height * 0.5, width, height),
        CollisionRule.PLAYER_CHANNEL
    )

    private val attackIndicator = GLSquare(
        scene.view.shaderProgram,
        BitmapHolder(loadBitmapAsset(scene.context, "texture/attack_indicator.png")),
        gameToScreen(x - 1.5 * width * 0.5),
        gameToScreen(y - 1.5 * height * 0.5),
        gameToScreen(1.5 * width),
        gameToScreen(1.5 * height)
    )
    private val moveAccX = Accelerator(-0.2, 0.2, 0.1)
    private val moveAccY = Accelerator(-0.2, 0.2, 0.1)
    override val health = Health(this, 10.0)

    override fun onDamage(damage: Double) {
        healthBar.progress = (health.health / health.maxHealth * 100).roundToInt()
        if (health.isDead) {
            var alpha = 0.0f
            val bloodscreen = scene.game.activity.findViewById<View>(R.id.blood_screen)
            scene.game.sceneTimer.scheduleAtFixedRate(timerTask {
                alpha += 0.1f
                bloodscreen.alpha = alpha
                if (alpha > 0.4f)
                    cancel()
            }, 50, 50)
            scene.game.sceneTimer.schedule(timerTask {
                scene.game.activity.runOnUiThread {
                    scene.game.shutdown()
                    scene.game.activity.startActivity(Intent(scene.game.activity, MainActivity::class.java).also {
                        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    scene.game.activity.overridePendingTransition(0, 0)
                }
                cancel()
            }, 1000)
            return
        }
    }

    override fun onHeal(heal: Double) {
        healthBar.progress = (health.health / health.maxHealth * 100).roundToInt()
    }

    override fun onChangeMaxHealth(old: Double, new: Double) {
        healthBar.maxWidth = (width / old * new).roundToInt()
        healthBar.minWidth = (width / old * new).roundToInt()
    }

    override fun updateLoc() {
        super.updateLoc()
        attackIndicator.screenX = gameToScreen(x - 1.5 * width * 0.5)
        attackIndicator.screenY = gameToScreen(y - 1.5 * height * 0.5)
        scene.game.openGLView.renderer.let {
            it.viewPort.transX = -gameToScreen(x - width * 0.5)
            it.viewPort.transY = -gameToScreen(y - height * 0.5)
        }
    }

    override fun addToScene() {
        updateLoc()
        scene.view.renderer.addShape(attackIndicator)
        super.addToScene()
        scene.animationManager.startAnimation(
            glShape.texture, Animation(
                900L,
                true,
                loadBitmapAsset(scene.context, "texture/dolphin-idle/dolphin-idle-1.png"),
                loadBitmapAsset(scene.context, "texture/dolphin-idle/dolphin-idle-2.png")
            )
        )
    }

    override fun removeFromScene() {
        super.removeFromScene()
        scene.animationManager.stopAnimation(glShape.texture)
    }

    override fun onTick(deltaTime: Long) {
        if (health.isDead)
            return
        move(
            moveAccX.onTick(deltaTime, controller.strengthX),
            moveAccY.onTick(deltaTime, -controller.strengthY)
        )
        val angle = getAngle(controller.strengthX, controller.strengthY)
        attackIndicator.rotationSin = angle.first.toFloat()
        attackIndicator.rotationCos = angle.second.toFloat()
        attackIndicator.updateRotation()
        if (System.currentTimeMillis() - health.lastDamageMil < 150)
            glShape.tintColor = floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f)
        else
            glShape.tintColor = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
    }

    override fun copyCollision(x: Double, y: Double): RectangleBox {
        return collider.box.copyTo(x - width * 0.5, y - height * 0.5)
    }

    override fun getRoomCoords(roomGrid: RoomGrid): ArrayList<Coord>? {
        return arrayListOf(roomGrid.gameToRoom(x, y))
    }

}