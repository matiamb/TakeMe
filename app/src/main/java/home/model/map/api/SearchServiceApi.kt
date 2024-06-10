package home.model.map.api

import home.model.map.PlaceSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchServiceApi {
    @GET("/search.php")
    suspend fun getPlacesFromSearch(
        @Query("q") placeToSearch: String,
        @Query("format") format: String = "jsonv2"
    ): Response<List<PlaceSearchResponse>>

}