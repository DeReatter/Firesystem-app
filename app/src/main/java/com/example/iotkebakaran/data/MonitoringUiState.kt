package com.example.iotkebakaran.data

data class MonitoringUiState(
    val data: MonitoringData = MonitoringData(),
    val isLiveConnected: Boolean = false,
    val lastUpdatedTimestamp: Long = 0L,
    val errorMessage: String? = null,
    val isLoading: Boolean = true,
    val isMuted: Boolean = false
)
