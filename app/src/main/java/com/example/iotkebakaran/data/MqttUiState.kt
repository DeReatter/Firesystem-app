package com.example.iotkebakaran.data

data class MqttUiState(
    val telemetry: TelemetryData = TelemetryData(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val topicPrefix: String = "fire_system",
    val errorMessage: String? = null,
    val isPumpLoading: Boolean = false,
    val isMuted: Boolean = false,
    val lastReceivedTimeMs: Long = 0L
)
