package com.discord.voicemod;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages voice effect presets
 * Allows users to save and load voice configurations
 */
public class VoicePreset {
    
    private static final String PREFS_NAME = "VoicePresets";
    private static final String PRESETS_KEY = "presets";
    
    private String name;
    private float amplificationLevel;
    private float pitchShift;
    private VoiceProcessor.VoiceEffect effect;
    private boolean loudMicEnabled;
    private boolean voiceChangerEnabled;
    private boolean noiseSuppressionEnabled;
    private boolean vadEnabled;
    
    public VoicePreset(String name, float amplificationLevel, float pitchShift,
                      VoiceProcessor.VoiceEffect effect, boolean loudMicEnabled,
                      boolean voiceChangerEnabled, boolean noiseSuppressionEnabled,
                      boolean vadEnabled) {
        this.name = name;
        this.amplificationLevel = amplificationLevel;
        this.pitchShift = pitchShift;
        this.effect = effect;
        this.loudMicEnabled = loudMicEnabled;
        this.voiceChangerEnabled = voiceChangerEnabled;
        this.noiseSuppressionEnabled = noiseSuppressionEnabled;
        this.vadEnabled = vadEnabled;
    }
    
    // Getters
    public String getName() { return name; }
    public float getAmplificationLevel() { return amplificationLevel; }
    public float getPitchShift() { return pitchShift; }
    public VoiceProcessor.VoiceEffect getEffect() { return effect; }
    public boolean isLoudMicEnabled() { return loudMicEnabled; }
    public boolean isVoiceChangerEnabled() { return voiceChangerEnabled; }
    public boolean isNoiseSuppressionEnabled() { return noiseSuppressionEnabled; }
    public boolean isVadEnabled() { return vadEnabled; }
    
    /**
     * Convert preset to JSON
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("amplificationLevel", amplificationLevel);
        json.put("pitchShift", pitchShift);
        json.put("effect", effect.name());
        json.put("loudMicEnabled", loudMicEnabled);
        json.put("voiceChangerEnabled", voiceChangerEnabled);
        json.put("noiseSuppressionEnabled", noiseSuppressionEnabled);
        json.put("vadEnabled", vadEnabled);
        return json;
    }
    
    /**
     * Create preset from JSON
     */
    public static VoicePreset fromJSON(JSONObject json) throws JSONException {
        return new VoicePreset(
            json.getString("name"),
            (float) json.getDouble("amplificationLevel"),
            (float) json.getDouble("pitchShift"),
            VoiceProcessor.VoiceEffect.valueOf(json.getString("effect")),
            json.getBoolean("loudMicEnabled"),
            json.getBoolean("voiceChangerEnabled"),
            json.getBoolean("noiseSuppressionEnabled"),
            json.getBoolean("vadEnabled")
        );
    }
    
    /**
     * Save preset to SharedPreferences
     */
    public static void savePreset(Context context, VoicePreset preset) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String presetsJson = prefs.getString(PRESETS_KEY, "[]");
            JSONArray presets = new JSONArray(presetsJson);
            
            // Check if preset with same name exists and replace it
            for (int i = 0; i < presets.length(); i++) {
                JSONObject obj = presets.getJSONObject(i);
                if (obj.getString("name").equals(preset.getName())) {
                    presets.remove(i);
                    break;
                }
            }
            
            presets.put(preset.toJSON());
            
            prefs.edit().putString(PRESETS_KEY, presets.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Load all presets from SharedPreferences
     */
    public static List<VoicePreset> loadPresets(Context context) {
        List<VoicePreset> presetList = new ArrayList<>();
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String presetsJson = prefs.getString(PRESETS_KEY, "[]");
            JSONArray presets = new JSONArray(presetsJson);
            
            for (int i = 0; i < presets.length(); i++) {
                presetList.add(fromJSON(presets.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return presetList;
    }
    
    /**
     * Delete preset from SharedPreferences
     */
    public static void deletePreset(Context context, String presetName) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String presetsJson = prefs.getString(PRESETS_KEY, "[]");
            JSONArray presets = new JSONArray(presetsJson);
            
            for (int i = 0; i < presets.length(); i++) {
                JSONObject obj = presets.getJSONObject(i);
                if (obj.getString("name").equals(presetName)) {
                    presets.remove(i);
                    break;
                }
            }
            
            prefs.edit().putString(PRESETS_KEY, presets.toString()).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Apply preset to VoiceProcessor
     */
    public void applyTo(VoiceProcessor processor) {
        processor.setAmplificationLevel(amplificationLevel);
        processor.setPitchShift(pitchShift);
        processor.setVoiceEffect(effect);
        processor.setLoudMicEnabled(loudMicEnabled);
        processor.setVoiceChangerEnabled(voiceChangerEnabled);
        processor.setNoiseSuppressionEnabled(noiseSuppressionEnabled);
        processor.setVoiceActivityDetectionEnabled(vadEnabled);
    }
}
