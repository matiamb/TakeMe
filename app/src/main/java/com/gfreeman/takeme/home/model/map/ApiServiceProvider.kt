package com.gfreeman.takeme.home.model.map

import android.util.Log
import com.gfreeman.takeme.home.model.map.api.RoutesServiceApi
import com.gfreeman.takeme.home.model.map.api.SearchServiceApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceProvider {
    private const val SEARCH_SERVICE_BASE_URL = "https://nominatim.openstreetmap.org"
    private const val ROUTES_SERVICE_BASE_URL = "https://trueway-directions2.p.rapidapi.com"
    val searchServiceApi: SearchServiceApi = getRetrofitInstance(SEARCH_SERVICE_BASE_URL).create(SearchServiceApi::class.java)
    val routesServiceApi: RoutesServiceApi =
        getRetrofitInstance(ROUTES_SERVICE_BASE_URL).create(RoutesServiceApi::class.java)
    private val defaultInterceptor = getDefaultInterceptor()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(defaultInterceptor)
        .build()
    private fun getRetrofitInstance(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private fun getDefaultInterceptor(): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()

            val requestBodyString = request.body()?.let { body ->
                val buffer = Buffer()
                body.writeTo(buffer)
                buffer.readUtf8()
            } ?: ""

            val requestLog = """
            URL: ${request.url()}
            Method: ${request.method()}
            Headers: ${request.headers()}
            Body: $requestBodyString
        """.trimIndent()

            Log.d("InterceptorLogger", requestLog)

            chain.proceed(request)
        }
    }
}