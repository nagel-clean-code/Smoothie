package com.example.smoothie.screens

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.smoothie.databinding.FragmentAddRecipeBinding

class AddRecipeFragment : Fragment() {
    private lateinit var binding: FragmentAddRecipeBinding
    private var countIngredient: Int = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddRecipeBinding.inflate(layoutInflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        focusInputAndAppearanceKeyboard()
        processingInputIngredients()
        return binding.root
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