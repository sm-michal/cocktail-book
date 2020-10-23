package com.example.cocktailbook.activities

import android.content.Intent
import android.view.View
import com.example.cocktailbook.R
import com.example.cocktailbook.db.model.IngredientType

class StorageJuices : AbstractStorageActivity(
    R.layout.activity_juices,
    R.string.juices,
    IngredientType.JUICE
) {

    fun openAdditional(view: View) {
        startActivity(Intent(this, StorageAdditionals::class.java))
    }
}