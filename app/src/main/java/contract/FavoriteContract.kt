package contract

import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute

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
    }
}