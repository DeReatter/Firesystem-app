package com.example.iotkebakaran.ui.components

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iotkebakaran.ui.theme.AccentPrimary
import com.example.iotkebakaran.ui.theme.BorderColor
import com.example.iotkebakaran.ui.theme.CardSurface
import com.example.iotkebakaran.ui.theme.CardSurfaceVariant
import com.example.iotkebakaran.ui.theme.DangerRed
import com.example.iotkebakaran.ui.theme.TextMuted
import com.example.iotkebakaran.ui.theme.TextPrimary
import com.example.iotkebakaran.ui.theme.TextSecondary

@Composable
fun PumpControlCard(
    pumpStatus: String,
    isLoading: Boolean,
    onSendPumpCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPumpOn = pumpStatus.equals("ON", ignoreCase = true)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardSurface
        ),
        border = BorderStroke(
            1.5.dp,
            if (isPumpOn) AccentPrimary else BorderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val infiniteTransition = rememberInfiniteTransition(label = "PumpRotation")
                    val rotationAngle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1500, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        ),
                        label = "RotationAngle"
                    )

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPumpOn) AccentPrimary.copy(alpha = 0.2f) else CardSurfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Water,
                            contentDescription = "Pompa Air",
                            tint = if (isPumpOn) AccentPrimary else TextMuted,
                            modifier = Modifier
                                .size(24.dp)
                                .then(if (isPumpOn) Modifier.rotate(rotationAngle) else Modifier)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "KONTROL POMPA AIR DARURAT",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isPumpOn) "POMPA AKTIF / MENYALA" else "POMPA STANDBY / MATI",
                            color = if (isPumpOn) AccentPrimary else TextMuted,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = AccentPrimary,
                        strokeWidth = 2.5.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Switch / Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button ON
                Button(
                    onClick = { onSendPumpCommand("ON") },
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPumpOn) AccentPrimary else CardSurfaceVariant,
                        contentColor = if (isPumpOn) Color.Black else TextPrimary
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NYALAKAN (ON)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Button OFF
                OutlinedButton(
                    onClick = { onSendPumpCommand("OFF") },
                    enabled = !isLoading,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (!isPumpOn) DangerRed else BorderColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (!isPumpOn) DangerRed else TextSecondary
                    )
                ) {
                    Text(
                        text = "MATIKAN (OFF)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
