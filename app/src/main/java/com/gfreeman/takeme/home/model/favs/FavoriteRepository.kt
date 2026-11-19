package com.gfreeman.takeme.home.model.favs

import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute

/**
 * Clean Architecture Repository interface for Favorites.
 *
 * WHY USE AN INTERFACE FOR REPOSITORIES:
 * 1. Testability: Allows swapping Room DB for a FakeRepository in ViewModel Unit Tests.
 * 2. Decoupling: ViewModel depends on an abstraction (interface), not a concrete DB implementation.
 */
interface FavoriteRepository {
    suspend fun getFavorites(): List<FavoriteRoute>
    suspend fun deleteFavoriteRoute(id: Int): ResultDBOperation
    suspend fun findFavById(id: Int): FavoriteRoute
}

sealed class ResultDBOperation
data object ResultOk : ResultDBOperation()
data object ResultError : ResultDBOperation()
