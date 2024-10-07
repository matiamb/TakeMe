package com.gfreeman.takeme.home.model.favs

import android.content.Context
import androidx.room.Room
import com.gfreeman.takeme.home.model.db.DB_NAME
import com.gfreeman.takeme.home.model.db.FavoritesDao
import com.gfreeman.takeme.home.model.db.FavoritesDatabase
import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute
import contract.FavoritesContract

class FavoriteModel(context: Context) : FavoritesContract.FavoritesModel {
    private lateinit var favoritesDao: FavoritesDao
    init {
        try {
            val db = Room.databaseBuilder(context, FavoritesDatabase::class.java, DB_NAME).build()
            favoritesDao = db.favoritesDao()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getFavorites(): List<FavoriteRoute> = favoritesDao.getFavorites()

    override suspend fun deleteFavoriteRoute(id: Int): ResultDBOperation {
        return deleteFavorite(findFavById(id))
    }

    override suspend fun findFavById(id: Int): FavoriteRoute{
        return favoritesDao.findFavById(id)
    }

    private fun deleteFavorite(favorite: FavoriteRoute): ResultDBOperation = try {
        favoritesDao.deleteFavorite(favorite)
        ResultOk
    } catch (e: Exception){
        e.printStackTrace()
        ResultError
    }

sealed class ResultDBOperation
data object ResultOk : ResultDBOperation()
data object ResultError : ResultDBOperation()

}