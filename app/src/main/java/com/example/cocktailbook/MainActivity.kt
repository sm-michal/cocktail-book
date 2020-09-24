package com.example.cocktailbook

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.cocktailbook.db.DbHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun fillStorage(view: View) {
        startActivity(Intent(this, StorageBaseAlcohols::class.java))
    }

    fun showRecipes(view: View) {
        startActivity(Intent(this, RecipesList::class.java))
    }

    fun resetDb(view: View) {
        DbHelper(applicationContext).resetDbData()
    }

    fun whatToBuy(view: View) {
        DbHelper(applicationContext).findMostValuableIngredientToBuy()
    }
}