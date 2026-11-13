package com.example.iotkebakaran.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iotkebakaran.data.MonitoringData
import com.example.iotkebakaran.data.MonitoringRepository
import com.example.iotkebakaran.data.MonitoringUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MonitoringViewModel(
    private val repository: MonitoringRepository = MonitoringRepository(),
    private val alarmSoundManager: AlarmSoundManager = AlarmSoundManager()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    init {
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            repository.pollMonitoringData(2000L).collect { result ->
                result.fold(
                    onSuccess = { data ->
                        val safeData = data ?: MonitoringData()
                        val isMuted = _uiState.value.isMuted
                        val isBahaya = safeData.isBahaya
                        alarmSoundManager.updateAlarmState(isBahaya && !isMuted)

                        _uiState.value = MonitoringUiState(
                            data = safeData,
                            isLiveConnected = true,
                            lastUpdatedTimestamp = System.currentTimeMillis(),
                            errorMessage = null,
                            isLoading = false,
                            isMuted = isMuted
                        )
                    },
                    onFailure = { error ->
                        val currentData = _uiState.value.data
                        val isMuted = _uiState.value.isMuted
                        val isBahaya = currentData.isBahaya
                        alarmSoundManager.updateAlarmState(isBahaya && !isMuted)

                        _uiState.value = _uiState.value.copy(
                            isLiveConnected = false,
                            errorMessage = error.localizedMessage ?: "Koneksi ke Firebase terputus",
                            isLoading = false
                        )
                    }
                )
            }
        }
    }

    fun toggleMuteAlarm() {
        val newMuted = !_uiState.value.isMuted
        _uiState.value = _uiState.value.copy(isMuted = newMuted)
        val isBahaya = _uiState.value.data.isBahaya
        alarmSoundManager.updateAlarmState(isBahaya && !newMuted)
    }

    fun refreshNow() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.fetchLatestData()
            result.fold(
                onSuccess = { data ->
                    val isMuted = _uiState.value.isMuted
                    val isBahaya = data.isBahaya
                    alarmSoundManager.updateAlarmState(isBahaya && !isMuted)

                    _uiState.value = MonitoringUiState(
                        data = data,
                        isLiveConnected = true,
                        lastUpdatedTimestamp = System.currentTimeMillis(),
                        errorMessage = null,
                        isLoading = false,
                        isMuted = isMuted
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLiveConnected = false,
                        errorMessage = error.localizedMessage ?: "Gagal memperbarui data",
                        isLoading = false
                    )
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        alarmSoundManager.release()
    }
}
