package com.example.busdingo.data


import com.example.busdingo.model.Arrival
import com.example.busdingo.network.SiriApi
import com.example.busdingo.parser.SiriParser
import okhttp3.ResponseBody

class StopMonitoringRepository(private val api: SiriApi) {

    suspend fun getEtaForLine(stopCode: String, line: String, maxVisits: Int = 10): List<Arrival> {
        val response = api.stopMonitoringByStopCode(stopCode, maxVisits)
        if (!response.isSuccessful) return emptyList()

        val raw = response.body()?.string() ?: return emptyList()
        return SiriParser.parseArrivalsForLine(raw, line)
    }
}
