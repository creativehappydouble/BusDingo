package com.example.busdingo.network

import com.example.busdingo.BuildConfig
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertTrue   // ✅ 用 JUnit4 的 assertTrue
import org.junit.Test
import retrofit2.Response

class SiriApiTest {

    @Test
    fun testStopMonitoringByStopCodeApiKey() = runBlocking {
        println("KEY from BuildConfig: '${BuildConfig.METRO_API_KEY}'")

        val key = BuildConfig.METRO_API_KEY

        // 如果 key 为空，则测试直接失败（不会 ignored）
        assertTrue(
            "BuildConfig.METRO_API_KEY 为空，请在 local.properties 配置 METRO_API_KEY",
            key.isNotBlank()
        )

        // 如果 key 有值，可以继续写接口调用
        val api = SiriApi.create(key)
        val stopCode = "53088"
        val response: Response<ResponseBody> = api.stopMonitoringByStopCode(stopCode, 3)

        assertTrue("请求失败，HTTP ${response.code()}", response.isSuccessful)

        val body = response.body()?.string().orEmpty()
        println("响应前 300 字：\n${body.take(300)}")

        assertTrue("返回内容不包含 Siri 节点", body.contains("Siri"))
    }
}
