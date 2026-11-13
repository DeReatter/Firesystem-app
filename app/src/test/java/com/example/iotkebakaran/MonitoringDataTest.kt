package com.example.iotkebakaran

import com.example.iotkebakaran.data.MonitoringData
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MonitoringDataTest {

    private val gson = Gson()

    @Test
    fun testJsonParsing_SampleFromPrompt() {
        val json = """
            {
              "v_baterai": 12.80,
              "pres_baterai": 63.6,
              "mq2_raw": 450,
              "suhu": 28.4,
              "kelembapan": 65.2,
              "dht_status": 1,
              "v_pv": 18.20,
              "i_pv": 320.5,
              "p_pv": 5.83,
              "ina_status": 1,
              "status_kebakaran": "AMAN"
            }
        """.trimIndent()

        val data = gson.fromJson(json, MonitoringData::class.java)

        assertEquals(12.80, data.vBaterai, 0.001)
        assertEquals(63.6, data.presBaterai, 0.001)
        assertEquals(450, data.mq2Raw)
        assertEquals(28.4, data.suhu, 0.001)
        assertEquals(65.2, data.kelembapan, 0.001)
        assertEquals(1, data.dhtStatus)
        assertEquals(18.20, data.vPv, 0.001)
        assertEquals(320.5, data.iPv, 0.001)
        assertEquals(5.83, data.pPv, 0.001)
        assertEquals(1, data.inaStatus)
        assertEquals("AMAN", data.statusKebakaran)
        assertFalse(data.isBahaya)
    }

    @Test
    fun testIsBahaya_Triggers() {
        // Safe condition
        val safeData = MonitoringData(
            mq2Raw = 450,
            suhu = 28.4,
            statusKebakaran = "AMAN"
        )
        assertFalse(safeData.isBahaya)

        // Danger by status string
        val dangerStatus = MonitoringData(
            mq2Raw = 100,
            suhu = 25.0,
            statusKebakaran = "BAHAYA"
        )
        assertTrue(dangerStatus.isBahaya)

        // Danger by high gas MQ2 > 2000
        val dangerGas = MonitoringData(
            mq2Raw = 2100,
            suhu = 25.0,
            statusKebakaran = "AMAN"
        )
        assertTrue(dangerGas.isBahaya)

        // Danger by high temperature > 50°C
        val dangerTemp = MonitoringData(
            mq2Raw = 500,
            suhu = 51.5,
            statusKebakaran = "AMAN"
        )
        assertTrue(dangerTemp.isBahaya)
    }
}
