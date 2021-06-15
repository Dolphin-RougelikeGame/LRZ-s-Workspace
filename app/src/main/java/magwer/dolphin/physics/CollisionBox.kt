package magwer.dolphin.physics

interface CollisionBox {

    val centerX: Double
    val centerY: Double

    fun collideWith(box: CollisionBox): Boolean

}