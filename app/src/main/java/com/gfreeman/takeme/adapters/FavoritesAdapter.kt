package com.gfreeman.takeme.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gfreeman.takeme.R
import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute
import com.gfreeman.takeme.home.model.favs.FavoriteModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesAdapter : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    private var favorites: MutableList<FavoriteRoute> = listOf<FavoriteRoute>().toMutableList()
    private lateinit var favoriteModel: FavoriteModel
    fun setData(favRoutes: List<FavoriteRoute>){
       favorites = favRoutes.toMutableList()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_save_fav_route, parent, false)
        favoriteModel = FavoriteModel(parent.context)
        return FavoritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val item = favorites[position]
        holder.startLocation.text = item.startPlace.displayName
        holder.finishLocation.text = item.destinationPlace.displayName.split(",")?.take(3)?.joinToString(",")?:""
        holder.savedDate.text = item.date
        holder.savedDate.visibility = View.VISIBLE
        holder.cardTitle.visibility = View.GONE
        holder.fabBtn.setOnClickListener {
            favorites.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
            CoroutineScope(Dispatchers.IO).launch {
                favoriteModel.deleteFavoriteRoute(item.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return favorites.size
    }

    class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startLocation: TextView = itemView.findViewById(R.id.txt_fav_card_start_location)
        val finishLocation: TextView = itemView.findViewById(R.id.txt_fav_card_finish_location)
        val savedDate: TextView = itemView.findViewById(R.id.txt_fav_card_date)
        val fabBtn: FloatingActionButton = itemView.findViewById(R.id.btn_fav_route)
        val cardTitle: TextView = itemView.findViewById(R.id.txt_fav_card_title)
    }
}