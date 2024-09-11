<h1 align="center">Audio Playground</h1>

### Overview
- Project Name: Audio Playground
- Description: A brief description of the project and its purpose.
- Features:
  - Audio recording
  - Audio playback
  - Audio visualization
  - Import audio files

### Architecture
- Architecture: **MVI Architecture**
- Key Components:
  - `MainViewModel`: Manages UI state and processes user events.
  - `MainActivity` : Observes those UI states and updates the screen, build using Compose
  - `AudioPlayerImpl`: Handles audio playback.
  - `AudioRecorderImpl`: Handles audio recording.

### Libraries Used
  - `Amplituda`: To process audio and get array of amplitudes of audio
  - `compose-audiowaveform`: To display the array of amplitudes in the form of wave


## Tasks

### Audio Player Component:
- [x] Implement an audio player that can load and play an audio file (e.g., .mp3, .wav).
- [ ] Integrate a bar visualizer that reacts to the audio being played, updating in real-time.
- [x] Include standard player controls: play, pause, and stop.
- [x] Display the current playback time and total duration of the audio track.
### Audio Recorder Component:
- [x] Implement an audio recorder that can capture sound from the device’s microphone.
- [ ] Display a live bar visualizer that reflects the input audio levels as recording progresses.
- [x] Provide controls to start, pause, and stop the recording.
- [x] Allow users to save the recorded audio file locally on the device.
- [x] Include a playback feature that allows the user to listen to the recorded audio with the visualizer.
