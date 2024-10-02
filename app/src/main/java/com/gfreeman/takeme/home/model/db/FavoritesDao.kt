package com.gfreeman.takeme.home.model.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute
import com.gfreeman.takeme.home.model.db.entities.TABLE_FAVORITE

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM $TABLE_FAVORITE")
    fun getFavorites(): List<FavoriteRoute>

    @Insert
    fun addFavorite(favorite: FavoriteRoute)

    @Delete
    fun deleteFavorite(favorite: FavoriteRoute)
}