package com.example.iotkebakaran.ui

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AlarmVibratorManager(context: Context) {
    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var alarmJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        } catch (_: Exception) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            } catch (_: Exception) {}
        }
    }

    fun updateAlarmState(shouldAlarm: Boolean) {
        if (shouldAlarm) {
            if (alarmJob == null || alarmJob?.isActive != true) {
                alarmJob = scope.launch {
                    while (isActive) {
                        try {
                            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 400)
                        } catch (_: Exception) {}

                        try {
                            if (vibrator?.hasVibrator() == true) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    vibrator.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
                                } else {
                                    @Suppress("DEPRECATION")
                                    vibrator.vibrate(400)
                                }
                            }
                        } catch (_: Exception) {}

                        delay(700L)
                    }
                }
            }
        } else {
            stopAlarm()
        }
    }

    fun stopAlarm() {
        alarmJob?.cancel()
        alarmJob = null
        try {
            toneGenerator?.stopTone()
        } catch (_: Exception) {}
        try {
            vibrator?.cancel()
        } catch (_: Exception) {}
    }

    fun release() {
        stopAlarm()
        try {
            toneGenerator?.release()
        } catch (_: Exception) {}
        toneGenerator = null
    }
}
