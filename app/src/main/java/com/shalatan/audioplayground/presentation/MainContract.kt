package com.shalatan.audioplayground.presentation

import android.net.Uri
import java.io.File

data class MainScreenState(
    val isPlaying: Boolean = false,
    val isRecording: Boolean = false,
    val recordingFile: File? = null,
    val fileName: String = "_",
    val importedUri: Uri? = null,
    val error: String? = null,
    val waveformData: List<Int> = emptyList(),
    val recordingFileDuration: Int = 0
)

sealed class MainScreenEvents {
    data object StartRecordingClicked : MainScreenEvents()
    data object StopRecordingClicked : MainScreenEvents()
    data object PlayClicked : MainScreenEvents()
    data object StopClicked : MainScreenEvents()
    data object ImportClicked : MainScreenEvents()
    data class UriFetched(val uri: Uri) : MainScreenEvents()
}