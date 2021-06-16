package magwer.dolphin.game.sceneobject

import magwer.dolphin.animation.BitmapHolder
import magwer.dolphin.api.Coord
import magwer.dolphin.api.RenderedObject
import magwer.dolphin.api.loadBitmapAsset
import magwer.dolphin.game.GameScene
import magwer.dolphin.game.room.ChapterModel
import magwer.dolphin.game.room.RoomGrid
import magwer.dolphin.graphics.GLSquare
import magwer.dolphin.physics.Collider
import magwer.dolphin.physics.CollisionRule
import magwer.dolphin.physics.RectangleBox

class PortalObject(
    scene: GameScene,
    x: Int,
    y: Int,
    width: Int,
    height: Int,
    texture: String
) : RoomStaticObject(scene, x, y, width, height, texture) {

    override val glShape = GLSquare(
        scene.view.shaderProgram,
        BitmapHolder(loadBitmapAsset(scene.context, texture)),
        gameToScreen(x.toDouble()),
        gameToScreen(y.toDouble()),
        gameToScreen(width.toDouble()),
        gameToScreen(height.toDouble())
    )

    override val collider = Collider(
        this,
        scene,
        RectangleBox(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble()),
        CollisionRule.INTERACT_CHANNEL
    )

    override fun onTick(deltaTime: Long) {
        val overlaps = collider.checkCollision().second
        for (c in overlaps) {
            val owner = c.owner as? MainCharacter ?: return
            val target = scene.game.chapterModel.portals[ChapterModel.RoomLoc(scene.roomModel, x, y)]
            for (scene in scene.game.scenes)
                if (scene.roomModel == target?.room) {

                    break
                }
        }
    }

    override fun getRoomCoords(roomGrid: RoomGrid): ArrayList<Coord> {
        val list = ArrayList<Coord>()
        for (x in x until x + width)
            for (y in y until y + height)
                list.add(roomGrid.gameToRoom(x.toDouble(), y.toDouble()))
        return list
    }
}