package com.gfreeman.takeme.home.model.map.api

import com.gfreeman.takeme.home.model.map.RouteSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface RoutesServiceApi {
    @GET(ROUTE_PATH)
    suspend fun getRoute(
        @Query(ORIGIN_QUERY) origin: String,
        @Query(DESTINATION_QUERY) destination: String,
        @Header(HEADER_API_KEY) apiKey: String = ROUTES_API_KEY,
        @Header(HEADER_HOST) apiHost: String = ROUTES_HOST
       ): Response<RouteSearchResponse>
}

const val ROUTE_PATH = "/FindDrivingPath"
const val ORIGIN_QUERY = "origin"
const val DESTINATION_QUERY = "destination"
const val HEADER_API_KEY = "X-RapidAPI-Key"
const val HEADER_HOST = "X-RapidAPI-Host"

const val ROUTES_API_KEY = "f953d4fd41msh77cf91cc5f23254p1beacejsn7a481d36b596"
const val ROUTES_HOST = "trueway-directions2.p.rapidapi.com"
