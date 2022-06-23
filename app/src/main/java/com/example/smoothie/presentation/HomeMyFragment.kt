package com.example.smoothie.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.smoothie.databinding.FragmentMyBinding
import com.example.smoothie.databinding.PartResultBinding
import com.example.smoothie.presentation.viewmodels.SharedHomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.smoothie.utils.decodeFromBase64IntoDrawable
import com.example.smoothie.utils.onTryAgain

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
        viewModel.resultImageLiveDataMutable.observe(viewLifecycleOwner) {
            if(it.second != indexPager) return@observe
            if (it == null) {
                binding.banner.setImageResource(0)
            } else {
                binding.banner.setImageDrawable(it.first)
            }
        }
        onTryAgain(binding.root) {  //Установка слушателя на кнопку "Повторить"
            viewModel.tryAgain(indexPager)
        }

        listenerResults()

//        viewModel.nextRecipe() //FIXME Переделать на загрузки из шаредпрефенса
        return binding.root
    }

    private fun listenerResults(){
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
                    binding.ConstraintLayoutPart.visibility = VISIBLE
                    binding.scrollView.visibility = GONE
                    resultBinding.errorContainer.visibility = VISIBLE
                    resultBinding.progressBar.visibility = GONE
                }
            )
        }
    }

    private fun listenerCurrentRecipes(){
        viewModel.resultRecipeLiveData.observe(viewLifecycleOwner) { recipeResult ->
            if(recipeResult.second != indexPager) return@observe
            val recipe = recipeResult.first
            viewModel.getImage(recipe.imageUrl, indexPager, ::decodeFromBase64IntoDrawable)
            binding.headingRecipe.text = recipe.name
            if (recipe.ingredients.isNotBlank()) {
                binding.textViewIngredients.visibility = VISIBLE
                binding.ingredients.visibility = VISIBLE
                binding.ingredients.text = recipe.ingredients
            } else {
                binding.textViewIngredients.visibility = GONE
                binding.ingredients.visibility = GONE
            }
            if (recipe.recipe.isNotBlank()) {
                binding.textViewRecipe.visibility = VISIBLE
                binding.recipe.visibility = VISIBLE
                binding.recipe.text = recipe.recipe
            } else {
                binding.textViewRecipe.visibility = GONE
                binding.recipe.visibility = GONE
            }
            if (recipe.description.isNotBlank()) {
                binding.textViewDescription.visibility = VISIBLE
                binding.description.visibility = VISIBLE
                binding.description.text = recipe.description
            } else {
                binding.textViewDescription.visibility = GONE
                binding.description.visibility = GONE
            }
        }

    }

}