package com.example.smoothie.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.smoothie.Constants
import com.example.smoothie.presentation.fragments.FindMyFragment


class FindPagerAdapter(childFragmentManager: FragmentManager, fa: FragmentActivity): FragmentStateAdapter(childFragmentManager, fa.lifecycle) {
    override fun getItemCount() = Constants.COUNT_FRAGMENT_VIEW

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> FindMyFragment(0)
            1 -> FindMyFragment(1)
            else -> throw IllegalArgumentException("Нет такой вкладки")
        }
    }


}