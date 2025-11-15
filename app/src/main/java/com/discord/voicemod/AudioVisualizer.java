package com.discord.voicemod;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Custom view for visualizing audio levels
 * Displays real-time audio amplitude as a waveform or bar
 */
public class AudioVisualizer extends View {
    
    private Paint paint;
    private float amplitude = 0f;
    private int barColor = 0xFF5865F2; // Discord blurple
    private int backgroundColor = 0xFF2C2F33; // Dark background
    
    public AudioVisualizer(Context context) {
        super(context);
        init();
    }
    
    public AudioVisualizer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public AudioVisualizer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }
    
    /**
     * Update amplitude value (0.0 to 1.0)
     */
    public void updateAmplitude(float amplitude) {
        this.amplitude = Math.max(0f, Math.min(1f, amplitude));
        invalidate(); // Redraw
    }
    
    /**
     * Set bar color
     */
    public void setBarColor(int color) {
        this.barColor = color;
    }
    
    /**
     * Set background color
     */
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw background
        paint.setColor(backgroundColor);
        canvas.drawRect(0, 0, width, height, paint);
        
        // Draw amplitude bar
        paint.setColor(barColor);
        float barWidth = width * amplitude;
        canvas.drawRect(0, 0, barWidth, height, paint);
        
        // Draw border
        paint.setColor(0xFF99AAB5); // Light gray
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setStyle(Paint.Style.FILL);
    }
    
    /**
     * Calculate amplitude from audio buffer
     */
    public static float calculateAmplitude(short[] buffer, int length) {
        if (buffer == null || length == 0) return 0f;
        
        // Calculate RMS (Root Mean Square)
        long sum = 0;
        for (int i = 0; i < length; i++) {
            sum += buffer[i] * buffer[i];
        }
        double rms = Math.sqrt((double) sum / length);
        
        // Normalize to 0-1 range (assuming max amplitude is around 10000)
        return (float) Math.min(1.0, rms / 10000.0);
    }
}
