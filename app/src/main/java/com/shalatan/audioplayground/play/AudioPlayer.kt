package com.shalatan.audioplayground.play

import java.io.File

interface AudioPlayer {

    fun playFile(file:File)
    fun stop()
}