# Architecture Documentation

## Overview

Discord Voice Mod is built using standard Android development practices with a focus on real-time audio processing. The architecture is designed to be simple, maintainable, and performant.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                     MainActivity                         │
│  ┌──────────────────────────────────────────────────┐   │
│  │              User Interface Layer                 │   │
│  │  • SeekBars (Amplification, Pitch)               │   │
│  │  • Switches (Feature toggles)                     │   │
│  │  • Spinner (Effect selection)                     │   │
│  │  • Button (Record control)                        │   │
│  └──────────────────────────────────────────────────┘   │
│                          ↕                               │
│  ┌──────────────────────────────────────────────────┐   │
│  │           Control & Event Handling                │   │
│  │  • Permission management                          │   │
│  │  • User input handling                            │   │
│  │  • State management                               │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────────┬──────────────────────────────┘
                           ↕
┌─────────────────────────────────────────────────────────┐
│                   VoiceProcessor                         │
│  ┌──────────────────────────────────────────────────┐   │
│  │            Audio Recording Thread                 │   │
│  │  • Capture microphone input                       │   │
│  │  • Read audio buffers                             │   │
│  │  • Control recording state                        │   │
│  └──────────────────────────────────────────────────┘   │
│                          ↕                               │
│  ┌──────────────────────────────────────────────────┐   │
│  │           Audio Processing Pipeline               │   │
│  │                                                    │   │
│  │  ┌────────────────────────────────────────────┐  │   │
│  │  │  1. Apply Amplification (if enabled)       │  │   │
│  │  │     • Multiply samples by gain             │  │   │
│  │  │     • Prevent clipping                      │  │   │
│  │  └────────────────────────────────────────────┘  │   │
│  │                     ↓                             │   │
│  │  ┌────────────────────────────────────────────┐  │   │
│  │  │  2. Apply Voice Effect (if enabled)        │  │   │
│  │  │     • Select effect algorithm               │  │   │
│  │  │     • Apply pitch shift                     │  │   │
│  │  │     • Apply selected effect                 │  │   │
│  │  └────────────────────────────────────────────┘  │   │
│  │                                                    │   │
│  └──────────────────────────────────────────────────┘   │
│                          ↕                               │
│  ┌──────────────────────────────────────────────────┐   │
│  │            Audio Playback Thread                  │   │
│  │  • Write processed audio to output                │   │
│  │  • Manage playback state                          │   │
│  │  • Handle audio routing                           │   │
│  └──────────────────────────────────────────────────┘   │
└──────────────────────────┬──────────────────────────────┘
                           ↕
┌─────────────────────────────────────────────────────────┐
│              Android Audio System                        │
│  • AudioRecord (Input)                                   │
│  • AudioTrack (Output)                                   │
└─────────────────────────────────────────────────────────┘
```

## Component Details

### 1. MainActivity

**Responsibilities:**
- Initialize and manage UI components
- Handle user interactions
- Request and manage permissions
- Control VoiceProcessor lifecycle
- Display feedback to user

**Key Methods:**
- `onCreate()`: Initialize UI and processor
- `setupListeners()`: Set up event handlers
- `checkPermissions()`: Verify and request permissions
- `toggleRecording()`: Start/stop audio processing
- `onDestroy()`: Clean up resources

**State Management:**
- `isProcessing`: Tracks whether audio processing is active
- UI component states: Switch toggles, slider positions, spinner selection

### 2. VoiceProcessor

**Responsibilities:**
- Manage audio recording and playback
- Process audio in real-time
- Apply voice effects
- Handle audio threading
- Manage audio resources

**Key Components:**

#### Audio Recording
- Uses `AudioRecord` for microphone input
- Runs on dedicated thread
- Continuous buffer reading
- Sample rate: 44,100 Hz

#### Audio Processing
- Processes audio in chunks
- Applies effects in order
- Maintains low latency
- Prevents audio clipping

#### Audio Playback
- Uses `AudioTrack` for output
- Synchronized with recording
- Real-time playback
- Proper buffer management

**Key Methods:**
- `startProcessing()`: Begin audio capture and processing
- `stopProcessing()`: End audio processing
- `processAudio()`: Main processing pipeline
- `applyAmplification()`: Loud mic implementation
- `applyVoiceEffect()`: Voice changer implementation
- Various effect methods: `applyPitchShift()`, `applyEchoEffect()`, etc.

### 3. Audio Effects

#### Amplification (Loud Mic)
```java
for each sample:
    amplified = sample * amplificationLevel
    output = clamp(amplified, MIN_VALUE, MAX_VALUE)
```

#### Pitch Shift
```java
for each output sample i:
    sourceIndex = i * pitchFactor
    output[i] = input[sourceIndex]
```

#### Robot Effect
```java
quantizationLevel = 1000
for each sample:
    output = (sample / quantizationLevel) * quantizationLevel
```

#### Echo Effect
```java
delay = sampleRate / 4  // 250ms
decay = 0.5
for each sample i where i >= delay:
    output[i] = input[i] + input[i - delay] * decay
```

#### Reverb Effect
```java
delays = [sampleRate/10, sampleRate/20, sampleRate/30]
decays = [0.3, 0.2, 0.1]
for each delay d:
    for each sample i where i >= delay[d]:
        output[i] += input[i - delay[d]] * decay[d]
```

## Threading Model

### Main Thread (UI Thread)
- Handles all UI updates
- Processes user input
- Updates UI components
- No heavy processing

### Recording Thread
- Dedicated thread for audio processing
- Runs continuously while recording
- High priority for low latency
- Minimal memory allocations

**Thread Lifecycle:**
```
Start Recording
    ↓
Create recordingThread
    ↓
Start AudioRecord
    ↓
Start AudioTrack
    ↓
[Processing Loop]
  ← Read buffer
  ← Process audio
  ← Write output
  ← Repeat while isRecording
    ↓
Stop AudioRecord
    ↓
Stop AudioTrack
    ↓
Join thread
    ↓
End
```

## Data Flow

### Input → Output Flow
```
Microphone
    ↓
AudioRecord buffer (read)
    ↓
short[] audioBuffer
    ↓
processAudio()
    ↓
short[] processedBuffer
    ↓
AudioTrack buffer (write)
    ↓
Speaker/Headphones
```

### Configuration Flow
```
User adjusts slider
    ↓
MainActivity receives event
    ↓
Convert to appropriate value
    ↓
Call VoiceProcessor setter
    ↓
Effect applied on next buffer
```

## Memory Management

### Buffer Allocation
- Fixed-size buffers allocated once
- Size determined by `AudioRecord.getMinBufferSize()`
- No dynamic allocation in audio thread
- Reused across all processing

### Resource Lifecycle
```
onCreate()
    ↓
Initialize VoiceProcessor
    ↓
Allocate AudioRecord
    ↓
Allocate AudioTrack
    ↓
[Processing as needed]
    ↓
onDestroy()
    ↓
Stop processing
    ↓
Release AudioRecord
    ↓
Release AudioTrack
    ↓
Null references
```

## Performance Optimization

### Audio Thread Optimization
- Minimal object creation
- Primitive types for calculations
- Inline simple methods
- Avoid synchronization where possible
- Reuse buffers

### UI Thread Optimization
- Update UI only on changes
- Batch UI updates
- Use ViewHolder pattern (if needed)
- Avoid heavy calculations

### Memory Optimization
- Fixed buffer sizes
- No GC pressure in audio thread
- Proper resource cleanup
- Efficient data structures

## Error Handling

### Permission Errors
```java
if (!hasPermissions()) {
    requestPermissions();
    disable features;
    show message;
}
```

### Audio Initialization Errors
```java
try {
    initialize AudioRecord/AudioTrack
} catch (Exception e) {
    log error;
    show user-friendly message;
    disable affected features;
}
```

### Runtime Errors
```java
if (recordingThread != null) {
    try {
        // processing
    } catch (Exception e) {
        log error;
        stop processing;
        notify user;
    }
}
```

## State Management

### Application States
- **Not Recording**: Default state, ready to start
- **Recording**: Active audio processing
- **Paused**: App in background, processing stopped

### State Transitions
```
Not Recording → Recording: User presses start
Recording → Not Recording: User presses stop
Recording → Not Recording: App goes to background
Recording → Not Recording: Error occurs
```

## Configuration Management

### User Preferences
Currently in-memory only:
- Amplification level
- Pitch shift value
- Selected effect
- Feature toggles

Future: Persist to SharedPreferences

## Dependencies

### Core Android
- `android.media.AudioRecord`: Audio input
- `android.media.AudioTrack`: Audio output
- `android.media.MediaRecorder`: Audio source constants
- `android.media.AudioFormat`: Format constants

### UI Components
- `androidx.appcompat`: AppCompat support
- `com.google.android.material`: Material Design components
- `androidx.constraintlayout`: Layout system

### Testing (Future)
- `junit`: Unit testing
- `androidx.test`: Android testing

## Build System

### Gradle Configuration
- **Plugin**: Android Application Plugin
- **Compile SDK**: 33 (Android 13)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 33 (Android 13)
- **Java Version**: 1.8

### Build Types
- **Debug**: Development builds with debugging enabled
- **Release**: Production builds with ProGuard (optional)

## Future Architecture Considerations

### Planned Enhancements

1. **Service Architecture**
   - Background processing service
   - Notification controls
   - Persistent processing

2. **Settings Persistence**
   - SharedPreferences for user settings
   - Preset management
   - Import/export configuration

3. **Advanced Effects**
   - Plugin architecture for effects
   - Effect chaining
   - Custom effect parameters

4. **Network Features**
   - Client-server architecture
   - Voice streaming protocol
   - Encryption and security

5. **Testing Infrastructure**
   - Unit tests for effects
   - Integration tests for audio pipeline
   - UI tests for user flows

## Design Patterns

### Patterns Used
- **Singleton-like**: VoiceProcessor (one instance per MainActivity)
- **Observer**: UI event listeners
- **Strategy**: Voice effect selection
- **Template Method**: Effect processing pipeline

### Patterns to Consider
- **Factory**: Effect creation
- **Builder**: Configuration objects
- **Repository**: Settings persistence
- **Dependency Injection**: Testing and modularity

## Conclusion

The architecture is designed for:
- **Simplicity**: Easy to understand and maintain
- **Performance**: Optimized for real-time audio
- **Extensibility**: Easy to add new effects
- **Reliability**: Proper error handling and resource management
- **Testability**: Clear separation of concerns (future enhancement)
