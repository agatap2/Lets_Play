package com.akobusinska.letsplay.utils

import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.data.entities.GameType
import com.akobusinska.letsplay.data.entities.MyGame
import com.akobusinska.letsplay.data.repository.GameRepository
import com.akobusinska.letsplay.ui.addGame.DialogGamesListAdapter
import com.akobusinska.letsplay.ui.gameSelection.DetailedGamesListAdapter
import com.akobusinska.letsplay.ui.gameSelection.SimpleGamesListAdapter
import com.akobusinska.letsplay.ui.gamesList.BasicGamesListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*


@BindingAdapter("gameType")
fun TextView.setGameType(gameType: GameType?) {
    this.text =
        if (gameType == GameType.GAME || gameType == null) context.applicationContext.getString(R.string.game)
            .uppercase(Locale.ROOT)
        else context.applicationContext.getString(R.string.expansion).uppercase(Locale.ROOT)
}

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

@BindingAdapter("minNumberOfPlayers", "maxNumberOfPlayers")
fun TextView.setNumberOfPlayers(minPlayers: Int, maxPlayers: Int) {
    if (maxPlayers < 20)
        this.text = HtmlCompat.fromHtml(
            context.getString(
                R.string.number_of_players_value,
                minPlayers,
                maxPlayers
            ), HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    else
        this.text = HtmlCompat.fromHtml(
            context.applicationContext.getString(
                R.string.number_of_players_value,
                minPlayers,
                maxPlayers
            ), HtmlCompat.FROM_HTML_MODE_LEGACY
        )
}

@BindingAdapter("minPlaytime", "maxPlaytime")
fun TextView.setPlaytime(minPlaytime: Int, maxPlaytime: Int) {
    this.text = if (maxPlaytime <= 120)
        HtmlCompat.fromHtml(
            context.applicationContext.getString(
                R.string.playtime_value,
                minPlaytime,
                maxPlaytime
            ), HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    else
        HtmlCompat.fromHtml(
            context.applicationContext.getString(
                R.string.long_playtime,
                minPlaytime
            ), HtmlCompat.FROM_HTML_MODE_LEGACY
        )
}

@BindingAdapter("minAge")
fun TextView.setMinAge(minAge: Int) {
    this.text = HtmlCompat.fromHtml(
        context.applicationContext.getString(
            R.string.min_age_value,
            minAge
        ), HtmlCompat.FROM_HTML_MODE_LEGACY
    )
}

@BindingAdapter("listData")
fun RecyclerView.bindRecyclerView(data: List<MyGame>?) {
    val adapter = this.adapter as BasicGamesListAdapter
    adapter.submitList(data)
}

@BindingAdapter("detailedListData")
fun RecyclerView.bindDetailedRecyclerView(data: List<MyGame>?) {
    val adapter = this.adapter as DetailedGamesListAdapter
    adapter.submitList(data?.sortedBy { it.name })
}

@BindingAdapter("simpleListData")
fun RecyclerView.bindSimpleRecyclerView(data: List<MyGame>?) {
    val adapter = this.adapter as SimpleGamesListAdapter
    adapter.submitList(data?.sortedBy { it.name })
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