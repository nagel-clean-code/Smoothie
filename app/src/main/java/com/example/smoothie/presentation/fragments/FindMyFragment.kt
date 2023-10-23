package com.example.smoothie.presentation.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smoothie.databinding.FragmentFindMyBinding
import com.example.smoothie.presentation.adapters.DefaultLoadStateAdapter
import com.example.smoothie.presentation.adapters.RecipeAdapter
import com.example.smoothie.presentation.adapters.TryAgainAction
import com.example.smoothie.presentation.images.GlideApp
import com.example.smoothie.presentation.viewmodels.SharedFindRecipeViewModel
import com.example.smoothie.presentation.views.BordCategoriesView
import com.example.smoothie.utils.observeEvent
import com.example.smoothie.utils.simpleScan
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FindMyFragment(private val indexPager: Int) : BaseFragment() {

    private lateinit var binding: FragmentFindMyBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var loadStateHolder: DefaultLoadStateAdapter.Holder
    private lateinit var table: BordCategoriesView

    override val viewModel: SharedFindRecipeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFindMyBinding.inflate(layoutInflater)
        setupRecipeList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        observeRecipe()
        observeLoadState()
        setupSwipeToRefresh()
        setupSearchInput()
        handleScrollingToTopWhenSearching()
        handleListVisibility()
        observeErrorMessages()
        observeInvalidationEvents()
        initFilterBorder()
        table = binding.table
        table.setupApi(viewModel)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
        table.clearTable()
        table.displayCategories()
        CoroutineScope(Dispatchers.Main).launch {   //Костыльно решил проблему с расширением таблицы на весь экран
            binding.table.visibility = View.GONE
        }
    }

    private fun initFilterBorder() {
        binding.openFilter.setOnClickListener {
            if (binding.table.visibility == View.VISIBLE) {
                animationPopExit()
            } else {
                animationPopEnter()
            }
        }
    }

    private fun animationPopEnter() {
        val animation = AnimationUtils.loadAnimation(context, com.example.smoothie.R.anim.pop_enter)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                binding.table.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation?) {}

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        binding.table.startAnimation(animation)
    }

    private fun animationPopExit() {
        val animation = AnimationUtils.loadAnimation(context, com.example.smoothie.R.anim.pop_exit)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                binding.table.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        binding.table.startAnimation(animation)
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveSelectedCategoriesInSharPrefs(table.listCategorySelected)

    }

    private fun setupSearchInput() {
        binding.searchEditText.addTextChangedListener {
            viewModel.setSearchBy(it.toString())
        }
    }

    private fun setupRecipeList() {
        recipeAdapter = RecipeAdapter(viewModel)
        val tryAgainAction: TryAgainAction = { recipeAdapter.retry() }
        val footerAdapter = DefaultLoadStateAdapter(tryAgainAction)
        val adapterWithLoadState = recipeAdapter.withLoadStateFooter(footerAdapter) //Объединяем адаптеры

        (binding.myRecipesRecyclerView.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false

        val linearLayoutManager = LinearLayoutManager(context)
        binding.myRecipesRecyclerView.layoutManager = linearLayoutManager
        binding.myRecipesRecyclerView.adapter = adapterWithLoadState

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
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading
                    && viewModel.scrollEvents.value?.get() != null
                ) {
                    binding.myRecipesRecyclerView.scrollToPosition(0)
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
                Log.d("getRefreshLoadStateFlow", "beforePrevious $beforePrevious, previous $previous, current $current")

                val invisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
                Log.d("getRefreshLoadStateFlow: isInvisible = ", "$invisible")

                binding.myRecipesRecyclerView.isInvisible = invisible
            }
    }

    private fun getRefreshLoadStateFlow(): Flow<LoadState> {
        return recipeAdapter.loadStateFlow.map { it.refresh }
    }

    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    //TODO нужно сделать подгрузку из sharprefs, и проверять на сервере есть ли новые рецепты и только после этого подгружаеть новые
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

            GlideApp.get(requireActivity().applicationContext).clearMemory()
            CoroutineScope(Dispatchers.IO).launch {
                GlideApp.get(requireActivity().applicationContext).clearDiskCache()
            }
        }
    }
}