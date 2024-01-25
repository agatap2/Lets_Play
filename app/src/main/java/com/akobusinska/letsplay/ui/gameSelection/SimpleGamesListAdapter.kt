package com.akobusinska.letsplay.ui.gameSelection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.databinding.SimpleListItemBinding

class SimpleGamesListAdapter(private val clickListener: GamesListListener) :
    ListAdapter<MyGame, SimpleGamesListAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<MyGame>() {
        override fun areItemsTheSame(oldItem: MyGame, newItem: MyGame): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MyGame, newItem: MyGame): Boolean {
            return oldItem.gameId == newItem.gameId
        }
    }

    class ViewHolder constructor(private val binding: SimpleListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: GamesListListener, item: MyGame) {
            binding.game = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = SimpleListItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }

    class GamesListListener(val clickListener: (game: MyGame) -> Unit) {
        fun onClick(game: MyGame) = clickListener(game)
    }
}