package com.akobusinska.letsplay.ui.addGame


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.databinding.SelectGameListBinding

class DialogGamesListAdapter(
    private val clickListener: GamesListListener
) :
    ListAdapter<MyGame, DialogGamesListAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<MyGame>() {
        override fun areItemsTheSame(oldItem: MyGame, newItem: MyGame): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MyGame, newItem: MyGame): Boolean {
            return oldItem.game_id == newItem.game_id
        }
    }

    private var lastCheckedPosition = -1

    inner class ViewHolder constructor(private val binding: SelectGameListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var radioSelect: RadioButton = binding.listItem
        private var game: MyGame? = null

        init {
            radioSelect.setOnClickListener {
                updateRadioButton()
            }
            binding.name.setOnClickListener {
                updateRadioButton()
            }
            binding.cover.setOnClickListener {
                updateRadioButton()
            }
        }

        private fun updateRadioButton() {
            lastCheckedPosition = adapterPosition
            notifyDataSetChanged()
            if (game != null) clickListener.onClick(game!!)
        }

        fun bind(clickListener: GamesListListener, item: MyGame, position: Int) {
            binding.game = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

            game = item
            radioSelect.isChecked = position == lastCheckedPosition
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SelectGameListBinding.inflate(layoutInflater)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position), position)
    }

    class GamesListListener(val clickListener: (game: MyGame) -> Unit) {
        fun onClick(game: MyGame) = clickListener(game)
    }
}