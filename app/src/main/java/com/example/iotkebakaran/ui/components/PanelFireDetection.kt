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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iotkebakaran.data.SensorInfo
import com.example.iotkebakaran.ui.theme.AccentPrimary
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
fun PanelFireDetection(
    sensor: SensorInfo,
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
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Parameter Deteksi Kebakaran & Lingkungan",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Diagnostic Badge for DHT22
            val dhtOk = sensor.dhtStatus == 1
            Surface(
                color = if (dhtOk) SafeGreen.copy(alpha = 0.15f) else DangerRed.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    1.dp,
                    if (dhtOk) SafeGreen.copy(alpha = 0.4f) else DangerRed.copy(alpha = 0.4f)
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
                            .background(if (dhtOk) SafeGreen else DangerRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (dhtOk) "DHT22 OK" else "DHT22 Error",
                        color = if (dhtOk) SafeGreen else DangerRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Card 1: MQ-2 Gas & Asap
        val mq2Value = sensor.mq2Raw.coerceIn(0, 4095)
        val mq2Progress = mq2Value / 4095f
        val mq2Color = when {
            mq2Value > 2000 -> DangerRed
            mq2Value >= 1200 -> WarningAmber
            else -> SafeGreen
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            border = BorderStroke(1.dp, if (mq2Value > 2000) DangerRed else BorderColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "SENSOR MQ-2 (GAS & ASAP)",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$mq2Value",
                                color = mq2Color,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ADC (0 - 4095)",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    // Status Badge
                    Surface(
                        color = mq2Color.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = when {
                                mq2Value > 2000 -> "BAHAYA (>2000)"
                                mq2Value >= 1200 -> "WASPADA"
                                else -> "NORMAL"
                            },
                            color = mq2Color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Indicator (Green < 1200, Yellow 1200-2000, Red > 2000)
                LinearProgressIndicator(
                    progress = { mq2Progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = mq2Color,
                    trackColor = CardSurfaceVariant,
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "0", color = TextMuted, fontSize = 10.sp)
                    Text(
                        text = "Ambang batas bahaya > 2000 ADC",
                        color = DangerRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = "4095", color = TextMuted, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Card 2: Suhu & Kelembapan (DHT22) - Berdampingan
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
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Suhu Column
                val suhuVal = sensor.suhu
                val suhuColor = when {
                    suhuVal > 50.0 -> DangerRed
                    suhuVal > 38.0 -> WarningAmber
                    else -> SafeGreen
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(suhuColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = "Suhu",
                                tint = suhuColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SUHU",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "%.1f °C".format(suhuVal),
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (suhuVal > 50.0) "Suhu Sangat Tinggi!" else "Suhu Lingkungan",
                        color = if (suhuVal > 50.0) DangerRed else TextMuted,
                        fontSize = 11.sp
                    )
                }

                // Kelembapan Column
                val kelembapanVal = sensor.kelembapan
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(AccentPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Kelembapan",
                                tint = AccentPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "KELEMBAPAN",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "%.1f %%".format(kelembapanVal),
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Kelembaban Relatif",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
