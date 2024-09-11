package com.shalatan.audioplayground.presentation

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shalatan.audioplayground.play.AudioPlayerImpl
import com.shalatan.audioplayground.record.AudioRecorderImpl
import dagger.hilt.android.lifecycle.HiltViewModel
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
            MainScreenEvents.ImportClicked -> clearRecordedAudio()
            is MainScreenEvents.UriFetched -> handleImportedUri(events.uri)
        }
    }

    private fun clearRecordedAudio() {
        if (state.value.isPlaying) {
            audioPlayerImpl.stop()
        }
        _state.value =
            _state.value.copy(isPlaying = false, recordingFile = null, isRecording = false)
    }

    private fun handleImportedUri(uri: Uri) {
        val fileName = uri.path.toString().substring(uri.path.toString().lastIndexOf("/") + 1)
        _state.value = _state.value.copy(
            importedUri = uri,
            recordingFileDuration = 0,
            fileName = fileName
        )
        _currentDuration.value = 0
        processUriAudio(uri)
    }

    private fun stopRecording() {
        audioRecorderImpl.stop()
        _state.value = _state.value.copy(isRecording = false)
        _currentDuration.value = 0
        loadAndProcessAudioFile(_state.value.recordingFile)
    }

    private fun startRecording() {
        File(application.cacheDir, "recording.mp3").also {
            audioRecorderImpl.start(it)
            _state.value = _state.value.copy(
                isRecording = true,
                recordingFile = it,
                fileName = it.name,
                importedUri = null,
                isPlaying = false,
                recordingFileDuration = 0
            )
            _currentDuration.value = 0
        }
    }

    private fun playAudio() {
        if (_state.value.isPlaying) {   //restricting from playing multiple times
            return
        }
        state.value.recordingFile?.let {
            audioPlayerImpl.playFile(it)
        }
        state.value.importedUri?.let {
            audioPlayerImpl.playUri(it)
        }
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

    private fun processUriAudio(uri: Uri) {
        val file = uriToFile(uri, context = application)
        viewModelScope.launch {
            try {
                val result = amplituda.processAudio(file).get()
                Log.d("Something", "processUriAudio: ${result.amplitudesAsList()}")
                _state.value = _state.value.copy(
                    waveformData = result.amplitudesAsList()
                )
            } catch (e: Exception) {
                Log.e("SomethingError", "processUriAudio: $e")
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File? {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndex("_data")
            if (columnIndex != -1 && it.moveToFirst()) {
                val filePath = it.getString(columnIndex)
                return File(filePath)
            }
        }
        return null
    }
}