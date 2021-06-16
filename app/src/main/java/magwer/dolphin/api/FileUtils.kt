package magwer.dolphin.api

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.*
import java.util.*

typealias Coord = Pair<Int, Int>

val Coord.adjacents
    get() = arrayOf(
        Coord(first - 1, second),
        Coord(first, second - 1),
        Coord(first + 1, second),
        Coord(first, second + 1)
    )

val Coord.nearby
    get() = arrayOf(
        Coord(first - 1, second),
        Coord(first, second - 1),
        Coord(first + 1, second),
        Coord(first, second + 1),

        Coord(first - 1, second - 1),
        Coord(first + 1, second - 1),
        Coord(first + 1, second + 1),
        Coord(first - 1, second + 1)
    )

val Coord.conners
    get() = arrayOf(
        Coord(first - 1, second - 1),
        Coord(first + 1, second - 1),
        Coord(first + 1, second + 1),
        Coord(first - 1, second + 1)
    )

fun Coord.distanceSquared(c: Coord): Int {
    val dx = first - c.first
    val dy = second - c.second
    return dx * dx + dy * dy
}

fun clamp(a: Double, min: Double, max: Double): Double {
    if (a < min)
        return min
    if (a > max)
        return max
    return a
}

fun loadBitmapAsset(context: Context, fileName: String): Bitmap {
    var ins: InputStream? = null
    try {
        ins = context.assets.open(fileName)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        val image = BitmapFactory.decodeStream(ins, null, BitmapFactory.Options().also {
            it.outConfig = Bitmap.Config.ARGB_8888
        })
        image!!.setHasAlpha(true)
        ins?.close()
        return image
    }
}

fun loadStringAsset(context: Context, fileName: String): List<String> {
    var ins: InputStream? = null
    var inr: InputStreamReader? = null
    var reader: BufferedReader? = null
    try {
        ins = context.assets.open(fileName)
        inr = InputStreamReader(ins)
        reader = BufferedReader(inr)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        val result = reader?.readLines() ?: listOf()
        reader?.close()
        inr?.close()
        ins?.close()
        return result
    }
}

fun List<String>.join(): String {
    val builder = StringJoiner("\n")
    for (line in this)
        builder.add(line)
    return builder.toString()
}
