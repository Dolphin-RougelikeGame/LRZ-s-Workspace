package magwer.dolphin.game

import magwer.dolphin.animation.BitmapHolder
import magwer.dolphin.api.RenderedObject
import magwer.dolphin.api.loadBitmapAsset
import magwer.dolphin.game.sceneobject.GameObject
import magwer.dolphin.graphics.GLSquare

class TempBoxCharacter(scene: GameScene, val x: Int, val y: Int) :
    GameObject(scene), RenderedObject {

    val width = 1.0
    val height = 1.0

    override val glShape = GLSquare(
        scene.view.shaderProgram,
        BitmapHolder(loadBitmapAsset(scene.context, "texture/aaa.jpg")),
        gameToScreen(x - width * 0.5),
        gameToScreen(y - height * 0.5),
        gameToScreen(width),
        gameToScreen(height)
    )

    override fun onTick(deltaTime: Long) {

    }

}