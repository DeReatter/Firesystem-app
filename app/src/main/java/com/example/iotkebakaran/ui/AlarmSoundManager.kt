package com.example.iotkebakaran.ui

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AlarmSoundManager {
    private var toneGenerator: ToneGenerator? = null
    private var alarmJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        } catch (e: Exception) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            } catch (_: Exception) {
                toneGenerator = null
            }
        }
    }

    fun updateAlarmState(shouldPlayAlarm: Boolean) {
        if (shouldPlayAlarm) {
            if (alarmJob == null || alarmJob?.isActive != true) {
                alarmJob = scope.launch {
                    while (isActive) {
                        try {
                            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 400)
                        } catch (_: Exception) { }
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
        } catch (_: Exception) { }
    }

    fun release() {
        stopAlarm()
        try {
            toneGenerator?.release()
        } catch (_: Exception) { }
        toneGenerator = null
    }
}
