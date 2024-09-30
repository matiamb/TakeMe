package com.gfreeman.takeme.home.model.congrats

import android.content.Context
import androidx.room.Room
import com.gfreeman.takeme.home.model.db.DB_NAME
import com.gfreeman.takeme.home.model.db.FavoritesDao
import com.gfreeman.takeme.home.model.db.FavoritesDatabase
import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute
import com.gfreeman.takeme.home.model.map.Place
import contract.ArrivedToDestinationContract

class ArrivedToDestinationModel(context: Context): ArrivedToDestinationContract.ArrivedToDestinationModel {
    override suspend fun saveFavoriteRoute(startPlace: Place, destinationPlace: Place, date: String) : ResultDBOperation{
        return addFavorite(
            FavoriteRoute(
                startPlace = startPlace,
                destinationPlace = destinationPlace,
                date = date
            )
        )
    }

    private lateinit var favoritesDao: FavoritesDao

    init {
        try {
            val db = Room.databaseBuilder(context, FavoritesDatabase::class.java, DB_NAME).build()
            favoritesDao = db.favoritesDao()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getFavorites(): List<FavoriteRoute> = favoritesDao.getFavorites()

    fun addFavorite(favorite: FavoriteRoute): ResultDBOperation = try {
        favoritesDao.addFavorite(favorite)
        ResultOk
    } catch (e: Exception) {
        e.printStackTrace()
        ResultError
    }

}

sealed class ResultDBOperation
data object ResultOk : ResultDBOperation()
data object ResultError : ResultDBOperation()