package magwer.dolphin.physics

class RectangleBox(override val x: Double, override val y: Double, var width: Double, var height: Double) :
    MobilCollisionBox {

    override val centerX: Double
        get() = x + width * 0.5
    override val centerY: Double
        get() = y + height * 0.5

    fun internal_collideWith(box: RectangleBox): Boolean {
        return x < box.x + box.width &&
                x + width > box.x &&
                y < box.y + box.height &&
                height + y > box.y
    }

    override fun copyTo(toX: Double, toY: Double): RectangleBox {
        return RectangleBox(toX, toY, width, height)
    }

    override fun collideWith(box: CollisionBox): Boolean {
        return when (box) {
            is RectangleBox -> {
                CollisionHandler.collideWith(this, box)
            }
            is CircleBox -> {
                CollisionHandler.collideWith(this, box)
            }
            else -> throw TODO()
        }
    }

}