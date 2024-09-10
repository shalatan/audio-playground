package com.shalatan.audioplayground

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.linc.audiowaveform.AudioWaveform
import com.shalatan.audioplayground.ui.theme.AudioPlaygroundTheme
import com.shalatan.audioplayground.utils.formatMilliSecondsToMinutes
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

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

    Log.e("Something: ", "currentState: $state")

    var waveformProgress by remember { mutableStateOf(0f) }

    Column(
        modifier = modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Log.e("Something: ", "progress: $waveformProgress")
        AudioWaveform(modifier = Modifier
            .fillMaxSize()
            .height(128.dp)
            .padding(top = 32.dp),
            amplitudes = state.waveformData,
            progress = waveformProgress,
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
                text = if (state.recordingFileDuration != null)
                    state.recordingFileDuration!!.formatMilliSecondsToMinutes()
                else
                    "0:00"
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (state.recordingFileDuration != null) state.recordingFileDuration!!.formatMilliSecondsToMinutes() else "0:00"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
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
                    viewModel.processEvents(MainScreenEvents.StartRecordingClicked)
                }
            }) {
                Text(text = if (state.isRecording) "Stop Recording" else "Start Recording")
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}