package com.example.smoothie

enum class Category(
    val id: Int,
    val named: String,
    ) {
    LUNCH(1, "Обед"),
    DINNER(2, "Ужин"),
    BREAKFAST(3, "Завтрак"),

    CAKE(4, "Торт"),
    SALAD(5, "Салат"),
    SANDWICH(6, "Бутерброд"),
    DESSERT(7, "Десерт"),
    SOUP(8, "Суп"),
    PORRIDGE(8, "Каша"),
    SMOOTHIE(8, "Смузи")

}