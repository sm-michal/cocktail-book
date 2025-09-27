package com.example.cocktailbook.activities.storage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailbook.adapters.IngredientDto
import com.example.cocktailbook.adapters.IngredientAdapter
import com.example.cocktailbook.R
import com.example.cocktailbook.db.DbHelper
import com.example.cocktailbook.db.model.IngredientType

abstract class AbstractStorageActivity(
    private val layoutId: Int,
    private val titleId: Int,
    private val ingredientType: IngredientType
) : AppCompatActivity() {
    private lateinit var recyclerView : RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)

        dbHelper = DbHelper(applicationContext)

        supportActionBar?.title = getString(titleId)

        viewManager = LinearLayoutManager(this)

        recyclerView = findViewById<RecyclerView>(R.id.list_view).apply {
            setHasFixedSize(true)

            layoutManager = viewManager

            adapter = IngredientAdapter(loadIngredients(), applicationContext)
        }
    }

    private fun loadIngredients() =
        dbHelper.getAllIngredientsByType(ingredientType)
            .map { IngredientDto(it.id, it.name, it.inStorage) }

}