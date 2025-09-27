package com.example.cocktailbook.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListView
import com.example.cocktailbook.R
import com.example.cocktailbook.adapters.RecipeListAdapter
import com.example.cocktailbook.db.DbHelper
import com.example.cocktailbook.db.model.Recipe
import java.util.stream.IntStream

class RecipesList : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes_list)

        supportActionBar?.title = getString(R.string.recipesList)

        val lv = findViewById<ExpandableListView>(R.id.recipes_list_view)

        dbHelper = DbHelper(applicationContext)

        lv.setAdapter(RecipeListAdapter(applicationContext, loadRecipes()))
        lv.setOnGroupExpandListener { groupPosition ->
            IntStream.range(0, lv.adapter.count).filter { it != groupPosition }.forEach {
                lv.collapseGroup(it)
            }
        }
    }

    private fun loadRecipes(): List<Recipe> = dbHelper.getRecipes()
}