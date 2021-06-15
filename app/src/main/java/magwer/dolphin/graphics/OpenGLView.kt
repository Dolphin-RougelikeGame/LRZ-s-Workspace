package magwer.dolphin.graphics

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder
import magwer.dolphin.game.Game

class OpenGLView(context: Context, attrs: AttributeSet? = null) : GLSurfaceView(context, attrs) {

    lateinit var game: Game
    val renderer: OpenGLRenderer
    val shaderProgram by lazy {
        createShaderProgram(
            context,
            "graphics/gl_basic.vsh",
            "graphics/gl_basic.fsh"
        )
    }

    init {
        setEGLContextClientVersion(2)
        renderer = OpenGLRenderer(this)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun onLoaded() {
        game.loadTask.openglDone = true
        game.loadTask.update()
    }

}