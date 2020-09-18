package com.example.cocktailbook

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.cocktailbook.db.DbHelper
import com.example.cocktailbook.db.model.IngredientType

abstract class AbstractStorageActivity(
    private val layoutId: Int,
    private val titleId: Int,
    private val ingredientType: IngredientType
) : AppCompatActivity() {
    lateinit var lv : ListView
    lateinit var ingredients: List<Ingredient>
    lateinit var adapter: IngredientAdapter
    lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        supportActionBar?.title = getString(titleId)

        lv = findViewById(R.id.list_view)

        dbHelper = DbHelper(applicationContext)

        loadIngredients()

        lv.adapter = IngredientAdapter(ingredients, applicationContext)
    }

    private fun loadIngredients() {
        ingredients = dbHelper.getAllIngredientsByType(ingredientType)
            .map { it -> Ingredient(it.id, it.name, it.inStorage) }
    }
}