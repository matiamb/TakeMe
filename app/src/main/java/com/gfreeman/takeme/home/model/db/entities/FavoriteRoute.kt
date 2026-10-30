package com.gfreeman.takeme.home.model.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gfreeman.takeme.home.model.map.Place

@Entity(tableName = TABLE_FAVORITE)

data class FavoriteRoute(
    @ColumnInfo(name = COLUMN_START_PLACE)
    val startPlace: Place,
    @ColumnInfo(name = COLUMN_DESTINATION_PLACE)
    val destinationPlace: Place,
    @ColumnInfo(name = COLUMN_DATE)
    val date: String = ""
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    var id: Int = 0

}

const val TABLE_FAVORITE = "favorite"
const val COLUMN_ID = "id"
const val COLUMN_START_PLACE = "start_place"
const val COLUMN_DESTINATION_PLACE = "destination_place"
const val COLUMN_DATE = "date"
