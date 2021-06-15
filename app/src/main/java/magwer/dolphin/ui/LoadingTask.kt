package magwer.dolphin.ui

import android.view.View
import android.widget.ProgressBar
import magwer.dolphin.R
import magwer.dolphin.game.Game
import kotlin.concurrent.timerTask
import kotlin.math.roundToInt

class LoadingTask(
    private val game: Game,
    private val background: View,
    private val bar: ProgressBar = background.findViewById(R.id.progress_bar)
) {

    var openglDone = false
    var musicProcess = 0.0
    var musicDone = false

    fun update() {
        var p = 0.0
        if (openglDone)
            p += 0.5
        p += 0.5 * musicProcess
        bar.progress = (p * 100).roundToInt()
        if (openglDone && musicDone)
            loadDone()
    }

    fun loadDone() {
        var alpha = 1.0
        game.sceneTimer.scheduleAtFixedRate(timerTask {
            alpha -= 0.1
            if (alpha < 0.0) {
                alpha = 0.0
                background.alpha = alpha.toFloat()
                cancel()
                return@timerTask
            }
            background.alpha = alpha.toFloat()
        }, 0L, 50L)
        game.loadDone()
    }

}