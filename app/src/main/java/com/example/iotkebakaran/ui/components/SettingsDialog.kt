package com.example.iotkebakaran.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iotkebakaran.ui.theme.AccentPrimary
import com.example.iotkebakaran.ui.theme.CardSurface
import com.example.iotkebakaran.ui.theme.CardSurfaceVariant
import com.example.iotkebakaran.ui.theme.TextMuted
import com.example.iotkebakaran.ui.theme.TextPrimary
import com.example.iotkebakaran.ui.theme.TextSecondary

@Composable
fun SettingsDialog(
    currentPrefix: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var prefixText by remember { mutableStateOf(currentPrefix) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    onSave(prefixText)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentPrimary)
            ) {
                Text("Simpan & Hubungkan", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextSecondary)
            }
        },
        title = {
            Text(
                text = "Pengaturan Topik MQTT",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Ubah awalan topik (prefix) MQTT untuk komunikasi dengan Node ESP32.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = prefixText,
                    onValueChange = { prefixText = it },
                    label = { Text("Awalan Topik (Default: fire_system)", color = TextMuted) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = AccentPrimary,
                        unfocusedBorderColor = CardSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Topik aktif:\n" +
                            "• Telemetri: $prefixText/telemetry\n" +
                            "• Alarm: $prefixText/alarm\n" +
                            "• Kontrol: $prefixText/control",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        },
        containerColor = CardSurface,
        titleContentColor = TextPrimary
    )
}
