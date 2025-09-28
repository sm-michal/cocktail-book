package com.example.cocktailbook.activities

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.view.View
import androidx.core.content.FileProvider
import com.example.cocktailbook.R
import com.example.cocktailbook.activities.storage.StorageBaseAlcohols
import com.example.cocktailbook.db.DbHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
                        recipes.joinToString(prefix = "<li>&nbsp;", separator = "</li><li>&nbsp;", postfix = "</li>") +
                        "</ul>"
            }
            .values.joinToString(separator = "<br>>")

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.what_to_buy)
            .setMessage(Html.fromHtml(formattedResponse, FROM_HTML_MODE_COMPACT))
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }

            .show()
    }

    fun createRecipe(view: View) {
        startActivity(Intent(this, CreateRecipeActivity::class.java))
    }

    fun exportDb(view: View) {
        val files = ArrayList<Uri>()
        try {
            files.add(createCsv("recipes", DbHelper(applicationContext).getRecipesForExport()))
            files.add(createCsv("recipes_ingredients", DbHelper(applicationContext).getRecipesIngredientsForExport()))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, files)
            type = "text/csv"
        }
        startActivity(Intent.createChooser(shareIntent, "Share DB"))
    }

    private fun createCsv(fileName: String, data: Cursor): Uri {
        val file = File(cacheDir, "$fileName.csv")
        FileOutputStream(file).use { outputStream ->
            outputStream.write(cursorToCsv(data).toByteArray())
        }
        return FileProvider.getUriForFile(this, "com.example.cocktailbook.fileprovider", file)
    }

    private fun cursorToCsv(cursor: Cursor): String {
        val csv = StringBuilder()
        csv.append(cursor.columnNames.joinToString(",")).append("\n")
        while (cursor.moveToNext()) {
            (0 until cursor.columnCount).forEach {
                csv.append(cursor.getString(it)).append(",")
            }
            csv.append("\n")
        }
        return csv.toString()
    }
}