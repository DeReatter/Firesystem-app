package com.example.iotkebakaran.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.iotkebakaran.data.MqttManager
import com.example.iotkebakaran.data.MqttUiState
import com.example.iotkebakaran.data.TelemetryData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MqttViewModel(application: Application) : AndroidViewModel(application) {

    private val mqttManager = MqttManager()
    private val alarmVibratorManager = AlarmVibratorManager(application.applicationContext)

    private val _uiState = MutableStateFlow(MqttUiState())
    val uiState: StateFlow<MqttUiState> = _uiState.asStateFlow()

    private var pumpTimeoutJob: Job? = null

    init {
        // Sample initial data so dashboard looks complete right away
        val initialTelemetry = TelemetryData(
            deviceId = "FIRE_NODE_UNY_01",
            waktuWib = "2026-11-13 17:55:00",
            statusKebakaran = "AMAN",
            pumpStatus = "OFF"
        )
        _uiState.update { it.copy(telemetry = initialTelemetry) }

        observeMqtt()
        mqttManager.connect()
    }

    private fun observeMqtt() {
        viewModelScope.launch {
            mqttManager.isConnected.collect { connected ->
                _uiState.update { it.copy(isConnected = connected) }
            }
        }

        viewModelScope.launch {
            mqttManager.isConnecting.collect { connecting ->
                _uiState.update { it.copy(isConnecting = connecting) }
            }
        }

        viewModelScope.launch {
            mqttManager.telemetryFlow.collect { telemetry ->
                pumpTimeoutJob?.cancel()
                val isMuted = _uiState.value.isMuted
                val isBahaya = telemetry.isBahaya

                alarmVibratorManager.updateAlarmState(isBahaya && !isMuted)

                _uiState.update { currentState ->
                    currentState.copy(
                        telemetry = telemetry,
                        lastReceivedTimeMs = System.currentTimeMillis(),
                        isPumpLoading = false,
                        errorMessage = null
                    )
                }
            }
        }

        viewModelScope.launch {
            mqttManager.alarmFlow.collect { alarmPayload ->
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = "Peringatan Darurat: $alarmPayload"
                    )
                }
            }
        }

        viewModelScope.launch {
            mqttManager.errorFlow.collect { errorMsg ->
                _uiState.update { currentState ->
                    currentState.copy(errorMessage = errorMsg)
                }
            }
        }
    }

    fun sendPumpControl(command: String) {
        _uiState.update { it.copy(isPumpLoading = true) }
        mqttManager.publishPumpControl(command)

        pumpTimeoutJob?.cancel()
        pumpTimeoutJob = viewModelScope.launch {
            delay(5000L) // 5 seconds timeout fallback if hardware doesn't respond
            if (_uiState.value.isPumpLoading) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isPumpLoading = false,
                        errorMessage = "Tidak ada konfirmasi umpan balik dari modul ESP32"
                    )
                }
            }
        }
    }

    fun updateTopicPrefix(newPrefix: String) {
        val trimmed = newPrefix.trim()
        if (trimmed.isNotEmpty()) {
            _uiState.update { it.copy(topicPrefix = trimmed) }
            mqttManager.connect(trimmed)
        }
    }

    fun reconnect() {
        mqttManager.connect()
    }

    fun toggleMuteAlarm() {
        val newMuted = !_uiState.value.isMuted
        _uiState.update { it.copy(isMuted = newMuted) }
        val isBahaya = _uiState.value.telemetry.isBahaya
        alarmVibratorManager.updateAlarmState(isBahaya && !newMuted)
    }

    override fun onCleared() {
        super.onCleared()
        alarmVibratorManager.release()
        mqttManager.disconnect()
    }
}
