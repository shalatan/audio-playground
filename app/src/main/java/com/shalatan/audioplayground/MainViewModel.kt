package com.shalatan.audioplayground

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shalatan.audioplayground.play.AudioPlayerImpl
import com.shalatan.audioplayground.record.AudioRecorderImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import linc.com.amplituda.Amplituda
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val audioPlayerImpl: AudioPlayerImpl,
    private val audioRecorderImpl: AudioRecorderImpl,
    private val amplituda: Amplituda
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
        loadAndProcessAudioFile(_state.value.recordingFile)
    }

    private fun startRecording() {
        File(application.cacheDir, "audio.mp3").also {
            audioRecorderImpl.start(it)
            _state.value = _state.value.copy(isRecording = true, recordingFile = it)
        }
        if (_state.value.isPlaying) {
            stopAudio()
        }
    }

    private fun playAudio() {
        if (_state.value.isPlaying) {   //prohibiting from playing multiple times
            return
        }
        audioPlayerImpl.playFile(state.value.recordingFile ?: return)
        _state.value = _state.value.copy(
            isPlaying = true,
            recordingFileDuration = audioPlayerImpl.getDuration()
        )
    }

    private fun stopAudio() {
        audioPlayerImpl.stop()
        _state.value = _state.value.copy(isPlaying = false)
    }

    private fun loadAndProcessAudioFile(file: File?) {
        val result = amplituda.processAudio(file).get()
        _state.value = _state.value.copy(
            waveformData = result.amplitudesAsList(),
            recordingFileDuration = audioPlayerImpl.getDuration()
        )
        Log.e("SomethingImp", "audioProcessResult: ${result.amplitudesAsList()}")

    }
}