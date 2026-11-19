package com.gfreeman.takeme.home.model.favs

import com.gfreeman.takeme.home.model.db.FavoritesDao
import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Production implementation of FavoriteRepository using Room Database.
 *
 * HILT CONSTRUCTOR INJECTION & MAIN-SAFETY:
 * 1. Injecting FavoritesDao cleanly via constructor (no init DB building!).
 * 2. All suspend functions use withContext(ioDispatcher) for main-safety.
 */
class FavoriteRepositoryImpl @Inject constructor(
    private val favoritesDao: FavoritesDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : FavoriteRepository {

    override suspend fun getFavorites(): List<FavoriteRoute> = withContext(ioDispatcher) {
        favoritesDao.getFavorites()
    }

    override suspend fun deleteFavoriteRoute(id: Int): ResultDBOperation = withContext(ioDispatcher) {
        val favorite = favoritesDao.findFavById(id)
        if (favorite != null) {
            deleteFavorite(favorite)
        } else {
            ResultError
        }
    }

    override suspend fun findFavById(id: Int): FavoriteRoute = withContext(ioDispatcher) {
        favoritesDao.findFavById(id)
    }

    private fun deleteFavorite(favorite: FavoriteRoute): ResultDBOperation = try {
        favoritesDao.deleteFavorite(favorite)
        ResultOk
    } catch (e: Exception) {
        e.printStackTrace()
        ResultError
    }
}
