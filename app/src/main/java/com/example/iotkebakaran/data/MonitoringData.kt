package com.example.iotkebakaran.data

import com.google.gson.annotations.SerializedName

data class MonitoringData(
    @SerializedName("v_baterai") val vBaterai: Double = 0.0,
    @SerializedName("pres_baterai") val presBaterai: Double = 0.0,
    @SerializedName("mq2_raw") val mq2Raw: Int = 0,
    @SerializedName("suhu") val suhu: Double = 0.0,
    @SerializedName("kelembapan") val kelembapan: Double = 0.0,
    @SerializedName("dht_status") val dhtStatus: Int = 1,
    @SerializedName("v_pv") val vPv: Double = 0.0,
    @SerializedName("i_pv") val iPv: Double = 0.0,
    @SerializedName("p_pv") val pPv: Double = 0.0,
    @SerializedName("ina_status") val inaStatus: Int = 1,
    @SerializedName("status_kebakaran") val statusKebakaran: String = "AMAN"
) {
    val isBahaya: Boolean
        get() = statusKebakaran.equals("BAHAYA", ignoreCase = true) || mq2Raw > 2000 || suhu > 50.0
}
