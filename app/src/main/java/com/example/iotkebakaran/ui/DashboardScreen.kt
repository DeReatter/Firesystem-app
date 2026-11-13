package com.example.iotkebakaran.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iotkebakaran.ui.components.PanelEnergyManagement
import com.example.iotkebakaran.ui.components.PanelFireDetection
import com.example.iotkebakaran.ui.components.PumpControlCard
import com.example.iotkebakaran.ui.components.SettingsDialog
import com.example.iotkebakaran.ui.components.StatusBanner
import com.example.iotkebakaran.ui.components.SystemFooter
import com.example.iotkebakaran.ui.theme.DarkBackground
import com.example.iotkebakaran.ui.theme.TextMuted
import com.example.iotkebakaran.ui.theme.TextPrimary
import com.example.iotkebakaran.ui.theme.TextSecondary
import com.example.iotkebakaran.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MqttViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    if (showSettingsDialog) {
        SettingsDialog(
            currentPrefix = uiState.topicPrefix,
            onDismiss = { showSettingsDialog = false },
            onSave = { newPrefix ->
                viewModel.updateTopicPrefix(newPrefix)
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(WarningAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = WarningAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Fire & Energy Monitor",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "ID: ${uiState.telemetry.deviceId}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan Topik MQTT",
                            tint = TextSecondary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            val isWideScreen = this.maxWidth >= 600.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Priority #1 Banner at the very top
                StatusBanner(
                    telemetry = uiState.telemetry,
                    isMuted = uiState.isMuted,
                    onToggleMute = { viewModel.toggleMuteAlarm() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Emergency Water Pump Control Card
                PumpControlCard(
                    pumpStatus = uiState.telemetry.pumpStatus,
                    isLoading = uiState.isPumpLoading,
                    onSendPumpCommand = { command ->
                        viewModel.sendPumpControl(command)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isWideScreen) {
                    // Wide screen: 2-column layout side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        PanelFireDetection(
                            sensor = uiState.telemetry.sensor,
                            modifier = Modifier.weight(1f)
                        )

                        PanelEnergyManagement(
                            energi = uiState.telemetry.energi,
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    // Mobile portrait: Single column stack
                    PanelFireDetection(
                        sensor = uiState.telemetry.sensor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PanelEnergyManagement(
                        energi = uiState.telemetry.energi
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer NTP Timestamp Info
                SystemFooter(
                    waktuWib = uiState.telemetry.waktuWib,
                    isConnected = uiState.isConnected,
                    isConnecting = uiState.isConnecting,
                    errorMessage = uiState.errorMessage,
                    onReconnectClick = { viewModel.reconnect() }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
