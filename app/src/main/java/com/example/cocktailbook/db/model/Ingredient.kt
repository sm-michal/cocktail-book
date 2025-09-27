package com.example.cocktailbook.db.model

data class Ingredient(
    val id: Long? = null,
    val type: IngredientType,
    val name: String
)