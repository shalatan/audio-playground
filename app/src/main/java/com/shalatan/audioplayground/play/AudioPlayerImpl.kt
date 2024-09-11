package com.shalatan.audioplayground.play

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.core.net.toUri
import java.io.File

class AudioPlayerImpl(
    private val context: Context
) : AudioPlayer {

    private var player: MediaPlayer? = null
    private var _duration: Int = 0

    override fun playFile(file: File) {
        MediaPlayer.create(context, file.toUri()).apply {
            player = this
            _duration = duration
            setOnCompletionListener {
                onAutoCompleted()
            }
            start()
        }
    }

    var onAutoCompleted: () -> Unit = { }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

    override fun getDuration(): Int {
        return _duration
    }

    override fun getCurrentPosition(): Int {
        return player?.currentPosition ?: 0
    }

}