package magwer.dolphin.physics

import kotlin.math.sqrt

class CircleBox(override val x: Double, override val y: Double, val radius: Double) : MobilCollisionBox {

    override val centerX: Double
        get() = x
    override val centerY: Double
        get() = y

    fun internal_collideWith(cir: CircleBox): Boolean {
        val dx = x - cir.x
        val dy = y - cir.y
        return sqrt(dx * dx + dy * dy) < radius + cir.radius
    }

    override fun copyTo(toX: Double, toY: Double): CircleBox {
        return CircleBox(toX, toY, radius)
    }

    override fun collideWith(box: CollisionBox): Boolean {
        return when (box) {
            is RectangleBox -> {
                CollisionHandler.collideWith(box, this)
            }
            is CircleBox -> {
                CollisionHandler.collideWith(this, box)
            }
            else -> throw TODO()
        }
    }
}