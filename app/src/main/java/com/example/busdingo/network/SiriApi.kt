package com.example.busdingo.network

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ① 定义“接口”——把 HTTP 映射成 Kotlin 方法
interface SiriApi {

    // 对应：GET https://apis.metroinfo.co.nz/rti/siri/v1/sm?stopcode=xxx&MaximumStopVisits=10
    @GET("rti/siri/v1/sm")
    suspend fun stopMonitoringByStopCode(
        @Query("stopcode") stopCode: String,
        @Query("MaximumStopVisits") maxVisits: Int = 10
    ): Response<ResponseBody>

    companion object {
        // ② 提供一个工厂方法，统一创建 Retrofit 实例，并注入拦截器
        fun create(subscriptionKey: String): SiriApi {
            // ②-1 OkHttp 拦截器：自动加鉴权 Header
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val req = chain.request().newBuilder()
                        .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                        .build()
                    chain.proceed(req)
                }
                // （可选）再加日志拦截器便于调试：
                // .addInterceptor(HttpLoggingInterceptor().apply {
                //     level = HttpLoggingInterceptor.Level.BODY
                // })
                .build()

            // ②-2 Retrofit：指定基地址 + 转换器
            return Retrofit.Builder()
                .baseUrl("https://apis.metroinfo.co.nz/") // 注意末尾要有斜杠
                .client(client)
                // 先用纯文本拿回原始响应（XML/JSON 都能先看清楚）
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(SiriApi::class.java)
        }
    }
}
