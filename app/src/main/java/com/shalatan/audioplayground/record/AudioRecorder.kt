package com.shalatan.audioplayground.record

import java.io.File

interface AudioRecorder {
    fun start(outputFile: File)
    fun stop()
}