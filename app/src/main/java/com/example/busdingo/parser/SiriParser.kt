package com.example.busdingo.parser

import com.example.busdingo.model.Arrival
import org.json.JSONObject
import java.time.Duration
import java.time.OffsetDateTime
import kotlin.math.round

object SiriParser {

    fun parseArrivalsForLine(rawJson: String, targetLine: String): List<Arrival> {
        val root = JSONObject(rawJson)
        val siri = root.optJSONObject("Siri") ?: return emptyList()
        val serviceDelivery = siri.optJSONObject("ServiceDelivery") ?: return emptyList()

        val responseTs = serviceDelivery.optString("ResponseTimestamp", null)
            ?.let { OffsetDateTime.parse(it) }

        val smArray = serviceDelivery.optJSONArray("StopMonitoringDelivery") ?: return emptyList()
        if (smArray.length() == 0) return emptyList()

        val delivery = smArray.optJSONObject(0)
        val visits = delivery?.optJSONArray("MonitoredStopVisit") ?: return emptyList()

        val out = mutableListOf<Arrival>()

        for (i in 0 until visits.length()) {
            val v = visits.optJSONObject(i) ?: continue
            val mvj = v.optJSONObject("MonitoredVehicleJourney") ?: continue

            val lineName = mvj.optString("PublishedLineName",
                mvj.optString("LineRef", "")
            )
            if (lineName != targetLine) continue

            val dest = mvj.optString("DestinationName", null)
            val mc = mvj.optJSONObject("MonitoredCall")
            val stopName = mc?.optString("StopPointName", null)

            val expArr = mc?.optString("ExpectedArrivalTime", null)?.let { OffsetDateTime.parse(it) }
            val expDep = mc?.optString("ExpectedDepartureTime", null)?.let { OffsetDateTime.parse(it) }

            // JSON 自带 ETA/ETD
            val etaFromJson = mc?.optDouble("ETA", Double.NaN)
            val etdFromJson = mc?.optDouble("ETD", Double.NaN)

            // 判断：如果没有就计算
            val etaMin = if (etaFromJson != null && !etaFromJson.isNaN()) {
                etaFromJson
            } else {
                etaInMinutes(responseTs, expArr)
            }

            val etdMin = if (etdFromJson != null && !etdFromJson.isNaN()) {
                etdFromJson
            } else {
                etaInMinutes(responseTs, expDep)
            }

            out += Arrival(
                line = lineName,
                destination = dest,
                stopName = stopName,
                expectedArrival = expArr,
                expectedDeparture = expDep,
                etaMinutes = etaMin,
                etdMinutes = etdMin
            )
        }

        return out.sortedWith(compareBy<Arrival> { it.etaMinutes == null }.thenBy { it.etaMinutes })
    }

    private fun etaInMinutes(from: OffsetDateTime?, to: OffsetDateTime?): Double? {
        if (from == null || to == null) return null
        val seconds = Duration.between(from, to).seconds.toDouble()
        return round((seconds / 60.0) * 10.0) / 10.0
    }
}
