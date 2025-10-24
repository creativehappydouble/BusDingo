package com.example.busdingo.parser

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33]) // 可选：指定一个运行时 SDK
class SiriParserTest {

    @Test
    fun use_ETA_from_JSON_when_present() {
        val json = """
        {"Siri":{"ServiceDelivery":{
          "ResponseTimestamp":"2025-10-19T21:05:00+13:00",
          "StopMonitoringDelivery":[{"MonitoredStopVisit":[
            {"MonitoredVehicleJourney":{
              "PublishedLineName":"60",
              "MonitoredCall":{
                "StopPointName":"Test Stop",
                "ExpectedArrivalTime":"2025-10-19T21:10:00+13:00",
                "ETA": 3.5
              }
            }}
          ]}]}}}
        """.trimIndent()

        val list = SiriParser.parseArrivalsForLine(json, "60")
        assertTrue(list.isNotEmpty())
        assertEquals(3.5, list.first().etaMinutes!!, 0.0001)
    }

    @Test
    fun fallback_compute_ETA_when_JSON_missing() {
        val json = """
        {"Siri":{"ServiceDelivery":{
          "ResponseTimestamp":"2025-10-19T21:05:00+13:00",
          "StopMonitoringDelivery":[{"MonitoredStopVisit":[
            {"MonitoredVehicleJourney":{
              "PublishedLineName":"60",
              "MonitoredCall":{
                "StopPointName":"Test Stop",
                "ExpectedArrivalTime":"2025-10-19T21:10:00+13:00"
              }
            }}
          ]}]}}}
        """.trimIndent()

        val list = SiriParser.parseArrivalsForLine(json, "60")
        assertTrue(list.isNotEmpty())
        assertEquals(5.0, list.first().etaMinutes!!, 0.0001)
    }
}
