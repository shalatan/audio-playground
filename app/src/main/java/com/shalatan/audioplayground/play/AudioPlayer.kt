package com.shalatan.audioplayground.play

import android.net.Uri
import java.io.File

interface AudioPlayer {

    fun playFile(file: File)
    fun playUri(uri: Uri)
    fun stop()
    fun getDuration(): Int?
    fun getCurrentPosition(): Int?
}