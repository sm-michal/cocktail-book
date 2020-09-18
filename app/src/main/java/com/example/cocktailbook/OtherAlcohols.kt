package com.example.cocktailbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.example.cocktailbook.db.DbHelper
import com.example.cocktailbook.db.model.IngredientType

class OtherAlcohols : AbstractStorageActivity(
    R.layout.activity_other_alcohols,
    R.string.other_alcs,
    IngredientType.LIQUOR
) {

    fun openJuices(view: View) {
        startActivity(Intent(this, Juices::class.java))
    }
}