package com.akobusinska.letsplay.ui.gamesList


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akobusinska.letsplay.data.entities.CollectionOwner
import com.akobusinska.letsplay.databinding.SelectUserListBinding

class DialogUsersListAdapter(
    private val clickListener: UsersListListener, startPosition: Int
) :
    ListAdapter<CollectionOwner, DialogUsersListAdapter.ViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<CollectionOwner>() {
        override fun areItemsTheSame(oldItem: CollectionOwner, newItem: CollectionOwner): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: CollectionOwner,
            newItem: CollectionOwner
        ): Boolean {
            return oldItem.collectionOwnerId == newItem.collectionOwnerId
        }
    }

    private var lastCheckedPosition = startPosition

    inner class ViewHolder constructor(private val binding: SelectUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var radioSelect: RadioButton = binding.listItem
        private var user: CollectionOwner? = null

        init {
            radioSelect.setOnClickListener {
                updateRadioButton()
            }
            binding.name.setOnClickListener {
                updateRadioButton()
            }
        }

        private fun updateRadioButton() {
            lastCheckedPosition = adapterPosition
            notifyDataSetChanged()
            if (user != null) clickListener.onClick(user!!)
        }

        fun bind(clickListener: UsersListListener, item: CollectionOwner, position: Int) {
            binding.user = item
            binding.clickListener = clickListener
            binding.executePendingBindings()

            user = item
            radioSelect.isChecked = position == lastCheckedPosition
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SelectUserListBinding.inflate(layoutInflater)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position), position)
    }

    class UsersListListener(val clickListener: (user: CollectionOwner) -> Unit) {
        fun onClick(user: CollectionOwner) = clickListener(user)
    }
}