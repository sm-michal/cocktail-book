package com.example.cocktailbook.db.model

data class RecipeIngredient (
    val recipeId: Long,
    val ingredientId: Long,
    val ingredientName: String,
    val quantity: Double,
    val unit: String
)