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

class RoomFloorObject(
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

    override fun onTick(deltaTime: Long) {

    }

}