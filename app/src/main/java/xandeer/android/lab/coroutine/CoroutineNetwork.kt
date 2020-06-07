package xandeer.android.lab.coroutine

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import xandeer.android.lab.coroutine.util.SkipNetworkInterceptor

private val service: CoroutineNetwork by lazy {
  val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(SkipNetworkInterceptor())
    .build()

  val retrofit = Retrofit.Builder()
    .baseUrl("http://localhost/")
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

  retrofit.create(CoroutineNetwork::class.java)
}

fun getNetworkService() = service

/**
 * Main network interface which will fetch a new welcome title for us
 */
interface CoroutineNetwork {
  @GET("next_title.json")
  suspend fun fetchNextTitle(): String
}


