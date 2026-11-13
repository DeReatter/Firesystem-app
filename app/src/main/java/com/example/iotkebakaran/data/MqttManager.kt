package com.example.iotkebakaran.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.UUID

class MqttManager(
    private var brokerUrl: String = "ssl://lcbb05ee.ala.asia-southeast1.emqxsl.com:8883",
    var topicPrefix: String = "fire_system"
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var mqttClient: MqttClient? = null
    private var reconnectJob: Job? = null

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting: StateFlow<Boolean> = _isConnecting.asStateFlow()

    private val _telemetryFlow = MutableSharedFlow<TelemetryData>(extraBufferCapacity = 64)
    val telemetryFlow: SharedFlow<TelemetryData> = _telemetryFlow.asSharedFlow()

    private val _alarmFlow = MutableSharedFlow<String>(extraBufferCapacity = 64)
    val alarmFlow: SharedFlow<String> = _alarmFlow.asSharedFlow()

    private val _errorFlow = MutableSharedFlow<String>(extraBufferCapacity = 16)
    val errorFlow: SharedFlow<String> = _errorFlow.asSharedFlow()

    private val clientId = "Android_FireMonitor_" + UUID.randomUUID().toString().take(6)

    fun connect(newPrefix: String = topicPrefix) {
        this.topicPrefix = newPrefix.trim().removePrefix("/").removeSuffix("/")
        reconnectJob?.cancel()

        scope.launch {
            _isConnecting.value = true
            doConnect()
        }
    }

    private fun doConnect() {
        try {
            mqttClient?.let {
                if (it.isConnected) {
                    try { it.disconnect() } catch (_: Exception) {}
                }
            }

            val client = MqttClient(brokerUrl, clientId, MemoryPersistence())
            mqttClient = client

            val options = MqttConnectOptions().apply {
                isCleanSession = true
                isAutomaticReconnect = true
                connectionTimeout = 15
                keepAliveInterval = 30
                userName = "esp32-firesystem"
                password = "12233445".toCharArray()
            }

            client.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                    Log.d("MqttManager", "Connect complete (TLS): reconnect=$reconnect to $serverURI")
                    _isConnected.value = true
                    _isConnecting.value = false

                    // Subskripsi otomatis setiap kali terhubung atau reconnect
                    try {
                        val telemetryTopic = "$topicPrefix/telemetry"
                        val alarmTopic = "$topicPrefix/alarm"
                        client.subscribe(arrayOf(telemetryTopic, alarmTopic), intArrayOf(1, 1))
                        Log.d("MQTT_SUB", "Berhasil subscribe ke topik: $telemetryTopic & $alarmTopic")
                    } catch (e: Exception) {
                        Log.e("MQTT_SUB", "Gagal subscribe di connectComplete", e)
                    }
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.w("MqttManager", "Connection lost (TLS): ${cause?.message}")
                    _isConnected.value = false
                    _isConnecting.value = false
                    startAutoReconnect()
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    if (topic == null || message == null) return
                    val payload = message.toString()
                    Log.d("MQTT_MSG", "Topik: $topic | Payload: $payload")

                    val telemetryTopic = "$topicPrefix/telemetry"
                    val alarmTopic = "$topicPrefix/alarm"

                    when (topic) {
                        telemetryTopic -> {
                            val data = TelemetryData.fromJson(payload)
                            _telemetryFlow.tryEmit(data)
                        }
                        alarmTopic -> {
                            _alarmFlow.tryEmit(payload)
                        }
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d("MqttManager", "Delivery complete: ${token?.messageId}")
                }
            })

            client.connect(options)

        } catch (e: Exception) {
            Log.e("MqttManager", "TLS Connection failed", e)
            _isConnected.value = false
            _isConnecting.value = false
            _errorFlow.tryEmit("Gagal terhubung TLS EMQX: ${e.localizedMessage}")
            startAutoReconnect()
        }
    }

    fun publishPumpControl(command: String) {
        scope.launch {
            try {
                val client = mqttClient
                if (client == null || !client.isConnected) {
                    _errorFlow.tryEmit("Gagal publikasi: MQTT TLS belum terhubung")
                    return@launch
                }
                val controlTopic = "$topicPrefix/control"
                val message = MqttMessage(command.toByteArray()).apply {
                    qos = 1
                    isRetained = false
                }
                client.publish(controlTopic, message)
                Log.d("MQTT_PUB", "Published '$command' to $controlTopic (QoS 1, retained=false)")
            } catch (e: Exception) {
                Log.e("MQTT_PUB", "Publish failed", e)
                _errorFlow.tryEmit("Gagal kirim perintah pompa: ${e.localizedMessage}")
            }
        }
    }

    private fun startAutoReconnect() {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            while (!_isConnected.value) {
                delay(5000L)
                if (!_isConnected.value && !_isConnecting.value) {
                    Log.d("MqttManager", "Attempting TLS auto-reconnect...")
                    _isConnecting.value = true
                    doConnect()
                }
            }
        }
    }

    fun disconnect() {
        reconnectJob?.cancel()
        scope.launch {
            try {
                mqttClient?.disconnect()
                _isConnected.value = false
            } catch (_: Exception) {}
        }
    }
}
