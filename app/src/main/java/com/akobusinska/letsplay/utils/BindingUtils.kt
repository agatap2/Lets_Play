package com.akobusinska.letsplay.utils

import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.repository.GameRepository
import com.akobusinska.letsplay.ui.addGame.DialogGamesListAdapter
import com.akobusinska.letsplay.ui.gamesList.BasicGamesListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("imageUrl")
fun ImageView.setGameCover(imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(this.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .into(this)
    }
}

@BindingAdapter("listData")
fun RecyclerView.bindRecyclerView(data: List<MyGame>?) {
    val adapter = this.adapter as BasicGamesListAdapter
    adapter.submitList(data)
}

@BindingAdapter("dialogListData")
fun RecyclerView.bindDialogRecyclerView(data: List<MyGame>?) {
    val adapter = this.adapter as DialogGamesListAdapter
    adapter.submitList(data)
}

@BindingAdapter("searchingStatus")
fun ImageView.bindStatus(status: GameRepository.RequestStatus) {
    when (status) {
        GameRepository.RequestStatus.LOADING -> {
            this.visibility = VISIBLE
            this.setImageResource(R.drawable.loading_animation)
        }
        GameRepository.RequestStatus.ERROR -> {
            this.visibility = VISIBLE
            this.setImageResource(R.drawable.ic_connection_error)
        }
        else -> {
            this.visibility = GONE
        }
    }
}