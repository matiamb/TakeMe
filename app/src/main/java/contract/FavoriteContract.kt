package contract

import com.gfreeman.takeme.home.model.congrats.ResultDBOperation
import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute
import com.gfreeman.takeme.home.model.favs.FavoriteModel
import com.gfreeman.takeme.home.model.map.Place

interface FavoritesContract {
    interface FavoritesView<T : BaseContract.IBaseView> : FragmentBaseContract.IFragmentBaseView<T> {
        fun setFavoritesItems(favorites: List<FavoriteRoute>)
    }

    interface IFavoritesPresenter<T : FragmentBaseContract.IFragmentBaseView<*>> :
        FragmentBaseContract.IFragmentBasePresenter<T> {
        fun getFavoriteRoutes()
    }

    interface FavoritesModel {
        suspend fun getFavorites(): List<FavoriteRoute>
        suspend fun deleteFavoriteRoute(
            id: Int
        ): FavoriteModel.ResultDBOperation

        suspend fun findFavById(id: Int): FavoriteRoute
    }
}