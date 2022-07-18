package com.example.smoothie.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import coil.load
import com.example.smoothie.data.storage.models.RecipeEntity
import com.example.smoothie.databinding.FragmentAddRecipeBinding
import com.example.smoothie.presentation.images.ImagePicker
import com.example.smoothie.presentation.viewmodels.AddRecipeViewModel
import com.example.smoothie.utils.convertDrawableToByteArray
import com.example.smoothie.utils.decodeFromBase64IntoDrawable
import com.example.smoothie.utils.encodeToBase64
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRecipeFragment : BaseFragment() {

    private lateinit var binding: FragmentAddRecipeBinding

    private var countIngredient: Int = 2
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
        processingInputIngredients()
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
        viewModel.resultNameLiveDataMutable.observe(viewLifecycleOwner) { text ->
            binding.editTextNameRecipe.text.clear()
            binding.editTextNameRecipe.text.append(text)
        }
        viewModel.resultImageLiveDataMutable.observe(viewLifecycleOwner) { image ->
            binding.imagePreview.load(image)
        }
        viewModel.load()
        viewModel.loadImage(::decodeFromBase64IntoDrawable)
        return binding.root
    }

    override fun onStop() {
        viewModel.resultImageLiveDataMutable.removeObservers(viewLifecycleOwner)
        viewModel.saveName(binding.editTextNameRecipe.text.toString())
        viewModel.saveIngredients(binding.enteringIngredients.text.toString())
        viewModel.saveImageInSharPref(binding.imagePreview.drawable, ::encodeToBase64)
        super.onStop()
    }


    private fun saveRecipeToDatabase() {
        if (binding.editTextNameRecipe.text.toString().isEmpty()) {
            Toast.makeText(context, "Название рецепта не задано!", Toast.LENGTH_SHORT).show()
        } else if (binding.enteringIngredients.text.toString().isEmpty() &&
            binding.enteringRecipe.text.toString().isEmpty()
        ) {
            Toast.makeText(context, "Задайте рецепт", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.setRecipeToDataBase(
                RecipeEntity(
                    name = binding.editTextNameRecipe.text.toString(),
                    ingredients = binding.enteringIngredients.text.toString(),
                    recipe = binding.enteringRecipe.text.toString(),
                    description = binding.enteringDescription.text.toString()
                ),
                convertDrawableToByteArray(binding.imagePreview.drawable)
            )
            Toast.makeText(context, "Рецепт успешно добавлен", Toast.LENGTH_SHORT).show()
            binding.editTextNameRecipe.text.clear()
            binding.enteringIngredients.text?.clear()
            binding.enteringRecipe.text?.clear()
            binding.enteringDescription.text?.clear()
            binding.imagePreview.setImageResource(0)
        }
    }


    /** Обработка ввода ингридиентов (автоматическая нумерация строк) */
    private fun processingInputIngredients() {
        val twTextWatcher = object : TextWatcher {
            private var lengthPrev: Int = 0
            private var prevSymbol: Int = -1
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length!! > 0) {
                    lengthPrev = p0.length
                    prevSymbol = p0.last().code
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.enteringIngredientsLayout.editText?.text?.isEmpty() == true) {
                    binding.enteringIngredientsLayout.editText?.text?.append("1. ")
                }
                if (lengthPrev > s?.length!!) {
                    if (prevSymbol == 10) {
                        --countIngredient
                    }
                    return
                }
                if (s.last().code == 10) {
                    binding.enteringIngredientsLayout.editText?.text?.append("${countIngredient++}. ")
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        }
        binding.enteringIngredientsLayout.editText?.addTextChangedListener(twTextWatcher)

        binding.enteringIngredients.onFocusChangeListener =
            View.OnFocusChangeListener { _, _ ->
                if (binding.enteringIngredientsLayout.editText?.text?.isEmpty() == true) {
                    binding.enteringIngredientsLayout.editText?.text?.append("1. ")
                }
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