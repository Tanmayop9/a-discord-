# Features Documentation

## Overview
Discord Voice Mod is an Android application that provides real-time voice modification capabilities, perfect for gaming, streaming, or just having fun with friends.

## Core Features

### 1. Loud Mic (Audio Amplification)

The Loud Mic feature allows you to boost your microphone input volume beyond the normal limits.

**Key Capabilities:**
- **Amplification Range**: 0% to 300% of normal volume
- **Real-time Processing**: No noticeable delay
- **Clipping Prevention**: Automatic limiting to prevent audio distortion
- **Fine Control**: Smooth slider for precise adjustment

**Use Cases:**
- Boost quiet microphones
- Compensate for low-gain audio setups
- Make sure you're heard in noisy environments
- Gaming with friends who have trouble hearing you

**Technical Implementation:**
```java
// Amplification is applied as a multiplier
amplifiedSample = originalSample * amplificationLevel;
// With automatic clipping prevention
if (amplifiedSample > Short.MAX_VALUE) {
    amplifiedSample = Short.MAX_VALUE;
}
```

### 2. Voice Changer

Transform your voice with various effects and pitch modifications.

#### Available Effects:

##### Normal
- Standard voice passthrough
- Optional pitch adjustment
- Baseline for all other effects

##### Deep Voice
- Reduces pitch by 30%
- Creates a deeper, more resonant sound
- Great for dramatic effect or privacy

##### High Voice
- Increases pitch by 50%
- Creates a chipmunk-like effect
- Fun for comedy or disguising your voice

##### Robot
- Quantizes audio signal
- Creates a mechanical, robotic sound
- Distinctive and futuristic

##### Echo
- Adds 250ms delayed echo
- 50% decay factor
- Creates a spacious, reverberant effect

##### Reverb
- Multiple delayed echoes at 100ms, 50ms, and 33ms
- Simulates room acoustics
- Adds depth and richness to voice

#### Pitch Shifting
- **Range**: 0.5x to 2.0x of original pitch
- **Continuous Control**: Smooth slider adjustment
- **Real-time Application**: Instant feedback
- **Works With All Effects**: Combine with other effects for unique sounds

### 3. Noise Suppression

Advanced noise suppression to remove background noise from your audio.

**Key Capabilities:**
- **Noise Gate**: Intelligently removes low-level background noise
- **Adjustable Threshold**: Customizable noise detection sensitivity
- **Real-time Processing**: No perceptible delay
- **Preserves Voice Quality**: Only removes unwanted noise

**Use Cases:**
- Remove keyboard typing sounds
- Eliminate fan or air conditioning noise
- Clean up audio in noisy environments
- Professional-quality voice chat

**Technical Details:**
- Energy-based noise detection
- Configurable threshold levels
- Works in combination with other effects

### 4. Voice Activity Detection (VAD)

Automatically detect when you're speaking and mute when you're silent.

**Key Capabilities:**
- **Automatic Muting**: Only transmits audio when voice is detected
- **Visual Feedback**: Real-time status indicator (Speaking/Silent)
- **Energy-based Detection**: Uses RMS (Root Mean Square) analysis
- **Low Latency**: Fast response time

**Use Cases:**
- Reduce bandwidth usage
- Automatic push-to-talk alternative
- Privacy protection when not speaking
- Cleaner voice communication

**Technical Implementation:**
```java
// Calculate RMS energy
double rms = Math.sqrt((double) sum / length);
// Voice is active if RMS exceeds threshold
isVoiceActive = rms > vadThreshold;
```

### 5. Audio Visualization

Real-time visual feedback of your audio levels.

**Key Capabilities:**
- **Real-time Display**: Shows current audio amplitude
- **Visual Feedback**: See when you're speaking
- **Discord-themed Design**: Matches the app aesthetic
- **Low Overhead**: Minimal performance impact

**Use Cases:**
- Monitor microphone input levels
- Visual confirmation of voice activity
- Adjust settings based on visual feedback
- Ensure audio is being processed

### 6. Voice Effect Presets

Save and load your favorite voice configurations.

**Key Capabilities:**
- **Save Presets**: Store complete voice configurations
- **Load Presets**: Quickly switch between saved settings
- **Persistent Storage**: Presets saved across app restarts
- **JSON-based**: Easy to backup and share

**Preset Includes:**
- Amplification level
- Pitch shift value
- Selected voice effect
- Feature toggles (Loud Mic, Voice Changer, Noise Suppression, VAD)

**Use Cases:**
- Quick switching between different voice profiles
- Save favorite configurations for different games
- Share presets with friends
- Experiment without losing good settings

### 7. Background Service

Continue voice processing even when the app is in the background.

**Key Capabilities:**
- **Foreground Service**: Persistent voice processing
- **Notification Control**: Quick access from notification tray
- **Battery Optimized**: Efficient power usage
- **Reliable**: Won't be killed by system

**Use Cases:**
- Use voice effects while gaming
- Switch between apps without losing voice processing
- Long voice chat sessions
- Multi-tasking while maintaining voice effects

### 8. Real-time Processing

All audio processing happens in real-time with minimal latency.

**Performance Characteristics:**
- **Sample Rate**: 44,100 Hz (CD quality)
- **Bit Depth**: 16-bit PCM
- **Latency**: < 50ms typical
- **CPU Usage**: Optimized for mobile devices
- **Memory**: Efficient buffer management

### 4. User Interface

Modern Material Design interface with Discord-inspired theming.

**UI Components:**
- **Effect Cards**: Clear separation of Loud Mic and Voice Changer
- **Sliders**: Visual feedback for amplification and pitch levels
- **Switches**: Easy toggle for features
- **Dropdown**: Quick selection of voice effects
- **Recording Button**: Start/stop voice processing

**Design Principles:**
- Dark theme for comfortable use
- Discord color scheme (Blurple accent)
- Clear visual hierarchy
- Responsive controls
- Material Design 3 components

## Permission Management

### Required Permissions

1. **RECORD_AUDIO**
   - Access to device microphone
   - Required for all voice features
   - Requested at runtime

2. **MODIFY_AUDIO_SETTINGS**
   - Adjust audio system settings
   - Enable audio routing
   - Optimize for voice processing

3. **INTERNET** (for future features)
   - Network voice streaming (planned)
   - Online features (planned)

4. **ACCESS_NETWORK_STATE**
   - Check connection status
   - Optimize for network conditions

### Permission Flow
1. App checks for permissions on startup
2. Requests missing permissions if needed
3. User grants or denies permissions
4. Features enabled/disabled based on grants
5. Can re-request if initially denied

## Audio Processing Pipeline

```
Microphone Input
    ↓
Audio Record Buffer
    ↓
[Loud Mic Processing]
    - Amplification
    - Clipping Prevention
    ↓
[Voice Changer Processing]
    - Effect Selection
    - Pitch Shifting
    - Effect Application
    ↓
Audio Track Output
    ↓
Speaker/Headphone Output
```

## Safety Features

### Audio Protection
- **Automatic Clipping Prevention**: Prevents distortion and speaker damage
- **Volume Limiting**: Maximum safe levels enforced
- **Smooth Transitions**: No pops or clicks when changing settings

### Resource Management
- **Proper Cleanup**: Audio resources released when not in use
- **Background Handling**: Automatically stops when app backgrounded
- **Memory Efficient**: Minimal memory footprint
- **Battery Conscious**: Optimized power usage

### Privacy
- **No Recording**: Audio is processed but not stored
- **No Network Transmission**: All processing is local (currently)
- **Permission Control**: User controls all permissions
- **Transparent**: Open source code

## Performance Optimization

### CPU Optimization
- Efficient algorithms for real-time processing
- Minimized memory allocations in audio thread
- Vectorized operations where possible

### Memory Management
- Fixed-size audio buffers
- No dynamic allocation in hot path
- Proper cleanup and garbage collection

### Battery Life
- Processing only when actively recording
- Automatic stop when backgrounded
- Efficient native audio APIs

## Compatibility

### Minimum Requirements
- **Android Version**: 7.0 (API 24) or higher
- **Microphone**: Required hardware
- **RAM**: 2GB minimum recommended
- **CPU**: Any modern ARM or x86 processor

### Tested Devices
- Most Android devices with API 24+
- Various manufacturers and form factors
- Different audio hardware configurations

### Known Limitations
- Audio quality depends on device microphone
- Some effects may vary by device
- Performance may vary on older devices

## Future Enhancements

### Planned Features
- Network voice streaming
- Recording and playback
- More advanced voice effects (vocoder, autotune)
- Noise suppression and echo cancellation
- Custom effect presets and profiles
- Background service for persistent processing
- Integration with voice chat applications
- Export/import settings
- Effect mixing and layering
- Real-time visualization

### Potential Improvements
- AI-powered voice conversion
- Machine learning noise reduction
- Spatial audio effects
- Voice cloning (ethical considerations apply)
- Multi-user sessions
- Cloud sync for settings

## Technical Details

### Audio Formats
- Input: PCM 16-bit mono @ 44.1kHz
- Processing: 16-bit integer arithmetic
- Output: PCM 16-bit mono @ 44.1kHz

### Effect Algorithms

#### Amplification
```
output = clamp(input * gain, -32768, 32767)
```

#### Pitch Shift (Simple Time-Domain)
```
output[i] = input[floor(i * pitchFactor)]
```

#### Robot Effect (Quantization)
```
output = floor(input / quantLevel) * quantLevel
```

#### Echo
```
output[i] = input[i] + decay * input[i - delay]
```

#### Reverb (Multi-tap Delay)
```
output[i] = input[i] + Σ(decay[j] * input[i - delay[j]])
```

## Support and Troubleshooting

### Common Issues

**No sound output:**
- Check microphone permissions
- Verify device audio settings
- Try restarting the app

**Distorted audio:**
- Reduce amplification level
- Check for hardware issues
- Try different effects

**High latency:**
- Close background apps
- Reduce effect complexity
- Check device performance

**App crashes:**
- Ensure sufficient RAM
- Update to latest version
- Report bug with device info

### Getting Help
- Check [Issues](https://github.com/Tanmayop9/a-discord-/issues)
- Read [README](README.md)
- Review [Contributing](CONTRIBUTING.md)

## Conclusion

Discord Voice Mod provides powerful, real-time voice modification in an easy-to-use Android application. Whether you want to boost your microphone volume, change your voice, or apply fun audio effects, this app has you covered.
