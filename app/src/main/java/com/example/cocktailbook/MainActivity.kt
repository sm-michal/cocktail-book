package com.example.cocktailbook

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.view.View
import com.example.cocktailbook.db.DbHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
        val ingredientsMap = DbHelper(applicationContext).findMostValuableIngredientToBuy()

        val formattedResponse = ingredientsMap
            .mapValues { (ingredients, recipes) ->
                "${getString(R.string.buy)} <b>$ingredients</b> ${getString(R.string.toBeAble)}:" +
                        "<ul>" +
                        recipes.joinToString(prefix = "<li>", separator = "</li><li>", postfix = "</li>") +
                        "</ul>"
            }
            .values.joinToString(separator = "<br>>")

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.what_to_buy)
            .setMessage(Html.fromHtml(formattedResponse, FROM_HTML_MODE_COMPACT))
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }

            .show()
    }
}