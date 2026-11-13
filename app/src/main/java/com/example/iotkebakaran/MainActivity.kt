package com.example.iotkebakaran

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.iotkebakaran.ui.DashboardScreen
import com.example.iotkebakaran.ui.MqttViewModel
import com.example.iotkebakaran.ui.theme.IOTKebakaranTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MqttViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IOTKebakaranTheme {
                DashboardScreen(viewModel = viewModel)
            }
        }
    }
}
