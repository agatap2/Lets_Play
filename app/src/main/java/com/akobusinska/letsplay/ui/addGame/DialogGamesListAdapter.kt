package com.akobusinska.letsplay.ui.addGame


import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.databinding.SelectGameListBinding

class DialogGamesListAdapter(
    private val clickListener: GamesListListener,
    private val context: Context
) :
    ListAdapter<MyGame, DialogGamesListAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<MyGame>() {
        override fun areItemsTheSame(oldItem: MyGame, newItem: MyGame): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MyGame, newItem: MyGame): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private var lastCheckedPosition = -1

    inner class ViewHolder constructor(private val binding: SelectGameListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var name: TextView = binding.name
        private var radioSelect: RadioButton = binding.listItem

        init {
            binding.list.setOnClickListener {
                lastCheckedPosition = adapterPosition
            }
            radioSelect.setOnClickListener {
                lastCheckedPosition = adapterPosition
            }
        }

        fun bind(clickListener: GamesListListener, item: MyGame, position: Int) {
            binding.game = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

            val radioSelect = binding.listItem

            radioSelect.isChecked = position == lastCheckedPosition
            if (lastCheckedPosition == position) {
                name.setTextColor(getColor(context, R.color.primaryColor))
                radioSelect.isChecked = true
            } else {
                name.setTextColor(getColor(context, R.color.primaryTextColor))
                radioSelect.isChecked = false
            }
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