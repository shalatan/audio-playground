package com.shalatan.audioplayground

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.shalatan.audioplayground.play.AudioPlayerImpl
import com.shalatan.audioplayground.record.AudioRecorderImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val audioPlayerImpl: AudioPlayerImpl,
    private val audioRecorderImpl: AudioRecorderImpl
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(MainScreenState())
    val state = _state.asStateFlow()

    fun processEvents(events: MainScreenEvents) {
        when (events) {
            MainScreenEvents.StartRecordingClicked -> startRecording()
            MainScreenEvents.StopRecordingClicked -> stopRecording()
            MainScreenEvents.StopClicked -> stopAudio()
            MainScreenEvents.PlayClicked -> playAudio()
        }
    }

    private fun stopRecording() {
        audioRecorderImpl.stop()
        _state.value = _state.value.copy(isRecording = false)

    }

    private fun startRecording() {
        File(application.cacheDir, "audio.mp3").also {
            audioRecorderImpl.start(it)
            _state.value = _state.value.copy(isRecording = true, recordingFile = it)
        }
    }

    private fun playAudio() {
        audioPlayerImpl.playFile(state.value.recordingFile ?: return)
        _state.value = _state.value.copy(isPlaying = true)
    }

    private fun stopAudio() {
        audioPlayerImpl.stop()
        _state.value = _state.value.copy(isPlaying = false)
    }
}