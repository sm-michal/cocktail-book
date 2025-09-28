package com.example.cocktailbook.db.model

data class Recipe (
    val id: Long? = null,
    val name: String,
    val description: String,
    val available: Boolean = false,
    val glassType: GlassType,
    val ingredients: MutableList<RecipeIngredient> = arrayListOf(),
    val isDeleted: Boolean = false
)

enum class GlassType {
    SHOT, MARTINI, HURRICANE, LOW, HIGH, WINE
}