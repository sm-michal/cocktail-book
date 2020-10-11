package com.example.cocktailbook.db.model

data class Recipe (
    val id: Long? = null,
    val name: String,
    val description: String,
    val available: Boolean = false,
    val ingredients: MutableList<RecipeIngredient> = arrayListOf()
)