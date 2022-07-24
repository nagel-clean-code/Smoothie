package com.example.smoothie.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.smoothie.Constants.Companion.RECIPE_ENTiTY_KEY
import com.example.smoothie.R
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.databinding.FragmentDisplayRecipeBinding
import com.example.smoothie.presentation.adapters.RecipeAdapter
import com.example.smoothie.presentation.images.GlideApp


class DisplayRecipeFragment : Fragment() {

    private lateinit var binding: FragmentDisplayRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDisplayRecipeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val recipe = arguments?.getParcelable<RecipeEntity>(RECIPE_ENTiTY_KEY)
        binding.headingRecipe.text = recipe?.name
        binding.recipe.text = recipe?.recipe

        recipe?.imageUrl?.let {
            if (it.isNotBlank()) {
                val ref = RecipeAdapter.ref.child(it)
                GlideApp.with(binding.banner.context)
                    .load(ref)
                    .centerCrop()
                    .placeholder(R.drawable.smoothie)
                    .error(R.drawable.ic_baseline_broken_image_48)
                    .into(binding.banner)
            }
        }
        return binding.root
    }

    companion object {
        fun getNewInstance(recipeEntity: RecipeEntity) = DisplayRecipeFragment().apply {
            arguments = Bundle().apply {
                putParcelable(RECIPE_ENTiTY_KEY, recipeEntity)
            }
        }
    }

}