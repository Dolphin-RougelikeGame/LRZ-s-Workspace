package magwer.dolphin

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.media.MediaParser
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin

class MainActivity : Activity() {

    val timer = Timer(true)
    val background = MediaPlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        background.setDataSource(assets.openFd("entrance.mp3"))
        background.setVolume(1.0f, 1.0f)
        background.isLooping = true
        background.prepare()
        background.start()

        var clicked = false
        val text = findViewById<TextView>(R.id.start_text)
        text.setShadowLayer(6.0f, 0.0f, 0.0f, Color.WHITE)
        timer.scheduleAtFixedRate(timerTask {
            val a = abs(sin(System.currentTimeMillis() * 0.001))
            text.alpha = a.toFloat()
            if (clicked) {
                var alpha = 1.0
                timer.scheduleAtFixedRate(timerTask {
                    text.alpha = alpha.toFloat()
                    if (alpha <= 0.0) {
                        startGame()
                        cancel()
                    }
                    alpha -= 0.14
                    runOnUiThread {
                        text.scaleX += 0.07f
                    }
                }, 30, 30)
                cancel()
            }
        }, 0, 30)

        val scene = findViewById<ConstraintLayout>(R.id.start_scene)
        scene.setOnClickListener { v ->
            clicked = true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun startGame() {
        var vol = 1.0f
        timer.schedule(timerTask {
            vol -= 0.05f
            runOnUiThread {
                background.setVolume(vol, vol)
            }
            if (vol <= 0.0f) {
                background.stop()
                timer.cancel()
            }
        }, 50, 50)
        startActivity(Intent(this, GameActivity::class.java).also {
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        overridePendingTransition(0, 0)
    }

}