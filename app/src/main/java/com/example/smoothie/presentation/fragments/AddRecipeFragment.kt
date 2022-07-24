package com.example.smoothie.presentation.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.viewModels
import coil.load
import com.example.smoothie.Category
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.databinding.CategoryItemBinding
import com.example.smoothie.databinding.DialogAddCategoryBinding
import com.example.smoothie.databinding.FragmentAddRecipeBinding
import com.example.smoothie.domain.models.IRecipeModel
import com.example.smoothie.presentation.images.ImagePicker
import com.example.smoothie.presentation.viewmodels.AddRecipeViewModel
import com.example.smoothie.utils.convertDrawableToByteArray
import com.example.smoothie.utils.decodeFromBase64IntoDrawable
import com.example.smoothie.utils.encodeToBase64
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule


@AndroidEntryPoint
class AddRecipeFragment : BaseFragment() {

    private lateinit var binding: FragmentAddRecipeBinding

    override val viewModel: AddRecipeViewModel by viewModels()
    private val imagePicker = ImagePicker()
    private var timer: Timer? = null

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
        viewModel.updateSelectedCategoriesFromSharPrefs()
        viewModel.loadImage(::decodeFromBase64IntoDrawable)
        displayCategories()
        return binding.root
    }

    override fun onStop() {
        viewModel.resultImageLiveDataMutable.removeObservers(viewLifecycleOwner)
        viewModel.saveRecipeInSharedPrefs(recipeBuild())
        viewModel.saveImageInSharPref(binding.imagePreview.drawable, ::encodeToBase64)
        viewModel.saveSelectedCategoriesInSharPrefs()
        super.onStop()
    }

    private fun displayCategories() {
        Category.values().forEach {
            addCategoryTable(it.named)
        }
        viewModel.listCustomCategories.forEach {
            addCategoryTable(it)
        }
        addCategoryTable("+")
    }

    private fun clearTable() {
        binding.table.removeAllViews()
        viewModel.listCategorySelected.clear()
        countSelectedCategories = 0
    }

    private var countSelectedCategories: Int = 0
    private lateinit var currentRow: TableRow
    private fun addCategoryTable(item: String) {
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
        bindingCurrentItem.textView.text = item
        bindingCurrentItem.textView.tag = item
        if (viewModel.listCategorySelected.contains(item)) {
            bindingCurrentItem.textView.setBackgroundResource(com.example.smoothie.R.drawable.category_pressed)
        }

        initEventsCategory(bindingCurrentItem)

        currentRow.addView(bindingCurrentItem.root, (countSelectedCategories - 1) % COUNT_ROWS_CATEGORIES)
        if (countSelectedCategories % COUNT_ROWS_CATEGORIES == 1) {
            binding.table.addView(currentRow, countSelectedCategories / COUNT_ROWS_CATEGORIES)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEventsCategory(bindingCurrentItem: CategoryItemBinding){
        bindingCurrentItem.textView.setOnClickListener {
            val tag = (it.tag as String)
            if (tag == "+") {
                showDialogAddCategory()
            } else {
                if (!viewModel.listCategorySelected.contains(tag)) {
                    viewModel.listCategorySelected.add(tag)
                    it.setBackgroundResource(com.example.smoothie.R.drawable.category_pressed)
                } else {
                    viewModel.listCategorySelected.remove(tag)
                    it.setBackgroundResource(com.example.smoothie.R.drawable.category)
                }
            }
        }
        if (viewModel.listCustomCategories.contains(bindingCurrentItem.textView.tag)) {
            bindingCurrentItem.textView.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    timer = Timer("DeleteCategory", false)
                    timer?.schedule(DOWN_TIME_DELETE_CATEGORY) {
                        CoroutineScope(Dispatchers.Main).launch {
                            this@AddRecipeFragment.deleteCategory(view)
                            Toast.makeText(context, "Категория удалена", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    timer?.cancel()
                }
                true
            }
        }
    }

    private fun deleteCategory(view: View) {
        viewModel.deleteCustomCategoryInSharPrefs(view.tag as String)
        updateTable()
    }

    private fun showDialogAddCategory() {
        val dialogBinding = DialogAddCategoryBinding.inflate(layoutInflater)
        AlertDialog.Builder(context)
            .setTitle("Создание категории")
            .setView(dialogBinding.root)
            .setPositiveButton("Добавить") { _, _ ->
                val nameCategory = dialogBinding.textInput.text.toString()
                if (nameCategory.isNotBlank()) {
                    viewModel.saveCategoryInSharPrefs(nameCategory)
                    updateTable()
                }
            }
            .create().show()
    }

    private fun updateTable() {
        binding.table.removeAllViews()
        countSelectedCategories = 0
        displayCategories()
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
        listCategory = viewModel.listCategorySelected
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
            displayCategories()
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
        const val DOWN_TIME_DELETE_CATEGORY = 3000L
    }
}