package home.model.map


import contract.MapContract

class MapRepository: MapContract.MapModel {
    override fun getPlacesFromSearch(placeToSearch: String): List<Place> {
        //TODO("Not yet implemented")
        return listOf(
            Place(
                "Abasto, Balvanera, Buenos Aires, Comuna 3, Autonomous City of Buenos Aires, C1193AAF, Argentina",
                Point("-34.6037283".toDouble(), "-58.4125926".toDouble())
            )
        )
    }

    override fun getRoute(startPlace: Place, destination: Place): List<Point> {
        //TODO("Not yet implemented")
        return listOf(
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
        )
    }

    override fun getCurrentPosition(): Point {
        //TODO("Not yet implemented")
        return Point(-34.679437, -58.553777)
    }

    override fun getResult(search: String): String {
        return "Test text from backend"
    }

}