package com.akobusinska.letsplay.ui.addGame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.akobusinska.letsplay.R
import com.akobusinska.letsplay.databinding.FragmentAddNewGameBinding

class AddNewGameFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentAddNewGameBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_new_game, container, false)

        return binding.root
    }
}