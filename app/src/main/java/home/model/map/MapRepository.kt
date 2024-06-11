package home.model.map


import contract.MapContract

class MapRepository: MapContract.MapModel {
    override suspend fun getPlacesFromSearch(placeToSearch: String): List<Place> {
        /*return listOf(
            Place(
                "Abasto, Balvanera, Buenos Aires, Comuna 3, Autonomous City of Buenos Aires, C1193AAF, Argentina",
                Point("-34.6037283".toDouble(), "-58.4125926".toDouble())
            )
        )*/
        return ApiServiceProvider.searchServiceApi.getPlacesFromSearch(placeToSearch = placeToSearch)
            .body()?.map{
            val convertedLong = it.long.toDouble()
            val convertedLat = it.lat.toDouble()
            Place(displayName = it.displayName, point = Point(latitude = convertedLat, longitude = convertedLong))
        }?: emptyList()
    }

    override suspend fun getRoute(startPlace: Place, destination: Place): List<Point> {
        /*return listOf(
            Point(-34.679437, -58.553777),
            Point(
                -34.679217,
                -58.553513
            ),
            Point(
                -34.678996,
                -58.553279
            ),
            Point(
                -34.678119,
                -58.554457
            ),
            Point(
                -34.677234,
                -58.55561
            ),
            Point(
                -34.676409,
                -58.556671
            )
        )*/
        val rawRouteResponse = ApiServiceProvider.routesServiceApi.getRoute(
            getFormatedPoints(startPlace),
            getFormatedPoints(destination)
        )
        return if (rawRouteResponse.isSuccessful){
            mapRoute(rawRouteResponse.body()?.route?.geometry?.coordinates)
        } else {
            emptyList()
        }
    }

    private fun getFormatedPoints(place: Place): String =
        "${place.point.latitude}, ${place.point.longitude}"

    //TODO No borra la ruta vieja
    private fun mapRoute(coordinates: List<List<Double>>?): List<Point> =
        coordinates?.map{Point(it.first(), it.last())}?: emptyList()
    override fun getCurrentPosition(): Point {
        //TODO("Not yet implemented")
        return Point(-34.679437, -58.553777)
    }

    override fun getResult(search: String): String {
        return "Test text from backend"
    }

}