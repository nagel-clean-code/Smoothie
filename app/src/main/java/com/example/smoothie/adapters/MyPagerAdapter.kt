package com.example.smoothie.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.smoothie.Constants
import com.example.smoothie.screens.MyFragment
import com.example.smoothie.screens.StandardFragment

class MyPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount() = Constants.COUNT_FRAGMENT_VIEW

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> MyFragment()
            1 -> StandardFragment()
            else -> throw IllegalArgumentException("Нет такой вкладки")
        }
    }


}