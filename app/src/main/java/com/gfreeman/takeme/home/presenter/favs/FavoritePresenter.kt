package com.gfreeman.takeme.home.presenter.favs

import contract.BaseContract
import contract.FavoritesContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritePresenter(private val favoritesModel: FavoritesContract.FavoritesModel) : FavoritesContract.IFavoritesPresenter<
        FavoritesContract.FavoritesView<BaseContract.IBaseView>> {
    private lateinit var favoritesView: FavoritesContract.FavoritesView<BaseContract.IBaseView>

    override fun getFavoriteRoutes() {
        CoroutineScope(Dispatchers.IO).launch {
            val favorites = favoritesModel.getFavorites()
            withContext(Dispatchers.Main) {
               favoritesView.setFavoritesItems(favorites)
            }
        }
    }

    override fun attachView(view: FavoritesContract.FavoritesView<BaseContract.IBaseView>) {
        favoritesView = view
        getFavoriteRoutes()
    }
}