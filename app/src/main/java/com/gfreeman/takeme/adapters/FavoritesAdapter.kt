package com.gfreeman.takeme.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gfreeman.takeme.R
import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute

class FavoritesAdapter : RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    private var favorites: List<FavoriteRoute> = listOf()

    fun setData(favRoutes: List<FavoriteRoute>){
       favorites = favRoutes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_save_fav_route, parent, false)
        return FavoritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val item = favorites[position]
        holder.startLocation.text = item.startPlace.displayName
        holder.finishLocation.text = item.destinationPlace.displayName
        holder.savedDate.text = item.date
        holder.savedDate.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return favorites.size
    }

    class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startLocation: TextView = itemView.findViewById(R.id.txt_fav_card_start_location)
        val finishLocation: TextView = itemView.findViewById(R.id.txt_fav_card_finish_location)
        val savedDate: TextView = itemView.findViewById(R.id.txt_fav_card_date)
    }
}