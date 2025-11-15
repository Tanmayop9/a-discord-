package com.discord.voicemod;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Main activity for Discord Voice Mod application
 * Provides UI for loud mic and voice changer features
 */
public class MainActivity extends AppCompatActivity {
    
    private static final int PERMISSION_REQUEST_CODE = 1001;
    
    private VoiceProcessor voiceProcessor;
    
    private SeekBar amplificationSeekBar;
    private SeekBar pitchSeekBar;
    private SwitchMaterial loudMicSwitch;
    private SwitchMaterial voiceChangerSwitch;
    private SwitchMaterial noiseSuppressionSwitch;
    private SwitchMaterial vadSwitch;
    private Spinner effectSpinner;
    private Button recordButton;
    private TextView vadStatusText;
    private AudioVisualizer audioVisualizer;
    
    private boolean isProcessing = false;
    
    // Background update thread for VAD status and visualizer
    private Thread updateThread;
    private boolean isUpdating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeViews();
        initializeVoiceProcessor();
        setupListeners();
        
        // Check and request permissions
        checkPermissions();
    }
    
    private void initializeViews() {
        amplificationSeekBar = findViewById(R.id.amplificationSeekBar);
        pitchSeekBar = findViewById(R.id.pitchSeekBar);
        loudMicSwitch = findViewById(R.id.loudMicSwitch);
        voiceChangerSwitch = findViewById(R.id.voiceChangerSwitch);
        noiseSuppressionSwitch = findViewById(R.id.noiseSuppressionSwitch);
        vadSwitch = findViewById(R.id.vadSwitch);
        effectSpinner = findViewById(R.id.effectSpinner);
        recordButton = findViewById(R.id.recordButton);
        vadStatusText = findViewById(R.id.vadStatusText);
        audioVisualizer = findViewById(R.id.audioVisualizer);
        
        // Setup effect spinner
        String[] effects = {
            getString(R.string.normal),
            getString(R.string.deep_voice),
            getString(R.string.high_voice),
            getString(R.string.robot),
            getString(R.string.echo),
            getString(R.string.reverb)
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            effects
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        effectSpinner.setAdapter(adapter);
    }
    
    private void initializeVoiceProcessor() {
        voiceProcessor = new VoiceProcessor();
    }
    
    private void setupListeners() {
        // Amplification SeekBar
        amplificationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Convert 0-300 to 0.0-3.0 amplification
                float level = progress / 100.0f;
                if (voiceProcessor != null) {
                    voiceProcessor.setAmplificationLevel(level);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Pitch SeekBar
        pitchSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Convert 0-200 to 0.5-2.0 pitch shift
                float shift = (progress / 100.0f) * 1.5f + 0.5f;
                if (voiceProcessor != null) {
                    voiceProcessor.setPitchShift(shift);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Loud Mic Switch
        loudMicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (voiceProcessor != null) {
                voiceProcessor.setLoudMicEnabled(isChecked);
                Toast.makeText(this, 
                    "Loud Mic " + (isChecked ? "Enabled" : "Disabled"), 
                    Toast.LENGTH_SHORT).show();
            }
        });
        
        // Voice Changer Switch
        voiceChangerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (voiceProcessor != null) {
                voiceProcessor.setVoiceChangerEnabled(isChecked);
                Toast.makeText(this, 
                    "Voice Changer " + (isChecked ? "Enabled" : "Disabled"), 
                    Toast.LENGTH_SHORT).show();
            }
        });
        
        // Effect Spinner
        effectSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, 
                                      int position, long id) {
                if (voiceProcessor != null) {
                    VoiceProcessor.VoiceEffect effect = VoiceProcessor.VoiceEffect.values()[position];
                    voiceProcessor.setVoiceEffect(effect);
                }
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
        
        // Noise Suppression Switch
        noiseSuppressionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (voiceProcessor != null) {
                voiceProcessor.setNoiseSuppressionEnabled(isChecked);
                Toast.makeText(this, 
                    "Noise Suppression " + (isChecked ? "Enabled" : "Disabled"), 
                    Toast.LENGTH_SHORT).show();
            }
        });
        
        // VAD Switch
        vadSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (voiceProcessor != null) {
                voiceProcessor.setVoiceActivityDetectionEnabled(isChecked);
                Toast.makeText(this, 
                    "Voice Activity Detection " + (isChecked ? "Enabled" : "Disabled"), 
                    Toast.LENGTH_SHORT).show();
            }
        });
        
        // Record Button
        recordButton.setOnClickListener(v -> {
            if (hasPermissions()) {
                toggleRecording();
            } else {
                requestPermissions();
            }
        });
    }
    
    private void toggleRecording() {
        if (isProcessing) {
            stopProcessing();
        } else {
            startProcessing();
        }
    }
    
    private void startProcessing() {
        if (voiceProcessor != null && !isProcessing) {
            voiceProcessor.startProcessing();
            isProcessing = true;
            recordButton.setText(R.string.stop_recording);
            Toast.makeText(this, "Voice processing started", Toast.LENGTH_SHORT).show();
            
            // Start update thread for VAD status and visualizer
            startUpdateThread();
        }
    }
    
    private void stopProcessing() {
        if (voiceProcessor != null && isProcessing) {
            voiceProcessor.stopProcessing();
            isProcessing = false;
            recordButton.setText(R.string.start_recording);
            Toast.makeText(this, "Voice processing stopped", Toast.LENGTH_SHORT).show();
            
            // Stop update thread
            stopUpdateThread();
        }
    }
    
    private void startUpdateThread() {
        isUpdating = true;
        updateThread = new Thread(() -> {
            while (isUpdating && voiceProcessor != null) {
                try {
                    final boolean voiceActive = voiceProcessor.isVoiceActive();
                    final float amplitude = voiceProcessor.getCurrentAmplitude();
                    
                    runOnUiThread(() -> {
                        // Update VAD status
                        if (vadSwitch.isChecked()) {
                            if (voiceActive) {
                                vadStatusText.setText(R.string.vad_status_speaking);
                                vadStatusText.setTextColor(0xFF43B581); // Green
                            } else {
                                vadStatusText.setText(R.string.vad_status_silent);
                                vadStatusText.setTextColor(0xFFF04747); // Red
                            }
                        } else {
                            vadStatusText.setText(R.string.vad_status_idle);
                            vadStatusText.setTextColor(getColor(R.color.white));
                        }
                        
                        // Update audio visualizer
                        if (audioVisualizer != null) {
                            audioVisualizer.updateAmplitude(amplitude);
                        }
                    });
                    
                    Thread.sleep(100); // Update 10 times per second
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        updateThread.start();
    }
    
    private void stopUpdateThread() {
        isUpdating = false;
        if (updateThread != null) {
            try {
                updateThread.join(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void checkPermissions() {
        if (!hasPermissions()) {
            requestPermissions();
        }
    }
    
    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
            == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            },
            PERMISSION_REQUEST_CODE
        );
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions required for voice features", 
                    Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (voiceProcessor != null) {
            voiceProcessor.release();
            voiceProcessor = null;
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        // Stop processing when app goes to background
        if (isProcessing) {
            stopProcessing();
        }
    }
}
