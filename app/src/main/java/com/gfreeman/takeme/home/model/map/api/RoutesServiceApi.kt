package com.gfreeman.takeme.home.model.map.api

import com.gfreeman.takeme.home.model.map.RouteSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RoutesServiceApi {
    @GET("$ROUTE_PATH/{coordinates}")
    suspend fun getRoute(
        @Path(value = "coordinates", encoded = true)
        coordinates: String,
        @Query("overview")
        overview: String = "full",
        @Query("geometries")
        geometries: String = "geojson"
       ): Response<RouteSearchResponse>
}

const val ROUTE_PATH = "/route/v1/driving"
