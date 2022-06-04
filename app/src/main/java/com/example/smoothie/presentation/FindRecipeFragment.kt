package com.example.smoothie.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smoothie.Constants
import com.example.smoothie.databinding.FragmentFindMyBinding
import com.example.smoothie.databinding.FragmentFindRecipeBinding
import com.example.smoothie.presentation.adapters.DefaultLoadStateAdapter
import com.example.smoothie.presentation.adapters.HomePagerAdapter
import com.example.smoothie.presentation.adapters.RecipeAdapter
import com.example.smoothie.presentation.adapters.TryAgainAction
import com.example.smoothie.presentation.viewmodels.SharedFindRecipeViewModel
import com.example.smoothie.utils.observeEvent
import com.example.smoothie.utils.simpleScan
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FindRecipeFragment : BaseFragment() {
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