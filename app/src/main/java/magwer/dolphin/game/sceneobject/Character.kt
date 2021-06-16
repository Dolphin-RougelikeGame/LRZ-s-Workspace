package magwer.dolphin.game.sceneobject

import magwer.dolphin.animation.Animation
import magwer.dolphin.animation.BitmapHolder
import magwer.dolphin.api.Coord
import magwer.dolphin.api.Damageable
import magwer.dolphin.api.RenderedObject
import magwer.dolphin.api.loadBitmapAsset
import magwer.dolphin.game.GameScene
import magwer.dolphin.game.room.RoomGrid
import magwer.dolphin.graphics.GLSquare
import magwer.dolphin.physics.Collider
import magwer.dolphin.physics.CollisionBox
import magwer.dolphin.physics.RectangleBox
import magwer.dolphin.ui.JoyStickControlTouchListener
import kotlin.math.sqrt

abstract class Character<T : CollisionBox>(
    scene: GameScene,
    var x: Double,
    var y: Double,
    val width: Double,
    val height: Double,
    protected val texture: String,
    private val modelWidthScale: Double
) : GameObject(scene),
    RenderedObject, Damageable {

    override val glShape = GLSquare(
        scene.view.shaderProgram,
        BitmapHolder(loadBitmapAsset(scene.context, texture)),
        gameToScreen(x - modelWidthScale * width * 0.5),
        gameToScreen(y - modelWidthScale * height * 0.5),
        gameToScreen(modelWidthScale * width),
        gameToScreen(modelWidthScale * height)
    )

    abstract val collider: Collider<T>

    fun move(dx: Double, dy: Double, times: Int = 0) {
        if (dx == 0.0 && dy == 0.0)
            return
        moveTo(this.x + dx, this.y + dy, times)
    }

    fun moveTo(toX: Double, toY: Double, times: Int = 0) {
        if (toX == x && toY == y)
            return
        var targetx = toX
        var targety = toY
        val newbox = copyCollision(targetx, targety)
        val blocks = collider.checkCollision(newbox).first
        val newcenterx = newbox.centerX
        val newcentery = newbox.centerY
        for (collider in blocks) {
            val vecx = newcenterx - collider.box.centerX
            val vecy = newcentery - collider.box.centerY
            val len = sqrt(vecx * vecx + vecy * vecy)
            val nx = vecx / len
            val ny = vecy / len
            targetx += nx * 0.05
            targety += ny * 0.05
        }
        if (blocks.isNotEmpty() && times < 9) {
            moveTo(targetx, targety, times + 1)
            return
        }
        this.x = targetx
        this.y = targety
        updateLoc()
    }

    protected open fun updateLoc() {
        synchronized(collider.box) {
            collider.box = copyCollision(x, y)
        }
        glShape.screenX = gameToScreen(x - modelWidthScale * width * 0.5)
        glShape.screenY = gameToScreen(y - modelWidthScale * height * 0.5)
        scene.roomGrid.updateSlot(this, collider)
    }

    override fun addToScene() {
        super.addToScene()
        collider.addToScene()
        scene.roomGrid.updateSlot(this, collider)
    }

    override fun removeFromScene() {
        super.removeFromScene()
        collider.removeFromScene()
        scene.roomGrid.remove(collider)
    }

    override fun getRoomCoords(roomGrid: RoomGrid): ArrayList<Coord>? {
        return arrayListOf(roomGrid.gameToRoom(x, y))
    }

    abstract fun copyCollision(x: Double, y: Double): T

}