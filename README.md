# Discord Voice Mod

A Discord-like Android application with advanced voice modification features, inspired by [Aliucord](https://github.com/Aliucord/Aliucord).

## Features

### 🎤 Loud Mic
- **Audio Amplification**: Boost your microphone input up to 3x the normal volume
- **Real-time Processing**: Instant audio amplification with no delay
- **Adjustable Levels**: Fine-tune amplification from 0% to 300% using a slider
- **Toggle Control**: Enable/disable loud mic feature on the fly

### 🎭 Voice Changer
- **Multiple Voice Effects**:
  - **Normal**: Standard voice passthrough
  - **Deep Voice**: Lower pitch for a deeper, more resonant sound
  - **High Voice**: Higher pitch for a chipmunk-like effect
  - **Robot**: Quantized audio for a robotic sound
  - **Echo**: Add echo effect with customizable delay
  - **Reverb**: Multi-delayed reverb for spatial audio effect

- **Pitch Shifting**: Continuously adjust pitch from 0.5x to 2.0x
- **Real-time Processing**: All effects applied in real-time

## Technical Details

### Audio Processing
- **Sample Rate**: 44.1 kHz (CD quality)
- **Encoding**: PCM 16-bit
- **Channel**: Mono
- **Low Latency**: Optimized for real-time voice processing

### Architecture
```
app/
├── src/main/
│   ├── java/com/discord/voicemod/
│   │   ├── MainActivity.java          # Main UI controller
│   │   └── VoiceProcessor.java        # Audio processing engine
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml      # Main UI layout
│   │   ├── values/
│   │   │   ├── strings.xml            # String resources
│   │   │   ├── colors.xml             # Color palette
│   │   │   └── themes.xml             # App theme
│   │   └── drawable/
│   │       └── ic_launcher.xml        # App icon
│   └── AndroidManifest.xml            # App configuration
└── build.gradle                        # App dependencies
```

## Requirements

- **Android SDK**: API 24+ (Android 7.0 Nougat or higher)
- **Target SDK**: API 33 (Android 13)
- **Permissions**:
  - `RECORD_AUDIO` - Required for microphone access
  - `MODIFY_AUDIO_SETTINGS` - Required for audio modifications
  - `INTERNET` - For future network features
  - `ACCESS_NETWORK_STATE` - For connection status

## Building the App

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 8 or higher
- Android SDK with API 33

### Build Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/Tanmayop9/a-discord-.git
   cd a-discord-
   ```

2. Open in Android Studio:
   - File → Open → Select the project directory

3. Sync Gradle:
   - Click "Sync Project with Gradle Files" or wait for auto-sync

4. Build the APK:
   ```bash
   ./gradlew assembleDebug
   ```
   Or use Android Studio: Build → Build Bundle(s) / APK(s) → Build APK(s)

5. Install on device:
   ```bash
   ./gradlew installDebug
   ```
   Or use Android Studio: Run → Run 'app'

## Usage

1. **Grant Permissions**: On first launch, grant microphone and audio modification permissions
2. **Enable Loud Mic**: 
   - Adjust the amplification slider (0-300%)
   - Toggle the "Enable" switch
3. **Apply Voice Effects**:
   - Select an effect from the dropdown menu
   - Adjust pitch shift slider for custom pitch
   - Toggle the "Enable" switch
4. **Start Processing**: Tap "Start Recording" to begin real-time voice processing
5. **Stop Processing**: Tap "Stop Recording" to end voice processing

## Voice Effects Explained

- **Normal**: No modification, with optional pitch adjustment
- **Deep Voice**: Reduces pitch by 30% for a deeper tone
- **High Voice**: Increases pitch by 50% for a higher tone
- **Robot**: Quantizes audio signal for mechanical sound
- **Echo**: Adds 250ms delayed echo with 50% decay
- **Reverb**: Applies multiple echoes at 100ms, 50ms, and 33ms intervals

## Safety Features

- **Audio Clipping Prevention**: Automatic limiting to prevent distortion
- **Resource Management**: Proper cleanup of audio resources
- **Background Handling**: Automatically stops processing when app is backgrounded
- **Permission Checks**: Runtime permission verification before accessing microphone

## Inspired By

This project is inspired by [Aliucord](https://github.com/Aliucord/Aliucord), an Android Discord client mod that adds various features and enhancements to the Discord mobile app.

## License

This project is open source and available for educational purposes.

## Disclaimer

This is an independent project and is not affiliated with Discord Inc. Use responsibly and in accordance with Discord's Terms of Service and Community Guidelines.

## Future Enhancements

- [ ] Network voice streaming
- [ ] Recording and playback
- [ ] More advanced voice effects (vocoder, autotune)
- [ ] Noise suppression and echo cancellation
- [ ] Custom effect presets
- [ ] Background service for persistent processing
- [ ] Integration with Discord voice channels

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## Support

For issues or questions, please open an issue on the [GitHub repository](https://github.com/Tanmayop9/a-discord-/issues).