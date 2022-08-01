package com.akobusinska.letsplay.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.FragmentGamesListBinding

class GamesListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentGamesListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_games_list, container, false
        )

        return binding.root
    }
}