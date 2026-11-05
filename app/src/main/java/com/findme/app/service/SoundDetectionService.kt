package com.findme.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.findme.app.R
import kotlinx.coroutines.*

class SoundDetectionService : Service() {

    private var audioRecord: AudioRecord? = null
    private var isListening = false
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private val sampleRate = 8000
    private val channelConfig = android.media.AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = android.media.AudioFormat.ENCODING_PCM_16BIT

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1, 
                notification, 
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
            )
        } else {
            startForeground(1, notification)
        }

        startListening()
        return START_STICKY
    }

    private fun startListening() {
        if (isListening) return
        isListening = true

        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
            return
        }

        audioRecord?.startRecording()

        serviceScope.launch {
            val buffer = ShortArray(bufferSize)
            while (isListening) {
                val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                if (readSize > 0) {
                    var amplitude = 0.0
                    for (i in 0 until readSize) {
                        amplitude += kotlin.math.abs(buffer[i].toInt())
                    }
                    val avgAmplitude = amplitude / readSize

                    // عتبة الكتشف (الصوت العالي مثل التصفيق)
                    if (avgAmplitude > 25000.0) {
                        withContext(Dispatchers.Main) {
                            AlarmManagerHelper.playAlarm(applicationContext)
                        }
                        // إيقاف مؤقت لمنع التكرار السريع
                        delay(5000)
                    }
                }
                delay(100)
            }
        }
    }

    private fun stopListening() {
        isListening = false
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioRecord = null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "FindMeServiceChannel",
                "FindMe Background Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "FindMeServiceChannel")
            .setContentTitle("FindMe قيد التشغيل")
            .setContentText("التطبيق يراقب الصوت في الخلفية...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopListening()
        serviceJob.cancel()
        AlarmManagerHelper.stopAlarm()
    }
}
