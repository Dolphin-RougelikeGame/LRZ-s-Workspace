package magwer.dolphin.graphics

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import magwer.dolphin.api.loadBitmapAsset
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer(private val view: OpenGLView) : GLSurfaceView.Renderer {

    private val shapes = ArrayList<GLShape>()
    var ratio = 1.0f
        private set
    val viewPort = OpenGLViewport()

    fun addShape(shape: GLShape) {
        synchronized(shapes) {
            if (shapes.contains(shape))
                return
            shapes.add(shape)
        }
        shape.onRatioChange(ratio)
    }

    fun removeShape(shape: GLShape) {
        synchronized(shapes) {
            shapes.remove(shape)
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        view.shaderProgram
        view.onLoaded()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        ratio = width / height.toFloat()
        for (shape in shapes)
            shape.onRatioChange(ratio)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glClearColor(0.0f, 0.0f, 0f, 1.0f)
        synchronized(shapes) {
            for (shape in shapes)
                shape.draw(viewPort)
        }
    }

}