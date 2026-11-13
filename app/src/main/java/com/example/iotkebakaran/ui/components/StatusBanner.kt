package com.example.iotkebakaran.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iotkebakaran.data.TelemetryData
import com.example.iotkebakaran.ui.theme.DangerBg
import com.example.iotkebakaran.ui.theme.DangerBorder
import com.example.iotkebakaran.ui.theme.DangerRed
import com.example.iotkebakaran.ui.theme.SafeBg
import com.example.iotkebakaran.ui.theme.SafeBorder
import com.example.iotkebakaran.ui.theme.SafeGreen

@Composable
fun StatusBanner(
    telemetry: TelemetryData,
    isMuted: Boolean,
    onToggleMute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBahaya = telemetry.isBahaya

    if (isBahaya) {
        val infiniteTransition = rememberInfiniteTransition(label = "PulseTransition")
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "PulseAlpha"
        )

        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DangerBg.copy(alpha = 0.95f)
            ),
            border = BorderStroke(2.dp, DangerRed.copy(alpha = pulseAlpha))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(DangerRed.copy(alpha = 0.25f * pulseAlpha)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Peringatan Bahaya",
                                tint = DangerRed,
                                modifier = Modifier
                                    .size(28.dp)
                                    .alpha(pulseAlpha)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Surface(
                                color = DangerRed.copy(alpha = pulseAlpha),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = " BAHAYA TERDETEKSI ",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "BAHAYA: TERDETEKSI POTENSI KEBAKARAN",
                                color = DangerRed,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(40.dp)
                            .background(DangerBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = if (isMuted) "Mute Alarm" else "Aktif Alarm",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                val triggers = mutableListOf<String>()
                if (telemetry.statusKebakaran.equals("BAHAYA", ignoreCase = true)) {
                    triggers.add("Status: BAHAYA")
                }
                if (telemetry.sensor.mq2Raw > 2000) {
                    triggers.add("Gas MQ-2: ${telemetry.sensor.mq2Raw} ADC")
                }
                if (telemetry.sensor.suhu > 50.0) {
                    triggers.add("Suhu: ${telemetry.sensor.suhu}°C")
                }

                Text(
                    text = "Indikator Pemicu: " + triggers.joinToString(" | "),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = SafeBg
            ),
            border = BorderStroke(1.5.dp, SafeBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SafeGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Aman",
                        tint = SafeGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Surface(
                        color = SafeGreen,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = " AMAN ",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "KONDISI NORMAL & AMAN",
                        color = SafeGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
