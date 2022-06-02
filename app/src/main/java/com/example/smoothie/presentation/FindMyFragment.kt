package com.example.smoothie.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.smoothie.Constants
import com.example.smoothie.databinding.FragmentFindMyBinding
import com.example.smoothie.presentation.adapters.HomePagerAdapter
import com.example.smoothie.presentation.viewmodels.SharedFindRecipeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindMyFragment : BaseFragment() {

    private lateinit var binding: FragmentFindMyBinding
    override val viewModel: SharedFindRecipeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFindMyBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingPagerAdapter()
    }

    private fun settingPagerAdapter() {
        binding.pagerPlaceholder.adapter = activity?.let { HomePagerAdapter(it) }
        binding.tabLayout.tabIconTint = null        //TODo нужно узнать подробнее что это делает
        TabLayoutMediator(binding.tabLayout, binding.pagerPlaceholder) { tab, pos ->
            when (pos) {
                0 -> tab.text = Constants.MY_RECIPES
                1 -> tab.text = Constants.STANDARDS
            }
        }.attach()
    }
}