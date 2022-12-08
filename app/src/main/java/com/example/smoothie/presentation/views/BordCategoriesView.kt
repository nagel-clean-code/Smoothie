package com.example.smoothie.presentation.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.smoothie.ListCategory
import com.example.smoothie.R
import com.example.smoothie.databinding.BoardCategoriesBinding
import com.example.smoothie.databinding.CategoryItemBinding
import com.example.smoothie.databinding.DialogAddCategoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule
import kotlin.properties.Delegates

class BordCategoriesView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val binding: BoardCategoriesBinding
    private var countRows by Delegates.notNull<Int>()
    private var timer: Timer? = null
    private val inflater: LayoutInflater
    private lateinit var externalAPI: ApiWorkWithDataForBordCategories
    lateinit var listCustomCategories: MutableList<String>
    lateinit var listCategorySelected: MutableList<String>

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.board_categories, this, true)
        binding = BoardCategoriesBinding.bind(this)
        initialAttributes(attrs, defStyleAttr, defStyleRes)
    }

    /** Перед работой нужно обязательно инициализировать, иначе view не будет работать*/
    fun setupApi(api: ApiWorkWithDataForBordCategories) {
        this.externalAPI = api
        listCustomCategories = externalAPI.getListCustomCategoriesFromSharPrefs()
        listCategorySelected = externalAPI.getSelectedCategoriesFromSharPrefs()
    }

    private fun initialAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.BordCategoriesView, defStyleAttr, defStyleRes)
        typedArray.getString(R.styleable.BordCategoriesView_headerText)
        countRows = typedArray.getInt(R.styleable.BordCategoriesView_countRows, 3)
        typedArray.recycle()
    }

    fun displayCategories() {
        ListCategory.values().forEach {
            addCategoryTable(it.named)
        }
        listCustomCategories.forEach {
            addCategoryTable(it)
        }
        addCategoryTable("+")
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

        val bindingCurrentItem = CategoryItemBinding.inflate(inflater)
        bindingCurrentItem.textView.text = item
        bindingCurrentItem.textView.tag = item
        if (listCategorySelected.contains(item)) {
            bindingCurrentItem.textView.setBackgroundResource(R.drawable.category_pressed)
        }

        initEventsCategory(bindingCurrentItem)

        currentRow.addView(bindingCurrentItem.root, (countSelectedCategories - 1) % COUNT_ROWS_CATEGORIES)
        if (countSelectedCategories % COUNT_ROWS_CATEGORIES == 1) {
            binding.table.addView(currentRow, countSelectedCategories / COUNT_ROWS_CATEGORIES)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEventsCategory(bindingCurrentItem: CategoryItemBinding) {
        bindingCurrentItem.textView.setOnClickListener {
            val tag = (it.tag as String)
            if (tag == "+") {
                showDialogAddCategory()
            } else {
                val currentColor = if (bindingCurrentItem.textView.currentTextColor == Color.WHITE)
                    resources.getColor(R.color.carrot)
                else
                    Color.WHITE
                bindingCurrentItem.textView.setTextColor(currentColor)
                changingBackground(tag, it)
            }
        }
        if (listCustomCategories.contains(bindingCurrentItem.textView.tag)) {
            bindingCurrentItem.textView.setOnTouchListener { view, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    timer = Timer("DeleteCategory", false)
                    timer?.schedule(DOWN_TIME_DELETE_CATEGORY) {
                        CoroutineScope(Dispatchers.Main).launch {
                            this@BordCategoriesView.deleteCategory(view)
                            Toast.makeText(context, "Категория удалена", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                val tag = (view.tag as String)
                if (event.action == MotionEvent.ACTION_UP) {
                    changingBackground(tag, view)
                    timer?.cancel()
                }
                true
            }
        }
    }

    private fun changingBackground(tag: String, view: View) {
        if (!listCategorySelected.contains(tag)) {
            listCategorySelected.add(tag)
            view.setBackgroundResource(R.drawable.category_pressed)
        } else {
            listCategorySelected.remove(tag)
            view.setBackgroundResource(R.drawable.category)
        }
    }

    fun clearTable() {
        binding.table.removeAllViews()
        countSelectedCategories = 0
        listCategorySelected.clear()
    }

    private fun updateTable() {
        binding.table.removeAllViews()
        countSelectedCategories = 0
        displayCategories()
    }

    private fun deleteCategory(view: View) {
        listCustomCategories.remove(view.tag as String)
        listCategorySelected.remove(view.tag as String)
        externalAPI.saveCategoryInSharPrefs(listCustomCategories)
        updateTable()
    }

    private fun showDialogAddCategory() {
        val dialogBinding = DialogAddCategoryBinding.inflate(inflater)
        AlertDialog.Builder(context)
            .setTitle("Создание категории")
            .setView(dialogBinding.root)
            .setPositiveButton("Добавить") { _, _ ->
                val nameCategory = dialogBinding.textInput.text.toString()
                if (nameCategory.isNotBlank()) {
                    listCustomCategories.add(nameCategory)
                    externalAPI.saveCategoryInSharPrefs(listCustomCategories)
                    updateTable()
                }
            }
            .create().show()
    }

    companion object {
        const val COUNT_ROWS_CATEGORIES = 3
        const val DOWN_TIME_DELETE_CATEGORY = 1000L
    }
}