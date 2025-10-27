// New: network/ApiService.kt
interface ApiService {
    @GET("/eta")
    suspend fun eta(
        @Query("stop_id") stopId: String,
        @Query("route_id") routeId: String,
        @Query("direction") direction: String? = null
    ): EtaResp
}

// New: network/ApiClient.kt
object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8080" // 开发期；线上换成你的 HTTPS

    private val http = OkHttpClient.Builder()
        // .addInterceptor(HttpLoggingInterceptor().setLevel(BODY)) // 可选
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(http)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// New: model/EtaResp.kt  —— 字段名尽量和 Server 保持一致
data class EtaResp(
    val stop_id: String,
    val route_id: String,
    val direction: String?,
    val etaMin: Int?,            // ← 旧的 arrivalMinutes 换名或用 @Json
    val stopsLeft: Int?,
    val vehicleId: String?,
    val asOf: String,
    val source: String,
    val ttlSec: Int,
    val note: String?
)

// New: data/TransitRepository.kt  —— 保持方法签名不变，让 UI 无感
class TransitRepository {
    suspend fun queryEta(stopId: String, routeId: String, direction: String? = null): EtaResp =
        ApiClient.api.eta(stopId, routeId, direction)
}
