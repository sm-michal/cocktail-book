package com.example.cocktailbook.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailbook.R
import com.example.cocktailbook.adapters.CreateRecipeIngredientAdapter
import com.example.cocktailbook.adapters.GlassAdapter
import com.example.cocktailbook.db.DbHelper
import com.example.cocktailbook.db.model.GlassType
import com.example.cocktailbook.db.model.Recipe

class CreateRecipeActivity : AppCompatActivity() {

    private lateinit var dbHelper: DbHelper
    private lateinit var recipeName: EditText
    private lateinit var recipeDescription: EditText
    private lateinit var ingredientsRecyclerView: RecyclerView
    private lateinit var glassGallery: GridView
    private lateinit var saveRecipeButton: Button
    private lateinit var ingredientAdapter: CreateRecipeIngredientAdapter
    private lateinit var glassAdapter: GlassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        dbHelper = DbHelper(this)

        recipeName = findViewById(R.id.recipeName)
        recipeDescription = findViewById(R.id.recipeDescription)
        ingredientsRecyclerView = findViewById(R.id.ingredientsRecyclerView)
        glassGallery = findViewById(R.id.glassGallery)
        saveRecipeButton = findViewById(R.id.saveRecipeButton)

        setupIngredientsRecyclerView()
        setupGlassGallery()

        saveRecipeButton.setOnClickListener {
            saveRecipe()
        }
    }

    private fun setupIngredientsRecyclerView() {
        val ingredients = dbHelper.getAllIngredients()
        ingredientAdapter = CreateRecipeIngredientAdapter(ingredients)
        ingredientsRecyclerView.adapter = ingredientAdapter
        ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupGlassGallery() {
        val glasses = GlassType.values()
        glassAdapter = GlassAdapter(this, glasses)
        glassGallery.adapter = glassAdapter
        glassGallery.setOnItemClickListener { _, _, position, _ ->
            glassAdapter.selectedPosition = position
            glassAdapter.notifyDataSetChanged()
        }
    }

    private fun saveRecipe() {
        val name = recipeName.text.toString()
        val description = recipeDescription.text.toString()
        val selectedIngredients = ingredientAdapter.getSelectedRecipeIngredients()
        val selectedGlassPosition = glassAdapter.selectedPosition

        if (name.isBlank()) {
            Toast.makeText(this, "Please enter a recipe name", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isBlank()) {
            Toast.makeText(this, "Please enter a recipe description", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedIngredients.isEmpty()) {
            Toast.makeText(this, "Please select at least one ingredient", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedGlassPosition == -1) {
            Toast.makeText(this, "Please select a glass", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGlass = GlassType.values()[selectedGlassPosition]

        val recipe = Recipe(
            name = name,
            description = description,
            glassType = selectedGlass,
            ingredients = selectedIngredients.toMutableList()
        )
        dbHelper.saveRecipe(recipe)
        Toast.makeText(this, "Recipe saved!", Toast.LENGTH_SHORT).show()
        finish()
    }
}