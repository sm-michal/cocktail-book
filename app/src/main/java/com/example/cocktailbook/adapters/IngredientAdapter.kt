package com.example.cocktailbook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailbook.R
import com.example.cocktailbook.db.DbHelper

class IngredientAdapter(
    private val ingredients: List<IngredientDto>,
    context: Context
) : RecyclerView.Adapter<IngredientHolder>() {

    private val dbHelper = DbHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientHolder {
        val ingredientView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ingredient_layout, parent, false) as LinearLayout

        val checkBox = ingredientView.findViewById(R.id.ingredientCheckBox) as CheckBox
        val textView = ingredientView.findViewById(R.id.ingredientName) as TextView

        return IngredientHolder(checkBox, textView, ingredientView)
    }

    override fun onBindViewHolder(holder: IngredientHolder, position: Int) {
        val ingredient = ingredients[position]

        holder.ingredientCheckBox.isChecked = ingredient.selected
        holder.ingredientCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                dbHelper.saveNewIngredientInStorage(ingredient.id)
            else
                dbHelper.removeFromStorage(ingredient.id)
        }
        holder.ingredientName.text = ingredient.name
    }

    override fun getItemCount(): Int = ingredients.size
}

data class IngredientDto(
    val id: Long,
    val name: String,
    var selected: Boolean
)

class IngredientHolder(
    val ingredientCheckBox: CheckBox,
    val ingredientName: TextView,
    parentView: LinearLayout
): RecyclerView.ViewHolder(parentView)