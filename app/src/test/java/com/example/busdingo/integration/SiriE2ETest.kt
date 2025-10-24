package com.example.busdingo.integration

import com.example.busdingo.BuildConfig
import com.example.busdingo.data.StopMonitoringRepository
import com.example.busdingo.network.SiriApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Test

// ✨ 新增：导入 Robolectric
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertNotNull

// ✨ 新增：告诉 JUnit 用 Robolectric 跑，而不是普通 JVM Runner
@RunWith(RobolectricTestRunner::class)
// ✨ 新增：指定一个 Android SDK 版本（任意合适的就行）
@Config(sdk = [33])

class SiriE2ETest {

    @Test
    fun fetchEtaForLine() = runBlocking {
        val key = BuildConfig.METRO_API_KEY
        require(key.isNotBlank()) { "请配置 METRO_API_KEY" }

        val api = SiriApi.create(key)
        val repo = StopMonitoringRepository(api)

        val stopCode = "53088" // 示例站点
        val line = "60"        // 示例线路

        val arrivals = repo.getEtaForLine(stopCode, line)
        if (arrivals.isEmpty()) {
            println("当前站点/线路暂无班次")
        } else {
            val first = arrivals.first()
            println("Line ${first.line} → ETA(min) = ${first.etaMinutes}")
            assertNotNull(first.etaMinutes)
        }
    }
}
