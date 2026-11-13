package com.example.iotkebakaran.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iotkebakaran.data.EnergiInfo
import com.example.iotkebakaran.ui.theme.AccentPrimary
import com.example.iotkebakaran.ui.theme.AccentSecondary
import com.example.iotkebakaran.ui.theme.BorderColor
import com.example.iotkebakaran.ui.theme.CardSurface
import com.example.iotkebakaran.ui.theme.CardSurfaceVariant
import com.example.iotkebakaran.ui.theme.DangerRed
import com.example.iotkebakaran.ui.theme.SafeGreen
import com.example.iotkebakaran.ui.theme.TextMuted
import com.example.iotkebakaran.ui.theme.TextPrimary
import com.example.iotkebakaran.ui.theme.TextSecondary
import com.example.iotkebakaran.ui.theme.WarningAmber

@Composable
fun PanelEnergyManagement(
    energi: EnergiInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Section Header with Diagnostic Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = AccentPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Manajemen Energi Mandiri",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Diagnostic Badge for INA219
            val inaOk = energi.inaStatus == 1
            Surface(
                color = if (inaOk) SafeGreen.copy(alpha = 0.15f) else DangerRed.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    1.dp,
                    if (inaOk) SafeGreen.copy(alpha = 0.4f) else DangerRed.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (inaOk) SafeGreen else DangerRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (inaOk) "INA219 Aktif" else "INA219 Terputus",
                        color = if (inaOk) SafeGreen else DangerRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Card 1: Baterai LiFePO4
        val presBaterai = energi.presBaterai.coerceIn(0.0, 100.0)
        val batProgress = (presBaterai / 100.0).toFloat()
        val batColor = when {
            presBaterai > 50.0 -> SafeGreen
            presBaterai > 20.0 -> WarningAmber
            else -> DangerRed
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = batColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "BATERAI LiFePO4",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "%.1f %%".format(presBaterai),
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = CardSurfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Tegangan: %.2f V".format(energi.vBaterai),
                                color = AccentPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Circular Progress Indicator for Battery Percentage
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(72.dp),
                        color = CardSurfaceVariant,
                        strokeWidth = 7.dp,
                    )
                    CircularProgressIndicator(
                        progress = { batProgress },
                        modifier = Modifier.size(72.dp),
                        color = batColor,
                        strokeWidth = 7.dp,
                    )
                    Text(
                        text = "${presBaterai.toInt()}%",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Card 2: Panel Surya INA219 (3 Metrik Utama)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SolarPower,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PANEL SURYA (SOLAR PV)",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Surface(
                        color = WarningAmber.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "PENGISIAN",
                            color = WarningAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3 Metrics Grid Box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Metric 1: Tegangan PV (v_pv V)
                    MetricItem(
                        title = "Tegangan PV",
                        value = "%.2f".format(energi.vPv),
                        unit = "Volt",
                        icon = Icons.Default.Bolt,
                        color = AccentPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    // Metric 2: Arus Pengisian (i_pv mA)
                    MetricItem(
                        title = "Arus",
                        value = "%.1f".format(energi.iPv),
                        unit = "mA",
                        icon = Icons.Default.ElectricMeter,
                        color = AccentSecondary,
                        modifier = Modifier.weight(1f)
                    )

                    // Metric 3: Daya Masuk (p_pv W)
                    MetricItem(
                        title = "Daya Masuk",
                        value = "%.2f".format(energi.pPv),
                        unit = "Watt",
                        icon = Icons.Default.Power,
                        color = WarningAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = CardSurfaceVariant,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = unit,
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
