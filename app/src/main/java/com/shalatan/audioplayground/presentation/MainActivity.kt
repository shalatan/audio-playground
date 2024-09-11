package com.shalatan.audioplayground.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.linc.audiowaveform.AudioWaveform
import com.shalatan.audioplayground.R
import com.shalatan.audioplayground.ui.theme.AudioPlaygroundTheme
import com.shalatan.audioplayground.utils.formatMilliSecondsToMinutes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.RECORD_AUDIO), 0
        )
        enableEdgeToEdge()
        setContent {
            AudioPlaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val viewModel: MainViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()
    val currentDuration by viewModel.currentDuration.collectAsState()
    val waveformState by viewModel.waveformProgress.collectAsState()

    Log.e("Something: ", "currentState: $state")
    Log.e("Something: ", "waveFormState: $waveformState")

    var waveformProgress by remember { mutableFloatStateOf(waveformState) }

    Column(
        modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier.padding(bottom = 32.dp), text = "Name: ${state.recordingFile?.name}"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(enabled = state.recordingFile != null && !state.isRecording, onClick = {
                viewModel.processEvents(MainScreenEvents.PlayClicked)
            }) {
                Text(text = "Play")
            }
            Button(onClick = {
                viewModel.processEvents(MainScreenEvents.StopClicked)
            }) {
                Text(text = "Stop")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Log.e("Something: ", " waveform: $waveformProgress")
        AudioWaveform(modifier = Modifier
            .fillMaxSize()
            .height(128.dp)
            .padding(top = 32.dp),
            amplitudes = state.waveformData,
            progress = waveformState,
            progressBrush = SolidColor(Color.Magenta),
            waveformBrush = SolidColor(Color.LightGray),
            onProgressChange = { waveformProgress = it })
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currentDuration.formatMilliSecondsToMinutes()
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = state.recordingFileDuration.formatMilliSecondsToMinutes()
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        AudioRecordingScreen(state = state, viewModel = viewModel)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun AudioRecordingScreen(
    modifier: Modifier = Modifier, state: MainScreenState, viewModel: MainViewModel
) {
    val context = LocalContext.current

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.processEvents(MainScreenEvents.StartRecordingClicked)
        } else {
            Toast.makeText(context, "Permission denied for audio recording", Toast.LENGTH_LONG)
                .show()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = R.drawable.ic_mic),
            contentDescription = "mic",
            tint = if (state.isRecording) Color.Green else Color.LightGray
        )

        Button(modifier = Modifier, onClick = {
            if (state.isRecording) {
                viewModel.processEvents(MainScreenEvents.StopRecordingClicked)
            } else {
                //check if permission granted before recording
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.processEvents(MainScreenEvents.StartRecordingClicked)
                } else {
                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }) {
            Text(text = if (state.isRecording) "Stop Recording" else "Start Recording")
        }
    }
}