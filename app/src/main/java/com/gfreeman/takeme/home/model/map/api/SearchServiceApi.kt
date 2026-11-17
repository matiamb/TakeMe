package com.gfreeman.takeme.home.model.map.api

import com.gfreeman.takeme.home.model.map.PlaceSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SearchServiceApi {
    @Headers("User-Agent: TakeMe-Android-App")
    @GET("/search")
    suspend fun getPlacesFromSearch(
        @Query("q") placeToSearch: String,
        @Query("format") format: String = "jsonv2"
    ): Response<List<PlaceSearchResponse>>

}