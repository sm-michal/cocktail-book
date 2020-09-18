package com.example.cocktailbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.example.cocktailbook.db.DbHelper
import com.example.cocktailbook.db.model.IngredientType

class Juices : AbstractStorageActivity(
    R.layout.activity_juices,
    R.string.juices,
    IngredientType.JUICE
) {

    fun openAdditional(view: View) {
        startActivity(Intent(this, StorageAdditionals::class.java))
    }
}