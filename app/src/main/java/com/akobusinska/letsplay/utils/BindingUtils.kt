package com.akobusinska.letsplay.utils

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.ui.gamesList.GamesListAdapter
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
    val adapter = this.adapter as GamesListAdapter
    adapter.submitList(data)
}