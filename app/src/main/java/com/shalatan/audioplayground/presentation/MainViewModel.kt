package com.shalatan.audioplayground.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shalatan.audioplayground.play.AudioPlayerImpl
import com.shalatan.audioplayground.record.AudioRecorderImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val _currentDuration = MutableStateFlow(0)
    val currentDuration = _currentDuration.asStateFlow()

    val waveformProgress = currentDuration.map { duration ->
        val totalDuration = state.value.recordingFileDuration
        if (totalDuration > 0) {
            duration.toFloat() / totalDuration.toFloat()
        } else {
            0f
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    init {
        audioPlayerImpl.onAutoCompleted = {
            _state.value = _state.value.copy(isPlaying = false)
        }
        _currentDuration.value = 0
    }

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
        if (_state.value.isPlaying) {   //restricting from playing multiple times
            return
        }
        audioPlayerImpl.playFile(state.value.recordingFile ?: return)
        _state.value = _state.value.copy(
            isPlaying = true, recordingFileDuration = audioPlayerImpl.getDuration()
        )
        observeCurrentPosition()
    }

    private fun observeCurrentPosition() {
        viewModelScope.launch {
            while (_state.value.isPlaying) {
                _currentDuration.value = audioPlayerImpl.getCurrentPosition()
                delay(1000)
            }
        }
    }

    private fun stopAudio() {
        audioPlayerImpl.stop()
        _state.value = _state.value.copy(isPlaying = false)
    }

    private fun loadAndProcessAudioFile(file: File?) {
        viewModelScope.launch {
            try {
                val result = amplituda.processAudio(file).get()
                _state.value = _state.value.copy(
                    waveformData = result.amplitudesAsList()
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}