package com.example.busdingo.model

import java.time.OffsetDateTime

/**
 * 公交到站信息的数据模型。
 * 对应 SIRI StopMonitoring 返回的一条 MonitoredStopVisit。
 *
 * @param line            线路号（PublishedLineName 或 LineRef）
 * @param destination     终点站名称
 * @param stopName        当前站点名称
 * @param expectedArrival 预计到站时间（绝对时间，ISO-8601）
 * @param expectedDeparture 预计离站时间（绝对时间，ISO-8601）
 * @param etaMinutes      到站剩余分钟（优先使用 JSON 里的 ETA 字段，否则回退到计算）
 * @param etdMinutes      离站剩余分钟（优先使用 JSON 里的 ETD 字段，否则回退到计算）
 */
data class Arrival(
    val line: String,
    val destination: String?,
    val stopName: String?,
    val expectedArrival: OffsetDateTime?,
    val expectedDeparture: OffsetDateTime?,
    val etaMinutes: Double?,
    val etdMinutes: Double?
)
