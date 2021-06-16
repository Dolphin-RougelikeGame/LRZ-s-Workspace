package magwer.dolphin.graphics

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import magwer.dolphin.animation.BitmapHolder
import kotlin.math.cos
import kotlin.math.sin

class GLSquare(
    private val shaderProgram: Int,
    val texture: BitmapHolder,
    var screenX: Float,
    var screenY: Float,
    var screenWidth: Float,
    var screenHeight: Float,
    var tintColor: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)
) : GLShape {

    var rotationSin = 0.0f
    var rotationCos = 1.0f

    private var initialized = false
    private var ratio = 1.0f
    private val vertexBuffer = FloatBuffer(getCoords())
    private val textureBuffer = FloatBuffer(textureData)
    private val rotationBuffer = FloatBuffer(getRotation())
    private var vertexHandle = 0
    private var texturePosHandle = 0
    private var offsetHandle = 0
    private var viewportHandle = 0
    private var ratioHandle = 0
    private var rotationHandle = 0
    private var tintColorHandle = 0
    private var textureId = 0
    private var oldBitmap: Bitmap? = null

    private fun getCoords(): FloatArray {
        val w = screenWidth * 0.5f
        val h = screenHeight * 0.5f
        return floatArrayOf(
            -w, h,  // top left
            -w, -h,  // bottom left
            w, h, // top right
            w, -h  // bottom right
        )
    }

    private fun getRotation(): FloatArray {
        return floatArrayOf(rotationCos, -rotationSin, rotationSin, rotationCos)
    }

    fun updateCoords() {
        vertexBuffer.put(getCoords())
        vertexBuffer.position(0)
    }

    fun updateRotation() {
        rotationBuffer.put(getRotation())
        rotationBuffer.position(0)
    }

    override fun onRatioChange(ratio: Float) {
        this.ratio = ratio
        updateCoords()
    }

    override fun draw(viewPort: OpenGLViewport) {
        if (!initialized) {
            initialized = true
            vertexHandle = GLES20.glGetAttribLocation(shaderProgram, "av_Position")
            texturePosHandle = GLES20.glGetAttribLocation(shaderProgram, "af_Position")
            offsetHandle = GLES20.glGetUniformLocation(shaderProgram, "av_Offset")
            viewportHandle = GLES20.glGetUniformLocation(shaderProgram, "viewport")
            ratioHandle = GLES20.glGetUniformLocation(shaderProgram, "ratio")
            rotationHandle = GLES20.glGetUniformLocation(shaderProgram, "rotationMatrix")
            tintColorHandle = GLES20.glGetUniformLocation(shaderProgram, "tintColor")
            textureId = run {
                val textures = intArrayOf(0)
                GLES20.glGenTextures(1, textures, 0)
                val id = textures[0]
                if (id == 0)
                    return@run id
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_REPEAT
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_REPEAT
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR
                )
                GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR
                )
                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture.bitmap, 0)
                id
            }
            return
        }
        GLES20.glUseProgram(shaderProgram)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        val newbitmap = texture.bitmap
        if (oldBitmap != newbitmap) {
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture.bitmap, 0)
            oldBitmap = newbitmap
        }

        GLES20.glEnableVertexAttribArray(vertexHandle)
        GLES20.glVertexAttribPointer(
            vertexHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
            false, VERTEX_STRIDE, vertexBuffer
        )

        GLES20.glEnableVertexAttribArray(texturePosHandle)
        GLES20.glVertexAttribPointer(
            texturePosHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
            false, VERTEX_STRIDE, textureBuffer
        )

        GLES20.glUniform2f(offsetHandle, screenX + screenWidth * 0.5f, screenY + screenHeight * 0.5f)
        GLES20.glUniform1f(ratioHandle, ratio)
        GLES20.glUniform3f(viewportHandle, viewPort.transX, viewPort.transY * ratio, viewPort.scale)
        GLES20.glUniformMatrix2fv(rotationHandle, 1, false, rotationBuffer)
        GLES20.glUniform4f(tintColorHandle, tintColor[0], tintColor[1], tintColor[2], tintColor[3])

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, VERTICES)

        GLES20.glDisableVertexAttribArray(vertexHandle)
        GLES20.glDisableVertexAttribArray(texturePosHandle)
    }

    companion object {

        private const val VERTICES = 4
        private const val COORDS_PER_VERTEX = 2
        private const val VERTEX_STRIDE = COORDS_PER_VERTEX * 4
        private val textureData = floatArrayOf(
            1f, 0f,
            1f, 1f,
            0f, 0f,
            0f, 1f
        )

    }

}