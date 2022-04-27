package com.example.smoothie.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smoothie.Constants
import com.example.smoothie.databinding.HomeBinding
import com.example.smoothie.screens.adapters.HomePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment() : Fragment() {

    private lateinit var binding: HomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingPagerAdapter()
    }

    private fun settingPagerAdapter(){
        binding.pagerPlaceholder.adapter = activity?.let { HomePagerAdapter(it) }
        binding.tabLayout.tabIconTint = null        //TODo нужно узнать подробнее что это делает
        TabLayoutMediator(binding.tabLayout, binding.pagerPlaceholder){
                tab, pos ->
            when(pos){
                0 -> tab.text = Constants.MY_RECIPES
                1 -> tab.text = Constants.STANDARDS
            }
        }.attach()
    }
}