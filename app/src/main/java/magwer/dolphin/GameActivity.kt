package magwer.dolphin

import android.app.Activity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import magwer.dolphin.game.Game
import magwer.dolphin.sound.LoopMusic
import magwer.dolphin.ui.JoyStickControlTouchListener
import java.util.*

class GameActivity : Activity() {

    private lateinit var game: Game
    private val timer by lazy { Timer() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val leftcontrol = findViewById<FrameLayout>(R.id.left_control)
        val leftcontrolcenter = findViewById<ImageView>(R.id.left_control_center)
        val leftcontroller = JoyStickControlTouchListener(leftcontrol, leftcontrolcenter)
        leftcontrol.setOnTouchListener(leftcontroller)

        game = Game(this, leftcontroller)

            LoopMusic(
                game.soundManager,
                "airbattlecombat/",
                1.0,
                1,
                10092,
                4536,
                2338,
                6
            )


        //val c = ChapterShapeGenerator(Random())
        //val a = RoomShapeGenerator(c, 50 to 50, 30, 0.6, 1.0, 1.0)
//
        //while (true) {
        //    val success = a.trySpread()
        //    if (!success)
        //        return
        //    val list = c.generateMap(0, 0, 100, 100)
        //    for (line in list)
        //        println(line)
        //    println()
        //    println()
        //}

    }

    override fun onDestroy() {
        super.onDestroy()

        game.shutdown()
    }

}