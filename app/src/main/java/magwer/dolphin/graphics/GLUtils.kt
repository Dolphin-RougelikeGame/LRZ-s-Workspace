package magwer.dolphin.graphics

import android.content.Context
import android.opengl.GLES20
import magwer.dolphin.api.join
import magwer.dolphin.api.loadStringAsset
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.sqrt

fun getAngle(dx: Double, dy: Double): Pair<Double, Double> {
    if (dx == 0.0 && dy == 0.0)
        return 0.0 to 1.0
    val len = sqrt(dx * dx + dy * dy)
    return dx / len to -dy / len
}

fun createShaderProgram(context: Context, vertexShaderFile: String, fragmentShaderFile: String): Int {
    val vsc = loadStringAsset(context, vertexShaderFile).join()
    val fsc = loadStringAsset(context, fragmentShaderFile).join()
    val program = GLES20.glCreateProgram()
    GLES20.glAttachShader(program,
        loadVertexShader(vsc)
    )
    GLES20.glAttachShader(program,
        loadFragmentShader(fsc)
    )
    GLES20.glLinkProgram(program)
    return program
}

fun createShaderProgram(vertexShaderCode: String, fragmentShaderCode: String): Int {
    val program = GLES20.glCreateProgram()
    GLES20.glAttachShader(program,
        loadVertexShader(vertexShaderCode)
    )
    GLES20.glAttachShader(program,
        loadFragmentShader(fragmentShaderCode)
    )
    GLES20.glLinkProgram(program)
    return program
}

fun loadVertexShader(code: String): Int {
    return loadShader(GLES20.GL_VERTEX_SHADER, code)
}

fun loadFragmentShader(code: String): Int {
    return loadShader(GLES20.GL_FRAGMENT_SHADER, code)
}

fun loadShader(type: Int, code: String): Int {
    val shader = GLES20.glCreateShader(type)
    GLES20.glShaderSource(shader, code)
    GLES20.glCompileShader(shader)
    return shader
}

fun FloatBuffer(array: FloatArray): FloatBuffer {
    val bb: ByteBuffer = ByteBuffer.allocateDirect(array.size * 4)
    bb.order(ByteOrder.nativeOrder())
    val fbuffer = bb.asFloatBuffer()
    fbuffer.put(array)
    fbuffer.position(0)
    return fbuffer
}