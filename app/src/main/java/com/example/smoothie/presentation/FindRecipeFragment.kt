package com.example.smoothie.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smoothie.databinding.FragmentFindRecipeBinding
import com.example.smoothie.presentation.adapters.RecipeAdapter
import com.example.smoothie.presentation.viewmodels.FindRecipeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FindRecipeFragment : BaseFragment() {

    private lateinit var binding: FragmentFindRecipeBinding
    private val recipeAdapter by lazy(LazyThreadSafetyMode.NONE){ RecipeAdapter() }
    override val viewModel: FindRecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFindRecipeBinding.inflate(layoutInflater)

        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = recipeAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recipes.collectLatest{
                recipeAdapter.submitData(it)
            }
        }
        return binding.root
    }

}