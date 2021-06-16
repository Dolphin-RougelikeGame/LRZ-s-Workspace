package magwer.dolphin.game

import magwer.dolphin.animation.AnimationManager
import magwer.dolphin.api.CollisionScene
import magwer.dolphin.api.RenderedObject
import magwer.dolphin.api.RenderedScene
import magwer.dolphin.game.sceneobject.GameObject
import magwer.dolphin.game.room.RoomGrid
import magwer.dolphin.game.room.RoomModel
import magwer.dolphin.game.room.RoomRemodel
import magwer.dolphin.game.sceneobject.RoomStaticObject
import magwer.dolphin.game.sceneobject.RoomFloorObject
import magwer.dolphin.graphics.OpenGLView
import magwer.dolphin.physics.Collider
import magwer.dolphin.physics.CollisionRule
import kotlin.concurrent.timerTask

class GameScene(val game: Game) : RenderedScene,
    CollisionScene {

    override val collisionObjects = HashMap<Int, ArrayList<Collider<*>>>()
    override val collisionRule = CollisionRule()
    override val view: OpenGLView
        get() = game.openGLView

    lateinit var roomModel: RoomModel
    private val gameObjects = ArrayList<GameObject>()
    val roomGrid = RoomGrid(0, 0)
    val animationManager = AnimationManager()

    var paused = false
        set(value) {
            if (field == value)
                return
            field = value
            animationManager.paused = value
            if (field) {
                synchronized(gameObjects) {
                    for (obj in gameObjects)
                        if (obj is RenderedObject)
                            view.renderer.removeShape(obj.glShape)
                }
            } else {
                synchronized(gameObjects) {
                    for (obj in gameObjects)
                        if (obj is RenderedObject)
                            view.renderer.addShape(obj.glShape)
                }
            }
        }

    val context
        get() = view.context

    init {
        game.internal_addScene(this)
        collisionRule.setRule(CollisionRule.PLAYER_CHANNEL, CollisionRule.STATIC_CHANNEL, CollisionRule.Rule.BLOCK)
        collisionRule.setRule(CollisionRule.PLAYER_CHANNEL, CollisionRule.PHYSICAL_CHANNEL, CollisionRule.Rule.BLOCK)
        collisionRule.setRule(CollisionRule.PLAYER_CHANNEL, CollisionRule.SPIRIT_CHANNEL, CollisionRule.Rule.OVERLAP)
        collisionRule.setRule(CollisionRule.PLAYER_CHANNEL, CollisionRule.VISION_CHANNEL, CollisionRule.Rule.OVERLAP)
        collisionRule.setRule(CollisionRule.PLAYER_CHANNEL, CollisionRule.INTERACT_CHANNEL, CollisionRule.Rule.OVERLAP)
        collisionRule.setRule(CollisionRule.STATIC_CHANNEL, CollisionRule.PHYSICAL_CHANNEL, CollisionRule.Rule.BLOCK)
    }

    private fun onTick(deltaTime: Long) {
        val currentobjects = synchronized(gameObjects) {
            val list = ArrayList<GameObject>()
            list.addAll(gameObjects)
            list
        }
        for (obj in currentobjects)
            obj.onTick(deltaTime)
    }

    fun internal_addGameObject(obj: GameObject) {
        synchronized(gameObjects) {
            if (!gameObjects.contains(obj))
                gameObjects.add(obj)
            if (obj is RenderedObject)
                view.renderer.addShape(obj.glShape)
        }
    }

    fun internal_removeGameObject(obj: GameObject) {
        synchronized(gameObjects) {
            gameObjects.remove(obj)
            if (obj is RenderedObject)
                view.renderer.removeShape(obj.glShape)
        }
    }

    fun applyRoomModel(roomModel: RoomModel) {
        this.roomModel = roomModel
        for (x in 0 until roomModel.width)
            for (y in 0 until roomModel.height) {
                when (roomModel.matrix[x][y]) {
                    RoomRemodel.SlotType.WALL -> {
                        RoomStaticObject(this, x, y, 1, 1, "texture/wall.png").addToScene()
                    }
                    RoomRemodel.SlotType.FLOOR -> {
                        RoomFloorObject(this, x, y, 1, 1, "texture/sea.png").addToScene()
                    }
                    RoomRemodel.SlotType.TREASURE -> {

                    }
                    RoomRemodel.SlotType.BAR -> {
                        RoomStaticObject(this, x, y, 1, 1, "texture/bar.png").addToScene()
                    }
                    RoomRemodel.SlotType.PORTAL -> {

                    }
                }
            }
    }

    fun startTicking() {
        var lastTick = System.currentTimeMillis()
        game.sceneTimer.scheduleAtFixedRate(timerTask {
            val old = lastTick
            lastTick = System.currentTimeMillis()
            if (paused)
                return@timerTask
            onTick(lastTick - old)
        }, 0L, 50L)
    }

    fun shutdown() {
        synchronized(gameObjects) {
            for (obj in gameObjects)
                if (obj is RenderedObject)
                    view.renderer.removeShape(obj.glShape)
        }
        game.sceneTimer.cancel()
        animationManager.shutdown()
    }

}