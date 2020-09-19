package com.example.cocktailbook

import android.content.Intent
import android.view.View
import com.example.cocktailbook.db.model.IngredientType

class StorageOtherAlcohols : AbstractStorageActivity(
    R.layout.activity_other_alcohols,
    R.string.other_alcs,
    IngredientType.LIQUOR
) {

    fun openJuices(view: View) {
        startActivity(Intent(this, StorageJuices::class.java))
    }
}