package com.gfreeman.takeme.home.model.db.converters

import androidx.room.TypeConverter
import com.gfreeman.takeme.home.model.map.Place
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaceConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromJson(json: String): Place {
        val type = object : TypeToken<Place>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun toJson(route: Place): String {
        return gson.toJson(route)
    }
}