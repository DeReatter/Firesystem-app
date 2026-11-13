package com.example.iotkebakaran.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

data class SensorInfo(
    val suhu: Double = 0.0,
    val kelembapan: Double = 0.0,
    val mq2Raw: Int = 0,
    val dhtStatus: Int = 1
)

data class EnergiInfo(
    val vBaterai: Double = 0.0,
    val presBaterai: Double = 0.0,
    val vPv: Double = 0.0,
    val iPv: Double = 0.0,
    val pPv: Double = 0.0,
    val inaStatus: Int = 1
)

data class TelemetryData(
    val deviceId: String = "FIRE_NODE_UNY_01",
    val timestamp: Long = 0L,
    val waktuWib: String = "-",
    val statusKebakaran: String = "AMAN",
    val pumpStatus: String = "OFF",
    val sensor: SensorInfo = SensorInfo(),
    val energi: EnergiInfo = EnergiInfo()
) {
    val isBahaya: Boolean
        get() = statusKebakaran.equals("BAHAYA", ignoreCase = true) ||
                sensor.mq2Raw > 2000 ||
                sensor.suhu > 50.0

    companion object {
        private val gson = Gson()

        fun fromJson(jsonString: String): TelemetryData {
            return try {
                val json = JSONObject(jsonString)

                val deviceId = json.optString("device_id", "FIRE_NODE_UNY_01")
                val timestamp = json.optLong("timestamp", 0L)
                val waktuWib = json.optString("waktu_wib", "-")
                val statusKebakaran = json.optString("status_kebakaran", "AMAN")
                val pumpStatus = json.optString("pump_status", "OFF")

                val sensor = if (json.has("sensor")) {
                    val sensorObj = json.getJSONObject("sensor")
                    SensorInfo(
                        suhu = sensorObj.optDouble("suhu", 0.0),
                        kelembapan = sensorObj.optDouble("kelembapan", 0.0),
                        mq2Raw = sensorObj.optInt("mq2_raw", 0),
                        dhtStatus = sensorObj.optInt("dht_status", 1)
                    )
                } else SensorInfo()

                val energi = if (json.has("energi")) {
                    val energiObj = json.getJSONObject("energi")
                    EnergiInfo(
                        vBaterai = energiObj.optDouble("v_baterai", 0.0),
                        presBaterai = energiObj.optDouble("pres_baterai", 0.0),
                        vPv = energiObj.optDouble("v_pv", 0.0),
                        iPv = energiObj.optDouble("i_pv", 0.0),
                        pPv = energiObj.optDouble("p_pv", 0.0),
                        inaStatus = energiObj.optInt("ina_status", 1)
                    )
                } else EnergiInfo()

                if (deviceId.isEmpty() && timestamp == 0L) {
                    parseWithGson(jsonString)
                } else {
                    TelemetryData(
                        deviceId = if (deviceId.isEmpty()) "FIRE_NODE_UNY_01" else deviceId,
                        timestamp = timestamp,
                        waktuWib = if (waktuWib.isEmpty()) "-" else waktuWib,
                        statusKebakaran = if (statusKebakaran.isEmpty()) "AMAN" else statusKebakaran,
                        pumpStatus = if (pumpStatus.isEmpty()) "OFF" else pumpStatus,
                        sensor = sensor,
                        energi = energi
                    )
                }
            } catch (e: Exception) {
                Log.w("TelemetryData", "JSONObject parsing fallback to Gson: ${e.message}")
                parseWithGson(jsonString)
            }
        }

        private fun parseWithGson(jsonString: String): TelemetryData {
            return try {
                val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
                val deviceId = jsonObject.get("device_id")?.asString ?: "FIRE_NODE_UNY_01"
                val timestamp = jsonObject.get("timestamp")?.asLong ?: 0L
                val waktuWib = jsonObject.get("waktu_wib")?.asString ?: "-"
                val statusKebakaran = jsonObject.get("status_kebakaran")?.asString ?: "AMAN"
                val pumpStatus = jsonObject.get("pump_status")?.asString ?: "OFF"

                val sensorObj = jsonObject.getAsJsonObject("sensor")
                val sensor = if (sensorObj != null) {
                    SensorInfo(
                        suhu = sensorObj.get("suhu")?.asDouble ?: 0.0,
                        kelembapan = sensorObj.get("kelembapan")?.asDouble ?: 0.0,
                        mq2Raw = sensorObj.get("mq2_raw")?.asInt ?: 0,
                        dhtStatus = sensorObj.get("dht_status")?.asInt ?: 1
                    )
                } else SensorInfo()

                val energiObj = jsonObject.getAsJsonObject("energi")
                val energi = if (energiObj != null) {
                    EnergiInfo(
                        vBaterai = energiObj.get("v_baterai")?.asDouble ?: 0.0,
                        presBaterai = energiObj.get("pres_baterai")?.asDouble ?: 0.0,
                        vPv = energiObj.get("v_pv")?.asDouble ?: 0.0,
                        iPv = energiObj.get("i_pv")?.asDouble ?: 0.0,
                        pPv = energiObj.get("p_pv")?.asDouble ?: 0.0,
                        inaStatus = energiObj.get("ina_status")?.asInt ?: 1
                    )
                } else EnergiInfo()

                TelemetryData(
                    deviceId = deviceId,
                    timestamp = timestamp,
                    waktuWib = waktuWib,
                    statusKebakaran = statusKebakaran,
                    pumpStatus = pumpStatus,
                    sensor = sensor,
                    energi = energi
                )
            } catch (_: Exception) {
                TelemetryData()
            }
        }
    }
}
