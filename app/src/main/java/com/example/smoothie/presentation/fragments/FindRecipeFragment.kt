package com.example.smoothie.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import androidx.fragment.app.activityViewModels
import com.example.smoothie.Constants
import com.example.smoothie.R
import com.example.smoothie.databinding.FragmentFindRecipeBinding
import com.example.smoothie.presentation.adapters.FindPagerAdapter
import com.example.smoothie.presentation.viewmodels.SharedFindRecipeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindRecipeFragment : BaseFragment() {
    private lateinit var binding: FragmentFindRecipeBinding
    override val viewModel: SharedFindRecipeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFindRecipeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.pagerPlaceholder.isSaveEnabled = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingPagerAdapter()
        observeRecipeDisplayEvents()
    }

    override fun onPause() {
        super.onPause()
        viewModel.chooseElement.removeObservers(viewLifecycleOwner)
    }


    private fun initLists() {
        val data1 = listOf(
            mapOf(
                KEY_TITLE to "Завттрак"
            ),
            mapOf(
                KEY_TITLE to "Ужин"
            )
        )
        val adapter = SimpleAdapter(
            context,
            data1,
            android.R.layout.simple_list_item_1,
            arrayOf(KEY_TITLE),
            intArrayOf(android.R.id.text1)
        )
//        binding.listStandardCategories.adapter = adapter

    }

    companion object {
        const val KEY_TITLE = "KEY_TITLE"
    }

    private fun settingPagerAdapter() {
        binding.pagerPlaceholder.adapter = activity?.let { FindPagerAdapter(childFragmentManager, it) }
        binding.tabLayout.tabIconTint = null        //TODo нужно узнать подробнее что это делает
        TabLayoutMediator(binding.tabLayout, binding.pagerPlaceholder) { tab, pos ->
            when (pos) {
                0 -> tab.text = Constants.MY_RECIPES
                1 -> tab.text = Constants.STANDARDS
            }
        }.attach()
    }

    private fun observeRecipeDisplayEvents() {
        viewModel.chooseElement.observe(viewLifecycleOwner) {
            viewModel.chooseElement.value?.let { it1 ->
                parentFragmentManager
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.frame_layout_placeholder,
                        DisplayRecipeFragment.getNewInstance(it1)
                    )
                    .commit()
                viewModel.chooseElement.postValue(null)
            }
        }
    }
}