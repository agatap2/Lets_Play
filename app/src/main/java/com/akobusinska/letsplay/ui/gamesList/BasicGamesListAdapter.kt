package com.akobusinska.letsplay.ui.gamesList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.databinding.BasicListItemBinding
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView.SectionedAdapter

class BasicGamesListAdapter(private val clickListener: GamesListListener) :
    ListAdapter<MyGame, BasicGamesListAdapter.ViewHolder>(DiffCallback), SectionedAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }

    override fun getSectionName(position: Int): String {
        return getItem(position).name.first().toString()
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MyGame>() {
        override fun areItemsTheSame(oldItem: MyGame, newItem: MyGame): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MyGame, newItem: MyGame): Boolean {
            return oldItem.id == newItem.id
        }
    }

    class ViewHolder constructor(private val binding: BasicListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: GamesListListener, item: MyGame) {
            binding.game = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BasicListItemBinding.inflate(layoutInflater)

                return ViewHolder(binding)
            }
        }
    }

    class GamesListListener(val clickListener: (game: MyGame) -> Unit) {
        fun onClick(game: MyGame) = clickListener(game)
    }
}