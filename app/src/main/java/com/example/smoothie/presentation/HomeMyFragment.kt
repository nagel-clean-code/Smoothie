package com.example.smoothie.presentation

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.smoothie.databinding.FragmentMyBinding
import com.example.smoothie.databinding.PartResultBinding
import com.example.smoothie.presentation.viewmodels.SharedHomeViewModel
import com.example.smoothie.utils.decodeFromBase64IntoDrawable
import com.example.smoothie.utils.onTryAgain
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeMyFragment(private val indexPager: Int) : BaseFragment() {

    private lateinit var binding: FragmentMyBinding
    override val viewModel: SharedHomeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMyBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listenerCurrentRecipes()
        onTryAgain(binding.root) { viewModel.tryAgain(indexPager) }
        listenerResults()

        viewModel.loadLastRecipe(indexPager)
        return binding.root
    }

    private fun listenerResults() {
        val resultBinding = PartResultBinding.bind(binding.root)
        viewModel.loadResultLiveData.observe(viewLifecycleOwner) { result ->
            renderResult(
                root = binding.root,
                result = result,
                onSuccessResult = {
                    binding.ConstraintLayoutPart.visibility = GONE
                    binding.scrollView.visibility = VISIBLE
                },
                onPending = {
                    binding.ConstraintLayoutPart.visibility = VISIBLE
                    binding.scrollView.visibility = GONE
                    resultBinding.errorContainer.visibility = GONE
                    resultBinding.progressBar.visibility = VISIBLE
                },
                onError = {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    binding.ConstraintLayoutPart.visibility = VISIBLE
                    binding.scrollView.visibility = GONE
                    resultBinding.errorContainer.visibility = VISIBLE
                    resultBinding.progressBar.visibility = GONE
                }
            )
        }

        viewModel.loadImageLiveData.observe(viewLifecycleOwner) { result ->
            renderResult(
                root = binding.root,
                result = result,
                onSuccessResult = {
                    binding.textErrorLoadImage.visibility = GONE
                    if (it.second == -1) {
                        binding.banner.setImageResource(0)
                        binding.banner.visibility = GONE
                    }
                    if (it.second != indexPager) {
                        binding.progressBarImage.visibility = GONE
                        return@renderResult
                    }
                    binding.banner.setImageDrawable(
                        decodeFromBase64IntoDrawable(
                            Base64.encodeToString(
                                it.first,
                                Base64.DEFAULT
                            )
                        )
                    )
                    binding.progressBarImage.visibility = GONE
                },
                onPending = {
                    binding.progressBarImage.visibility = VISIBLE
                    binding.banner.setImageResource(0)
                    binding.textErrorLoadImage.visibility = GONE
                },
                onError = {
                    binding.banner.setImageResource(0)
                    binding.textErrorLoadImage.visibility = VISIBLE
                    binding.progressBarImage.visibility = GONE
                }
            )
        }
    }

    private fun listenerCurrentRecipes() {
        viewModel.resultRecipeLiveData.observe(viewLifecycleOwner) { recipeResult ->
            if (recipeResult.second != indexPager) return@observe
            val recipe = recipeResult.first
            binding.banner.visibility = VISIBLE
            viewModel.getImage(recipe.imageUrl!!, indexPager)
            binding.headingRecipe.text = recipe.name

            binding.textViewRecipe.visibility = VISIBLE
            binding.recipe.visibility = VISIBLE
            binding.recipe.text = recipe.recipe
        }

    }

}