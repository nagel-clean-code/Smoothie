package com.example.smoothie.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.smoothie.Constants
import com.example.smoothie.databinding.HomeBinding
import com.example.smoothie.screens.adapters.HomePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment(private val fragmentActivity: FragmentActivity?) : Fragment() {
    constructor():this(null)

    private lateinit var binding: HomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeBinding.inflate(layoutInflater)
        if(savedInstanceState == null){
            binding.pagerPlaceholder.adapter = HomePagerAdapter(fragmentActivity!!)
            binding.tabLayout.tabIconTint = null        //TODo нужно узнать подробнее что это делает
            TabLayoutMediator(binding.tabLayout, binding.pagerPlaceholder){
                    tab, pos ->
                when(pos){
                    0 -> tab.text = Constants.MY_RECIPES
                    1 -> tab.text = Constants.STANDARDS
                }
            }.attach()
        }
        return binding.root
    }
}