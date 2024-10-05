package com.gfreeman.takeme.home.view.fav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gfreeman.takeme.R
import com.gfreeman.takeme.adapters.FavoritesAdapter
import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute
import com.gfreeman.takeme.home.model.favs.FavoriteModel
import com.gfreeman.takeme.home.presenter.favs.FavoritePresenter
import com.google.android.material.transition.platform.MaterialSharedAxis
import contract.BaseContract
import contract.FavoritesContract

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavFragment : Fragment(), FavoritesContract.FavoritesView<BaseContract.IBaseView> {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var favoritesPresenter: FavoritesContract.IFavoritesPresenter<FavoritesContract.FavoritesView<BaseContract.IBaseView>>
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var favoritesRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false)
        val favoritesView = inflater.inflate(R.layout.fragment_fav, container, false)
        favoritesRecyclerView = favoritesView.findViewById(R.id.fav_list_recycler_view)
        return favoritesView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPresenter()
    }

    override fun setFavoritesItems(favorites: List<FavoriteRoute>) {
        favoritesAdapter = FavoritesAdapter()
        //Tengo que setear la data antes de decirle al recycler view cual es el adapter,
        //si no recibe que no tiene elementos y no crea la lista IMPORTANTISIMO
        favoritesAdapter.setData(favorites)
        favoritesRecyclerView.layoutManager = LinearLayoutManager(context)
        favoritesRecyclerView.adapter = favoritesAdapter
    }

    override fun getParentView(): BaseContract.IBaseView? {
        return activity as BaseContract.IBaseView?
    }

    private fun initPresenter(){
        context?.let {
            val favoritesModel = FavoriteModel(it)
            favoritesPresenter = FavoritePresenter(favoritesModel)
            favoritesPresenter.attachView(this)
        }
    }
}