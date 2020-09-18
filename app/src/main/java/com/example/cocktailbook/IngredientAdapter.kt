package com.example.cocktailbook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import com.example.cocktailbook.db.DbHelper

class IngredientAdapter(
    private val ingredients: List<Ingredient>,
    context: Context
) : ArrayAdapter<Ingredient>(context, R.layout.ingredient_layout, ingredients) {

    private val dbHelper = DbHelper(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        val holder: IngredientHolder

        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(R.layout.ingredient_layout, null)

            holder = IngredientHolder(
                v.findViewById(R.id.ingredientCheckBox) as CheckBox,
                v.findViewById(R.id.ingredientName) as TextView
            )
        } else {
            holder = convertView.tag as IngredientHolder
        }

        val ingredient = ingredients[position]
        holder.ingredientCheckBox.isChecked = ingredient.selected
        holder.ingredientCheckBox.tag = ingredient
        holder.ingredientCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                dbHelper.saveNewIngredientInStorage(ingredient.id)
            else
                dbHelper.removeFromStorage(ingredient.id)
        }
        holder.ingredientName.text = ingredient.name

        return v!!
    }
}

data class Ingredient(
    val id: Long,
    val name: String,
    var selected: Boolean
)

class IngredientHolder(
    val ingredientCheckBox: CheckBox,
    val ingredientName: TextView
)