package com.findme.app.service

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager

object AlarmManagerHelper {
    private var mediaPlayer: MediaPlayer? = null

    fun playAlarm(context: Context) {
        try {
            if (mediaPlayer == null) {
                val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                mediaPlayer = MediaPlayer.create(context, alarmUri).apply {
                    isLooping = true
                    start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopAlarm() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
