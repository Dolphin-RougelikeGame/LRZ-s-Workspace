package magwer.dolphin.game

import android.app.Activity
import magwer.dolphin.R
import magwer.dolphin.game.sceneobject.MainCharacter
import magwer.dolphin.graphics.OpenGLView
import magwer.dolphin.sound.SoundManager
import magwer.dolphin.ui.JoyStickControlTouchListener
import magwer.dolphin.ui.LoadingTask
import java.util.*

class Game(
    activity: Activity,
    private val leftcontroller: JoyStickControlTouchListener
) {

    private val scenes = ArrayList<GameScene>()
    val openGLView = activity.findViewById<OpenGLView>(R.id.openGLView)
    val soundManager = SoundManager(this)
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

        val initialScene = GameScene(this)
        MainCharacter(
            initialScene,
            leftcontroller
        ).addToScene()
        TempBoxCharacter(initialScene, 0, 0).addToScene()
        TempBoxCharacter(initialScene, 0, 1).addToScene()
        TempBoxCharacter(initialScene, 1, 1).addToScene()
        TempBoxCharacter(initialScene, 1, 2).addToScene()
        initialScene.startTicking()
        openGLView.renderer.viewPort.scale = 0.5f
    }

    fun shutdown() {
        for (scene in scenes)
            scene.shutdown()
    }

}