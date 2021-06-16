package magwer.dolphin.game

import android.app.Activity
import magwer.dolphin.R
import magwer.dolphin.game.room.FightingDelegage
import magwer.dolphin.game.sceneobject.FishCharacter
import magwer.dolphin.game.sceneobject.MainCharacter
import magwer.dolphin.game.sceneobject.RoomFloorObject
import magwer.dolphin.game.sceneobject.RoomStaticObject
import magwer.dolphin.graphics.OpenGLView
import magwer.dolphin.sound.SoundManager
import magwer.dolphin.ui.JoyStickControlTouchListener
import magwer.dolphin.ui.LoadingTask
import java.util.*

class Game(
    val activity: Activity,
    private val leftcontroller: JoyStickControlTouchListener
) {

    val scenes = ArrayList<GameScene>()
    val openGLView = activity.findViewById<OpenGLView>(R.id.openGLView)
    val soundManager = SoundManager(this)
    val chapterModel = FightingDelegage().generate()
    val sceneTimer = Timer()
    val loadTask = LoadingTask(this, activity.findViewById(R.id.progress_background))

    val assets
        get() = openGLView.context.assets
    val context
        get() = openGLView.context

    init {
        openGLView.game = this
    }

    fun internal_addScene(scene: GameScene) {
        if (!scenes.contains(scene))
            scenes.add(scene)
    }

    fun loadDone() {
        val scenes = HashMap<Int, GameScene>()
        for ((i, room) in chapterModel.rooms) {
            val scene = GameScene(this)
            scenes[i] = scene
            scene.applyRoomModel(room)
            scene.paused = true
        }
        val initScene = scenes[0]!!
        initScene.applyRoomModel(chapterModel.rooms[0]!!)
        initScene.paused = false
        MainCharacter(
            initScene,
            activity.findViewById(R.id.health_bar),
            leftcontroller,
            27 * 0.5 + 0.5,
            15 * 0.5 + 0.5
        ).addToScene()
        FishCharacter(initScene, 27 * 0.5 + 4.5, 15 * 0.5 + 3.5).addToScene()
        initScene.startTicking()
        openGLView.renderer.viewPort.scale = 1.5f
    }

    fun shutdown() {
        for (scene in scenes)
            scene.shutdown()
    }

}