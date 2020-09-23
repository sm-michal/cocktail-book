package com.example.cocktailbook

import android.content.Intent
import android.view.View
import com.example.cocktailbook.db.model.IngredientType

class StorageAdditionals : AbstractStorageActivity(
    R.layout.activity_storage_additionals,
    R.string.additionals,
    IngredientType.ADDITIONAL
) {

    fun showRecipes(view: View) {
        startActivity(Intent(this, RecipesList::class.java))
    }
}