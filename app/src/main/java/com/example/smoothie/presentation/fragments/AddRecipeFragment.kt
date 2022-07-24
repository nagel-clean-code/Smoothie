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
import com.example.smoothie.Category
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.databinding.CategoryItemBinding
import com.example.smoothie.databinding.FragmentAddRecipeBinding
import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.presentation.images.ImagePicker
import com.example.smoothie.presentation.viewmodels.AddRecipeViewModel
import com.example.smoothie.utils.convertDrawableToByteArray
import com.example.smoothie.utils.decodeFromBase64IntoDrawable
import com.example.smoothie.utils.encodeToBase64
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class AddRecipeFragment : BaseFragment() {

    private lateinit var binding: FragmentAddRecipeBinding

    override val viewModel: AddRecipeViewModel by viewModels()
    private val imagePicker = ImagePicker()
    private val listCategory: MutableList<Int> = mutableListOf()

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
        binding.addButton.setOnClickListener {
            saveRecipeToDatabase()
            clearTable()
            displayCategories()
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
        displayCategories()
        return binding.root
    }

    override fun onStop() {
        viewModel.resultImageLiveDataMutable.removeObservers(viewLifecycleOwner)
        viewModel.saveRecipeInSharedPrefs(recipeBuild())
        viewModel.saveImageInSharPref(binding.imagePreview.drawable, ::encodeToBase64)
        super.onStop()
    }

    private fun displayCategories() {
        Category.values().forEach {
            addSelectedCategory(it)
        }
    }

    private fun clearTable() {
        binding.table.removeAllViews()
        listCategory.clear()
        countSelectedCategories = 0
    }

    private var countSelectedCategories: Int = 0
    private lateinit var currentRow: TableRow
    private fun addSelectedCategory(item: Category) {
        countSelectedCategories++
        if (countSelectedCategories % COUNT_ROWS_CATEGORIES == 1) {
            currentRow = TableRow(context)
            val params = TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            currentRow.layoutParams = params
        }

        val bindingCurrentItem = CategoryItemBinding.inflate(layoutInflater)
        bindingCurrentItem.textView.text = item.named
        bindingCurrentItem.textView.tag = item
        bindingCurrentItem.textView.setOnClickListener {
            val tag = (it.tag as Category)
            if (!listCategory.contains(tag.id)) {
                listCategory.add(tag.id)
                it.setBackgroundResource(com.example.smoothie.R.drawable.category_pressed)
            } else {
                listCategory.remove(tag.id)
                it.setBackgroundResource(com.example.smoothie.R.drawable.category)
            }
        }

        currentRow.addView(bindingCurrentItem.root, 0)
        if (countSelectedCategories % COUNT_ROWS_CATEGORIES == 1) {
            binding.table.addView(currentRow, countSelectedCategories / 3)
        }
    }

    private fun recipeDisplay(recipe: IRecipeModel) {
        binding.editTextNameRecipe.text.clear()
        binding.editTextNameRecipe.text.append(recipe.name)
        binding.enteringRecipe.text?.clear()
        binding.enteringRecipe.text?.append(recipe.recipe)  //FIXME сделать ещё подгрузку маркировок
    }

    private fun recipeBuild(): IRecipeModel = RecipeEntity(
        uniqueId = "${viewModel.getUserName()}_${UUID.randomUUID()}",
        name = binding.editTextNameRecipe.text.toString(),
        recipe = binding.enteringRecipe.text.toString(),
        listCategory = listCategory
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

    companion object {
        const val COUNT_ROWS_CATEGORIES = 3
    }
}