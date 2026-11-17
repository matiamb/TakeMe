package com.gfreeman.takeme.home.model.map

data class RouteSearchResponse (
    val code: String,
    val routes: List<OsrmRoute>
)
data class OsrmRoute(
    val distance: Double,
    val duration: Double,
    val geometry: GeometryResponse
)
data class GeometryResponse (
    val coordinates: List<List<Double>>
)
