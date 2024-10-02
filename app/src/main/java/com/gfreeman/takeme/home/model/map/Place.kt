package com.gfreeman.takeme.home.model.map

import java.io.Serializable

data class Place(
    var displayName: String,
    val point: Point
): Serializable