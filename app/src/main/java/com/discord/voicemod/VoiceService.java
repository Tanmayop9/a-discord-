package com.discord.voicemod;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

/**
 * Background service for persistent voice processing
 * Allows voice effects to continue running when app is in background
 */
public class VoiceService extends Service {
    
    private static final String CHANNEL_ID = "VoiceProcessingChannel";
    private static final int NOTIFICATION_ID = 1001;
    
    private VoiceProcessor voiceProcessor;
    private final IBinder binder = new VoiceServiceBinder();
    
    public class VoiceServiceBinder extends Binder {
        public VoiceService getService() {
            return VoiceService.this;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        voiceProcessor = new VoiceProcessor();
        createNotificationChannel();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start as foreground service with notification
        startForeground(NOTIFICATION_ID, createNotification());
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (voiceProcessor != null) {
            voiceProcessor.release();
            voiceProcessor = null;
        }
    }
    
    /**
     * Get the voice processor instance
     */
    public VoiceProcessor getVoiceProcessor() {
        return voiceProcessor;
    }
    
    /**
     * Create notification channel for Android O and above
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Voice Processing",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Voice processing is running");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
    
    /**
     * Create notification for foreground service
     */
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 
            0, 
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        );
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Discord Voice Mod")
            .setContentText("Voice processing active")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build();
    }
}
