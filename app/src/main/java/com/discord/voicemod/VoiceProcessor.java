package com.discord.voicemod;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

/**
 * Audio processor for voice modification features
 * Handles loud mic (amplification) and voice changing (pitch shift)
 */
public class VoiceProcessor {
    
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private boolean isRecording = false;
    private Thread recordingThread;
    
    private float amplificationLevel = 1.0f; // 1.0 = normal, 2.0 = 2x louder
    private float pitchShift = 1.0f; // 1.0 = normal, 0.5 = lower, 2.0 = higher
    private VoiceEffect currentEffect = VoiceEffect.NORMAL;
    
    private boolean loudMicEnabled = false;
    private boolean voiceChangerEnabled = false;
    
    public enum VoiceEffect {
        NORMAL,
        DEEP_VOICE,
        HIGH_VOICE,
        ROBOT,
        ECHO,
        REVERB
    }
    
    public VoiceProcessor() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        
        try {
            audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            );
            
            audioTrack = new AudioTrack.Builder()
                .setAudioFormat(new AudioFormat.Builder()
                    .setEncoding(AUDIO_FORMAT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build())
                .setBufferSizeInBytes(bufferSize)
                .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Start processing audio with voice effects
     */
    public void startProcessing() {
        if (isRecording) return;
        
        isRecording = true;
        
        recordingThread = new Thread(() -> {
            int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
            short[] audioBuffer = new short[bufferSize];
            
            if (audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecord.startRecording();
            }
            
            if (audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
                audioTrack.play();
            }
            
            while (isRecording && audioRecord != null) {
                int read = audioRecord.read(audioBuffer, 0, bufferSize);
                
                if (read > 0) {
                    short[] processedBuffer = processAudio(audioBuffer, read);
                    
                    if (audioTrack != null) {
                        audioTrack.write(processedBuffer, 0, read);
                    }
                }
            }
        });
        
        recordingThread.start();
    }
    
    /**
     * Stop processing audio
     */
    public void stopProcessing() {
        isRecording = false;
        
        if (recordingThread != null) {
            try {
                recordingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (audioRecord != null && audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            audioRecord.stop();
        }
        
        if (audioTrack != null && audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            audioTrack.pause();
            audioTrack.flush();
        }
    }
    
    /**
     * Process audio buffer with current effects
     */
    private short[] processAudio(short[] buffer, int length) {
        short[] processed = new short[length];
        System.arraycopy(buffer, 0, processed, 0, length);
        
        // Apply loud mic (amplification)
        if (loudMicEnabled) {
            processed = applyAmplification(processed, length);
        }
        
        // Apply voice changer effects
        if (voiceChangerEnabled) {
            processed = applyVoiceEffect(processed, length);
        }
        
        return processed;
    }
    
    /**
     * Apply amplification to audio buffer (Loud Mic feature)
     */
    private short[] applyAmplification(short[] buffer, int length) {
        for (int i = 0; i < length; i++) {
            int amplified = (int) (buffer[i] * amplificationLevel);
            
            // Prevent clipping
            if (amplified > Short.MAX_VALUE) {
                amplified = Short.MAX_VALUE;
            } else if (amplified < Short.MIN_VALUE) {
                amplified = Short.MIN_VALUE;
            }
            
            buffer[i] = (short) amplified;
        }
        return buffer;
    }
    
    /**
     * Apply voice effect to audio buffer (Voice Changer feature)
     */
    private short[] applyVoiceEffect(short[] buffer, int length) {
        switch (currentEffect) {
            case DEEP_VOICE:
                return applyPitchShift(buffer, length, 0.7f);
            case HIGH_VOICE:
                return applyPitchShift(buffer, length, 1.5f);
            case ROBOT:
                return applyRobotEffect(buffer, length);
            case ECHO:
                return applyEchoEffect(buffer, length);
            case REVERB:
                return applyReverbEffect(buffer, length);
            case NORMAL:
            default:
                return applyPitchShift(buffer, length, pitchShift);
        }
    }
    
    /**
     * Apply pitch shift to audio buffer
     */
    private short[] applyPitchShift(short[] buffer, int length, float shift) {
        if (shift == 1.0f) return buffer;
        
        short[] output = new short[length];
        
        // Simple pitch shifting using time-stretching
        for (int i = 0; i < length; i++) {
            int sourceIndex = (int) (i * shift);
            if (sourceIndex < length) {
                output[i] = buffer[sourceIndex];
            } else {
                output[i] = 0;
            }
        }
        
        return output;
    }
    
    /**
     * Apply robot voice effect
     */
    private short[] applyRobotEffect(short[] buffer, int length) {
        // Quantize the audio signal for robotic effect
        for (int i = 0; i < length; i++) {
            int quantized = (buffer[i] / 1000) * 1000;
            buffer[i] = (short) quantized;
        }
        return buffer;
    }
    
    /**
     * Apply echo effect
     */
    private short[] applyEchoEffect(short[] buffer, int length) {
        int delayInSamples = SAMPLE_RATE / 4; // 250ms delay
        float decayFactor = 0.5f;
        
        for (int i = delayInSamples; i < length; i++) {
            int echo = (int) (buffer[i - delayInSamples] * decayFactor);
            int mixed = buffer[i] + echo;
            
            if (mixed > Short.MAX_VALUE) {
                mixed = Short.MAX_VALUE;
            } else if (mixed < Short.MIN_VALUE) {
                mixed = Short.MIN_VALUE;
            }
            
            buffer[i] = (short) mixed;
        }
        
        return buffer;
    }
    
    /**
     * Apply reverb effect
     */
    private short[] applyReverbEffect(short[] buffer, int length) {
        // Simple reverb using multiple delayed echoes
        int[] delays = {SAMPLE_RATE / 10, SAMPLE_RATE / 20, SAMPLE_RATE / 30};
        float[] decays = {0.3f, 0.2f, 0.1f};
        
        for (int d = 0; d < delays.length; d++) {
            for (int i = delays[d]; i < length; i++) {
                int reverb = (int) (buffer[i - delays[d]] * decays[d]);
                int mixed = buffer[i] + reverb;
                
                if (mixed > Short.MAX_VALUE) {
                    mixed = Short.MAX_VALUE;
                } else if (mixed < Short.MIN_VALUE) {
                    mixed = Short.MIN_VALUE;
                }
                
                buffer[i] = (short) mixed;
            }
        }
        
        return buffer;
    }
    
    // Setters
    public void setAmplificationLevel(float level) {
        this.amplificationLevel = level;
    }
    
    public void setPitchShift(float shift) {
        this.pitchShift = shift;
    }
    
    public void setVoiceEffect(VoiceEffect effect) {
        this.currentEffect = effect;
    }
    
    public void setLoudMicEnabled(boolean enabled) {
        this.loudMicEnabled = enabled;
    }
    
    public void setVoiceChangerEnabled(boolean enabled) {
        this.voiceChangerEnabled = enabled;
    }
    
    public boolean isRecording() {
        return isRecording;
    }
    
    /**
     * Release resources
     */
    public void release() {
        stopProcessing();
        
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }
    }
}
