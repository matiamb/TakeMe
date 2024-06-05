package home.model.map

import home.model.map.api.SearchServiceApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiServiceProvider {
    private const val SEARCH_SERVICE_BASE_URL = "https://nominatim.openstreetmap.org"
    val searchServiceApi: SearchServiceApi = getRetrofitInstance(SEARCH_SERVICE_BASE_URL).create(SearchServiceApi::class.java)
    private fun getRetrofitInstance(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}