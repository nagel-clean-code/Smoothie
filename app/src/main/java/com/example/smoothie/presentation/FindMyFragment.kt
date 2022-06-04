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
class FindMyFragment : BaseFragment() {


    private lateinit var binding: FragmentFindRecipeBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var loadStateHolder: DefaultLoadStateAdapter.Holder

    override val viewModel: SharedFindRecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFindRecipeBinding.inflate(layoutInflater)
        setupRecipeList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        observeRecipe()
        observeLoadState()
        setupSwipeToRefresh()

        handleScrollingToTopWhenSearching()
        handleListVisibility()
        observeErrorMessages()
        observeInvalidationEvents()
        return binding.root
    }

    private fun setupRecipeList() {
        recipeAdapter = RecipeAdapter(viewModel)
        val tryAgainAction: TryAgainAction = { recipeAdapter.retry() }
        val footerAdapter = DefaultLoadStateAdapter(tryAgainAction)
        val adapterWithLoadState =
            recipeAdapter.withLoadStateFooter(footerAdapter) //Объединяем адаптеры

        (binding.recyclerView.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations =
            false

        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = adapterWithLoadState

        loadStateHolder = DefaultLoadStateAdapter.Holder(
            binding.loadStateView,
            tryAgainAction,
            binding.swipeRefreshLayout
        )


    }

    private fun handleScrollingToTopWhenSearching() = viewLifecycleOwner.lifecycleScope.launch {
        // list should be scrolled to the 1st item (index = 0) if data has been reloaded:
        // (prev state = Loading, current state = NotLoading)
        getRefreshLoadStateFlow()
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading) {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
    }

    private fun handleListVisibility() = viewLifecycleOwner.lifecycleScope.launch {
        // list should be hidden if an error is displayed OR if items are being loaded after the error:
        // (current state = Error) OR (prev state = Error)
        //   OR
        // (before prev state = Error, prev state = NotLoading, current state = Loading)
        getRefreshLoadStateFlow()
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.recyclerView.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun getRefreshLoadStateFlow(): Flow<LoadState> {
        return recipeAdapter.loadStateFlow
            .map { it.refresh }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeRecipe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.recipesFlow.collectLatest {
                recipeAdapter.submitData(it)
            }
        }
    }

    private fun observeLoadState() {
        viewLifecycleOwner.lifecycleScope.launch {
            recipeAdapter.loadStateFlow.debounce(200).collectLatest { state ->
                loadStateHolder.bind(state.refresh)
            }
        }
    }

    private fun observeErrorMessages() {
        viewModel.errorEvents.observeEvent(this) { messageRes ->
            Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeInvalidationEvents() {
        viewModel.invalidateEvents.observeEvent(this) {
            recipeAdapter.refresh()
        }
    }
}