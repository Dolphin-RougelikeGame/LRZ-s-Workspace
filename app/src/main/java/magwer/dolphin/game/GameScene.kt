package magwer.dolphin.game

import magwer.dolphin.animation.AnimationManager
import magwer.dolphin.api.CollisionScene
import magwer.dolphin.api.RenderedObject
import magwer.dolphin.api.RenderedScene
import magwer.dolphin.game.`object`.GameObject
import magwer.dolphin.graphics.OpenGLView
import magwer.dolphin.physics.Collider
import magwer.dolphin.physics.CollisionRule
import kotlin.concurrent.timerTask

class GameScene(val game: Game) : RenderedScene,
    CollisionScene {

    override val collisionObjects = HashMap<Int, ArrayList<Collider<*>>>()
    override val collisionRule = CollisionRule()
    override val view: OpenGLView
        get() = game.view

    private val gameObjects = ArrayList<GameObject>()
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