package com.discord.voicemod;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
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
    private Spinner effectSpinner;
    private Button recordButton;
    
    private boolean isProcessing = false;

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
        effectSpinner = findViewById(R.id.effectSpinner);
        recordButton = findViewById(R.id.recordButton);
        
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
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, 
                                      int position, long id) {
                if (voiceProcessor != null) {
                    VoiceProcessor.VoiceEffect effect = VoiceProcessor.VoiceEffect.values()[position];
                    voiceProcessor.setVoiceEffect(effect);
                }
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
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
        }
    }
    
    private void stopProcessing() {
        if (voiceProcessor != null && isProcessing) {
            voiceProcessor.stopProcessing();
            isProcessing = false;
            recordButton.setText(R.string.start_recording);
            Toast.makeText(this, "Voice processing stopped", Toast.LENGTH_SHORT).show();
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
