package com.example.cocktailbook

import android.content.Intent
import android.view.View
import com.example.cocktailbook.db.model.IngredientType

class StorageBaseAlcohols : AbstractStorageActivity(
    R.layout.storage_base_alcohols,
    R.string.base_alcs,
    IngredientType.BASE_ALCOHOL
) {

    fun openOtherAlcohols(view: View) {
        startActivity(Intent(this, OtherAlcohols::class.java))
    }
}

