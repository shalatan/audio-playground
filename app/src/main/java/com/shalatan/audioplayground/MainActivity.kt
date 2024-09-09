package com.shalatan.audioplayground

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.shalatan.audioplayground.play.AudioPlayerImpl
import com.shalatan.audioplayground.record.AudioRecorderImpl
import com.shalatan.audioplayground.ui.theme.AudioPlaygroundTheme
import java.io.File

class MainActivity : ComponentActivity() {

    private val recorder by lazy {
        AudioRecorderImpl(applicationContext)
    }

    private val player by lazy {
        AudioPlayerImpl(applicationContext)
    }

    private var audioFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )
        enableEdgeToEdge()
        setContent {
            AudioPlaygroundTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
//                        AudioRecorderHalf(
//                            modifier = Modifier,
//                            recorder = recorder,
//                            audioFile = audioFile
//                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Button(onClick = {
                                File(cacheDir, "audio.mp3").also {
                                    recorder.start(it)
                                    audioFile = it
                                }
                            }) {
                                Text(text = "Start Recording")
                            }
                            Button(onClick = {
                                recorder.stop()
                            }) {
                                Text(text = "Stop Recording")
                            }
                            Button(onClick = {
                                player.playFile(audioFile ?: return@Button)
                            }) {
                                Text(text = "Play")
                            }
                            Button(onClick = {
                                player.stop()
                            }) {
                                Text(text = "Stop")
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AudioRecorderHalf(
    modifier: Modifier = Modifier,
    recorder: AudioRecorderImpl,
    audioFile: File?
) {
    val context = LocalContext.current

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AudioPlaygroundTheme {
//        AudioRecorderHalf()
    }
}