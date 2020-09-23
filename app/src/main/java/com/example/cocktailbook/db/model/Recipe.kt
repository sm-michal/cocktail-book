package com.example.cocktailbook.db.model

data class Recipe (
    val id: Long,
    val name: String,
    val description: String,
    val available: Boolean,
    val ingredients: MutableList<RecipeIngredient> = arrayListOf()
)