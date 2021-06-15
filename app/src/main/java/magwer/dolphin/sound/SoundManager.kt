package magwer.dolphin.sound

import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import magwer.dolphin.game.Game
import java.util.*

class SoundManager(val game: Game) {

    val timer = Timer()
    val soundsToLoad = ArrayList<Int>()
    val soundBuffer = HashMap<String, Int>()
    val soundPool = SoundPool.Builder().setMaxStreams(64).setAudioAttributes(
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
    ).build()
    var currentMusic: Music? = null
    var nextMusic: Music? = null

    init {
        soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
            soundsToLoad.remove(sampleId)
            game.loadTask.musicProcess = 1.0 / (soundsToLoad.size + 1)
            if (soundsToLoad.isEmpty())
                game.loadTask.musicDone = true
            game.loadTask.update()
        }
    }

    fun loadSound(soundPath: String) {
        if (soundBuffer.containsKey(soundPath))
            return
        val id = soundPool.load(game.assets.openFd(soundPath), 1)
        soundsToLoad.add(id)
        soundBuffer[soundPath] = id
        id
    }

    fun playSound(soundPath: String, disX: Double, disY: Double, strength: Double) {
        val id = soundBuffer[soundPath] ?: let {
            val id = soundPool.load(game.assets.openFd(soundPath), 1)
            soundsToLoad.add(id)
            soundBuffer[soundPath] = id
            id
        }
        val lv = strength * FADE_FACTOR / disX
        val rv = strength * FADE_FACTOR / disY
        soundPool.play(id, lv.toFloat(), rv.toFloat(), 1, 0, 1.0f)
    }

    fun playSound(soundPath: String, volume: Float) {
        val id = soundBuffer[soundPath] ?: let {
            val id = soundPool.load(game.assets.openFd(soundPath), 1)
            soundsToLoad.add(id)
            soundBuffer[soundPath] = id
            id
        }
        soundPool.play(id, volume, volume, 1, 0, 1.0f)
    }

    fun setMusic(music: Music) {
        synchronized(this) {
            if (currentMusic == null) {
                currentMusic = music
                music.start()
                return
            }
            if (currentMusic == music) {
                if (currentMusic!!.stopping)
                    if (!currentMusic!!.resume())
                        nextMusic = currentMusic
                return
            }
            if (currentMusic!!.stopping)
                nextMusic = music
            else {
                nextMusic = music
                currentMusic?.stop()
            }
        }
    }

    fun stopMusic() {
        synchronized(this) {
            currentMusic?.stop()
            nextMusic = null
        }
    }

    fun shutdown() {
        timer.cancel()
        currentMusic?.shutdown()
        nextMusic = null
    }

    companion object {

        val FADE_FACTOR = 20

    }

}