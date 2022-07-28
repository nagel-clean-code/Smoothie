package com.example.smoothie.presentation.views

interface ApiWorkWithDataForBordCategories {
    fun getListCustomCategoriesFromSharPrefs(): MutableList<String>
    fun getSelectedCategoriesFromSharPrefs(): MutableList<String>
    fun saveCategoryInSharPrefs(list: List<String>)
    fun saveSelectedCategoriesInSharPrefs(list: List<String>) //его нужно испрользовать уже в Fragment->onStop()

}