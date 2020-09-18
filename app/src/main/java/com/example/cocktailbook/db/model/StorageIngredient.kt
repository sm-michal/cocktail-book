package com.example.cocktailbook.db.model

data class StorageIngredient(
    val id: Long,
    val type: IngredientType,
    val name: String,
    val inStorage: Boolean
)