package com.shalatan.audioplayground

import java.io.File

data class MainScreenState(
    val isPlaying: Boolean = false,
    val isRecording: Boolean = false,
    val recordingFile: File? = null,
    val recordingFileDuration: Int? = null,
    val error: String? = null,
    val waveformData: List<Int> = emptyList()
)

sealed class MainScreenEvents {
    data object StartRecordingClicked : MainScreenEvents()
    data object StopRecordingClicked : MainScreenEvents()
    data object PlayClicked : MainScreenEvents()
    data object StopClicked : MainScreenEvents()
}