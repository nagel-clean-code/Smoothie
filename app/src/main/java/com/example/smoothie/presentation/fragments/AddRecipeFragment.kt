package com.example.smoothie.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.viewModels
import coil.load
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.databinding.FragmentAddRecipeBinding
import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.presentation.images.ImagePicker
import com.example.smoothie.presentation.viewmodels.AddRecipeViewModel
import com.example.smoothie.presentation.views.BordCategoriesView
import com.example.smoothie.utils.convertDrawableToByteArray
import com.example.smoothie.utils.decodeFromBase64IntoDrawable
import com.example.smoothie.utils.encodeToBase64
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class AddRecipeFragment : BaseFragment() {

    private lateinit var binding: FragmentAddRecipeBinding
    private lateinit var table: BordCategoriesView

    override val viewModel: AddRecipeViewModel by viewModels()
    private val imagePicker = ImagePicker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddRecipeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        focusInputAndAppearanceKeyboard()
        binding.imageButton.setOnClickListener {
            activity?.activityResultRegistry?.let { it1 ->
                imagePicker.setupLoadFromGallery(it1) {
                    binding.imagePreview.load(it)
                }
            }
        }
        binding.addRecipeButton.setOnClickListener {
            saveRecipeToDatabase()
        }
        viewModel.recipeDisplayLiveDataMutable.observe(viewLifecycleOwner) { recipe ->
            recipe?.let {
                recipeDisplay(it)
            }
        }
        viewModel.resultImageLiveDataMutable.observe(viewLifecycleOwner) { image ->
            binding.imagePreview.load(image)
        }
        viewModel.load()
        viewModel.loadImage(::decodeFromBase64IntoDrawable)
        table = binding.table
        table.setupApi(viewModel)
        table.displayCategories()
        return binding.root
    }

    override fun onStop() {
        viewModel.resultImageLiveDataMutable.removeObservers(viewLifecycleOwner)
        viewModel.saveRecipeInSharedPrefs(recipeBuild())
        viewModel.saveImageInSharPref(binding.imagePreview.drawable, ::encodeToBase64)
        viewModel.saveSelectedCategoriesInSharPrefs(table.listCategorySelected)
        super.onStop()
    }

    private fun clearTable() {
        binding.table.clearTable()
    }


    private fun recipeDisplay(recipe: IRecipeModel) {
        binding.editTextNameRecipe.text.clear()
        binding.editTextNameRecipe.text.append(recipe.name)
        binding.enteringRecipe.text?.clear()
        binding.enteringRecipe.text?.append(recipe.recipe)
    }

    private fun recipeBuild(): IRecipeModel = RecipeEntity(
        uniqueId = "${viewModel.getUserName()}_${UUID.randomUUID()}",
        name = binding.editTextNameRecipe.text.toString(),
        recipe = binding.enteringRecipe.text.toString(),
        listCategory = table.listCategorySelected
    )


    private fun saveRecipeToDatabase() {
        if (binding.editTextNameRecipe.text.toString().isEmpty()) {
            Toast.makeText(context, "Название рецепта не задано!", Toast.LENGTH_SHORT).show()
        } else if (binding.enteringRecipe.text.toString().isEmpty()) {
            Toast.makeText(context, "Задайте рецепт", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.setRecipeToDataBase(
                recipeBuild(),
                convertDrawableToByteArray(binding.imagePreview.drawable)
            )
            Toast.makeText(context, "Рецепт успешно добавлен", Toast.LENGTH_SHORT).show()
            binding.editTextNameRecipe.text.clear()
            binding.enteringRecipe.text?.clear()
            binding.imagePreview.setImageResource(0)
            clearTable()
            table.displayCategories()
        }
    }

    /** Фокусировка на ввод названия рецепта и автоматическое появление клавиатуры для ввода */
    private fun focusInputAndAppearanceKeyboard() {
        if (binding.editTextNameRecipe.requestFocus()) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }
    }

}