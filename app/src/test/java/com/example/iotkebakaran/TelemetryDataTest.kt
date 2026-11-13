package com.example.iotkebakaran

import com.example.iotkebakaran.data.TelemetryData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TelemetryDataTest {

    @Test
    fun testMqttJsonParsing_SampleFromPrompt() {
        val json = """
            {
              "device_id": "FIRE_NODE_UNY_01",
              "timestamp": 1773229200,
              "waktu_wib": "2026-11-13 15:20:00",
              "status_kebakaran": "AMAN",
              "pump_status": "OFF",
              "sensor": {
                "suhu": 28.4,
                "kelembapan": 65.2,
                "mq2_raw": 450,
                "dht_status": 1
              },
              "energi": {
                "v_baterai": 12.80,
                "pres_baterai": 63.6,
                "v_pv": 18.20,
                "i_pv": 320.5,
                "p_pv": 5.83,
                "ina_status": 1
              }
            }
        """.trimIndent()

        val data = TelemetryData.fromJson(json)

        assertEquals("FIRE_NODE_UNY_01", data.deviceId)
        assertEquals(1773229200L, data.timestamp)
        assertEquals("2026-11-13 15:20:00", data.waktuWib)
        assertEquals("AMAN", data.statusKebakaran)
        assertEquals("OFF", data.pumpStatus)

        // Sensor
        assertEquals(28.4, data.sensor.suhu, 0.001)
        assertEquals(65.2, data.sensor.kelembapan, 0.001)
        assertEquals(450, data.sensor.mq2Raw)
        assertEquals(1, data.sensor.dhtStatus)

        // Energi
        assertEquals(12.80, data.energi.vBaterai, 0.001)
        assertEquals(63.6, data.energi.presBaterai, 0.001)
        assertEquals(18.20, data.energi.vPv, 0.001)
        assertEquals(320.5, data.energi.iPv, 0.001)
        assertEquals(5.83, data.energi.pPv, 0.001)
        assertEquals(1, data.energi.inaStatus)

        assertFalse(data.isBahaya)
    }

    @Test
    fun testIsBahaya_Triggers() {
        val safeJson = """
            {
              "status_kebakaran": "AMAN",
              "sensor": { "suhu": 28.4, "mq2_raw": 450 }
            }
        """.trimIndent()
        assertFalse(TelemetryData.fromJson(safeJson).isBahaya)

        val dangerStatusJson = """
            {
              "status_kebakaran": "BAHAYA",
              "sensor": { "suhu": 25.0, "mq2_raw": 300 }
            }
        """.trimIndent()
        assertTrue(TelemetryData.fromJson(dangerStatusJson).isBahaya)

        val highGasJson = """
            {
              "status_kebakaran": "AMAN",
              "sensor": { "suhu": 25.0, "mq2_raw": 2100 }
            }
        """.trimIndent()
        assertTrue(TelemetryData.fromJson(highGasJson).isBahaya)

        val highTempJson = """
            {
              "status_kebakaran": "AMAN",
              "sensor": { "suhu": 51.2, "mq2_raw": 300 }
            }
        """.trimIndent()
        assertTrue(TelemetryData.fromJson(highTempJson).isBahaya)
    }
}
