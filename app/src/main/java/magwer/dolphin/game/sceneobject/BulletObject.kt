package magwer.dolphin.game.sceneobject

import magwer.dolphin.animation.BitmapHolder
import magwer.dolphin.api.RenderedObject
import magwer.dolphin.api.loadBitmapAsset
import magwer.dolphin.game.GameScene
import magwer.dolphin.graphics.GLSquare
import magwer.dolphin.physics.*

abstract class BulletObject<T>(
    scene: GameScene,
    var x: Double,
    var y: Double,
    val width: Double,
    val height: Double,
    protected val texture: String,
    private val modelWidthScale: Double
) : GameObject(scene),
    RenderedObject {

    override val glShape = GLSquare(
        scene.view.shaderProgram,
        BitmapHolder(loadBitmapAsset(scene.context, texture)),
        gameToScreen(x - modelWidthScale * width * 0.5),
        gameToScreen(y - modelWidthScale * height * 0.5),
        gameToScreen(modelWidthScale * width),
        gameToScreen(modelWidthScale * height)
    )

    abstract val collider: Collider<CircleBox>

    fun move(dx: Double, dy: Double, times: Int = 0) {
        if (dx == 0.0 && dy == 0.0)
            return

        this.x += dx
        this.y += dy
        updateLoc()
    }

    protected open fun updateLoc() {
        glShape.screenX = gameToScreen(x - modelWidthScale * width * 0.5)
        glShape.screenY = gameToScreen(y - modelWidthScale * height * 0.5)
    }

    override fun addToScene() {
        super.addToScene()
        collider.addToScene()
    }

    override fun removeFromScene() {
        super.removeFromScene()
        collider.removeFromScene()
    }
}