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
            start()
        }
    }

    override fun stop() {
        player?.stop()
        player?.release()
        player = null
    }

    override fun getDuration(): Int {
        Log.e("Something: ", "AudioDuration: ${_duration}")
        return _duration
    }
}