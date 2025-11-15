# Quick Start Guide

Get up and running with Discord Voice Mod in minutes!

## For Users

### Installation

#### Option 1: Build from Source
1. **Prerequisites:**
   - Android device running Android 7.0 or later
   - USB cable
   - Computer with Android Studio

2. **Clone and Build:**
   ```bash
   git clone https://github.com/Tanmayop9/a-discord-.git
   cd a-discord-
   ```

3. **Open in Android Studio:**
   - Launch Android Studio
   - File → Open
   - Select the `a-discord-` folder
   - Wait for Gradle sync to complete

4. **Connect Device:**
   - Enable Developer Options on your Android device
   - Enable USB Debugging
   - Connect device via USB

5. **Install:**
   - Click the green "Run" button in Android Studio
   - Or run: `./gradlew installDebug`

#### Option 2: Download APK (When Available)
*APK releases will be available in the GitHub Releases section*

### First Launch

1. **Grant Permissions:**
   - The app will request microphone access
   - Tap "Allow" to enable voice features

2. **Test Basic Features:**
   - Tap "Start Recording"
   - Speak into your microphone
   - You should hear your voice through your device speakers/headphones

3. **Try Effects:**
   - Enable "Loud Mic" switch
   - Adjust the amplification slider
   - Notice your voice getting louder

4. **Experiment with Voice Changer:**
   - Select "Deep Voice" from the dropdown
   - Enable "Voice Changer" switch
   - Hear your voice transformed!

### Basic Usage

```
┌─────────────────────────────────┐
│   Discord Voice Mod             │
├─────────────────────────────────┤
│                                 │
│   Loud Mic                      │
│   ├─ Amplification: [====]     │
│   └─ Enable: [✓]               │
│                                 │
│   Voice Changer                 │
│   ├─ Pitch Shift: [===]        │
│   ├─ Effect: Deep Voice ▼      │
│   └─ Enable: [✓]               │
│                                 │
│   [Start Recording]             │
└─────────────────────────────────┘
```

## For Developers

### Development Setup

1. **Install Android Studio:**
   - Download from https://developer.android.com/studio
   - Install with Android SDK

2. **Clone Repository:**
   ```bash
   git clone https://github.com/Tanmayop9/a-discord-.git
   cd a-discord-
   ```

3. **Open Project:**
   ```bash
   # Open Android Studio
   # File → Open → Select 'a-discord-' directory
   ```

4. **Sync Gradle:**
   - Android Studio will prompt to sync
   - Or: Tools → Gradle → Sync Project with Gradle Files

5. **Configure Device/Emulator:**
   - Physical device: Enable USB debugging
   - Emulator: Create AVD with API 24+ and microphone support

### Project Structure

```
a-discord-/
├── app/
│   ├── src/main/
│   │   ├── java/com/discord/voicemod/
│   │   │   ├── MainActivity.java       ← UI & Controls
│   │   │   └── VoiceProcessor.java     ← Audio Processing
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml   ← UI Layout
│   │   │   └── values/
│   │   │       ├── strings.xml         ← Text resources
│   │   │       ├── colors.xml          ← Color palette
│   │   │       └── themes.xml          ← App theme
│   │   └── AndroidManifest.xml         ← App config
│   └── build.gradle                    ← App dependencies
├── build.gradle                        ← Project config
├── settings.gradle                     ← Gradle settings
└── gradle.properties                   ← Gradle properties
```

### Making Changes

#### Adding a New Voice Effect

1. **Add Effect to Enum:**
   ```java
   // In VoiceProcessor.java
   public enum VoiceEffect {
       NORMAL,
       DEEP_VOICE,
       HIGH_VOICE,
       ROBOT,
       ECHO,
       REVERB,
       YOUR_EFFECT  // Add here
   }
   ```

2. **Implement Effect Method:**
   ```java
   private short[] applyYourEffect(short[] buffer, int length) {
       // Your effect algorithm here
       return buffer;
   }
   ```

3. **Add to Effect Switch:**
   ```java
   private short[] applyVoiceEffect(short[] buffer, int length) {
       switch (currentEffect) {
           case YOUR_EFFECT:
               return applyYourEffect(buffer, length);
           // ... other cases
       }
   }
   ```

4. **Add to UI:**
   ```xml
   <!-- In strings.xml -->
   <string name="your_effect">Your Effect</string>
   ```
   
   ```java
   // In MainActivity.java, update effects array
   String[] effects = {
       getString(R.string.normal),
       // ... other effects
       getString(R.string.your_effect)
   };
   ```

#### Modifying UI

1. **Edit Layout:**
   - Open `app/src/main/res/layout/activity_main.xml`
   - Use Layout Editor or XML editor
   - Add/modify components

2. **Add Resources:**
   - Strings: `res/values/strings.xml`
   - Colors: `res/values/colors.xml`
   - Update as needed

3. **Update MainActivity:**
   - Add view references
   - Set up listeners
   - Handle events

### Building

#### Debug Build
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

#### Release Build (Unsigned)
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release-unsigned.apk
```

#### Install on Device
```bash
./gradlew installDebug
```

### Testing

#### Manual Testing Checklist
- [ ] Grant permissions successfully
- [ ] Start/stop recording works
- [ ] Loud mic amplification works
- [ ] All voice effects work
- [ ] Sliders update values correctly
- [ ] Switches toggle features
- [ ] No crashes or freezes
- [ ] Audio quality is acceptable
- [ ] App responds to background/foreground

#### Device Testing
Test on:
- Different Android versions (7.0+)
- Different manufacturers
- Different screen sizes
- With/without headphones

### Debugging

#### Enable Logging
Add debug logs in code:
```java
import android.util.Log;

private static final String TAG = "VoiceProcessor";

Log.d(TAG, "Processing audio, buffer size: " + bufferSize);
Log.e(TAG, "Error in audio processing", exception);
```

#### View Logs
```bash
# Using ADB
adb logcat | grep VoiceProcessor

# Or in Android Studio
View → Tool Windows → Logcat
```

#### Common Issues

**Build Fails:**
- Clean project: Build → Clean Project
- Rebuild: Build → Rebuild Project
- Invalidate caches: File → Invalidate Caches / Restart

**App Crashes:**
- Check Logcat for stack traces
- Verify permissions are granted
- Check for null pointers
- Ensure audio resources are initialized

**No Audio:**
- Verify device microphone works
- Check permission grants
- Test on different device
- Review audio initialization code

### Code Style

Follow these conventions:
```java
// Class names: PascalCase
public class VoiceProcessor {
    
    // Constants: UPPER_SNAKE_CASE
    private static final int SAMPLE_RATE = 44100;
    
    // Variables: camelCase
    private boolean isRecording;
    
    // Methods: camelCase
    public void startProcessing() {
        // Code here
    }
}
```

### Git Workflow

1. **Create Branch:**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make Changes:**
   - Edit files
   - Test changes
   
3. **Commit:**
   ```bash
   git add .
   git commit -m "Add: description of changes"
   ```

4. **Push:**
   ```bash
   git push origin feature/your-feature-name
   ```

5. **Create Pull Request:**
   - Go to GitHub
   - Click "New Pull Request"
   - Fill in details
   - Submit

### Resources

#### Documentation
- [FEATURES.md](FEATURES.md) - Feature details
- [ARCHITECTURE.md](ARCHITECTURE.md) - Architecture guide
- [CONTRIBUTING.md](CONTRIBUTING.md) - Contribution guidelines
- [SECURITY.md](SECURITY.md) - Security information

#### Android Resources
- [Android Developer Guide](https://developer.android.com/guide)
- [AudioRecord API](https://developer.android.com/reference/android/media/AudioRecord)
- [AudioTrack API](https://developer.android.com/reference/android/media/AudioTrack)
- [Material Design](https://material.io/design)

#### Audio Processing
- [Digital Signal Processing Basics](https://en.wikipedia.org/wiki/Digital_signal_processing)
- [Audio Effects Theory](https://en.wikipedia.org/wiki/Audio_signal_processing)

## Troubleshooting

### Build Issues

**Gradle Sync Failed:**
1. Check internet connection
2. Update Gradle version in gradle/wrapper/gradle-wrapper.properties
3. File → Invalidate Caches / Restart

**SDK Not Found:**
1. Open SDK Manager: Tools → SDK Manager
2. Install required SDK versions (API 24, 33)
3. Sync project again

### Runtime Issues

**Permission Denied:**
- Check AndroidManifest.xml has correct permissions
- Request permissions at runtime
- Check device Settings → Apps → Permissions

**Audio Not Working:**
- Test device microphone in other apps
- Verify AudioRecord initialization succeeds
- Check for audio focus issues
- Test with headphones

## Next Steps

1. **Explore the Code:**
   - Read through MainActivity.java
   - Understand VoiceProcessor.java
   - Study the UI layout

2. **Make Small Changes:**
   - Adjust color scheme
   - Add new string resources
   - Modify UI layout

3. **Add Features:**
   - Implement new voice effect
   - Add settings persistence
   - Create custom UI controls

4. **Contribute:**
   - Fix bugs
   - Improve documentation
   - Share improvements

## Getting Help

- **Documentation**: Check docs in this repository
- **Issues**: Search/create GitHub issues
- **Discussions**: Use GitHub Discussions
- **Code**: Read existing code and comments

## Quick Commands

```bash
# Clone
git clone https://github.com/Tanmayop9/a-discord-.git

# Build debug APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Clean build
./gradlew clean

# List tasks
./gradlew tasks

# Check dependencies
./gradlew dependencies
```

Happy coding! 🎤🎭
