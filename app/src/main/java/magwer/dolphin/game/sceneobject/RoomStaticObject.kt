package magwer.dolphin.game.sceneobject

import magwer.dolphin.animation.BitmapHolder
import magwer.dolphin.api.Coord
import magwer.dolphin.api.RenderedObject
import magwer.dolphin.api.loadBitmapAsset
import magwer.dolphin.game.GameScene
import magwer.dolphin.game.room.RoomGrid
import magwer.dolphin.graphics.GLSquare
import magwer.dolphin.physics.Collider
import magwer.dolphin.physics.CollisionRule
import magwer.dolphin.physics.RectangleBox

class RoomStaticObject(
    scene: GameScene,
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int,
    texture: String
) : GameObject(scene), RenderedObject {

    override val glShape = GLSquare(
        scene.view.shaderProgram,
        BitmapHolder(loadBitmapAsset(scene.context, texture)),
        gameToScreen(x.toDouble()),
        gameToScreen(y.toDouble()),
        gameToScreen(width.toDouble()),
        gameToScreen(height.toDouble())
    )

    val collider = Collider(
        this,
        scene,
        RectangleBox(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble()),
        CollisionRule.STATIC_CHANNEL
    )

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

    override fun onTick(deltaTime: Long) {

    }

    override fun getRoomCoords(roomGrid: RoomGrid): ArrayList<Coord> {
        val list = ArrayList<Coord>()
        for (x in x until x + width)
            for (y in y until y + height)
                list.add(roomGrid.gameToRoom(x.toDouble(), y.toDouble()))
        return list
    }
}