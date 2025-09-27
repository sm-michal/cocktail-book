package com.example.cocktailbook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailbook.db.model.Ingredient
import com.example.cocktailbook.db.model.RecipeIngredient

class CreateRecipeIngredientAdapter(private val ingredients: List<Ingredient>) :
    RecyclerView.Adapter<CreateRecipeIngredientAdapter.ViewHolder>() {

    private val selectedIngredients = mutableMapOf<Long, RecipeIngredient>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.create_recipe_ingredient_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.checkBox.text = ingredient.name
        holder.checkBox.isChecked = selectedIngredients.containsKey(ingredient.id)

        holder.amountUnitLayout.visibility = if (holder.checkBox.isChecked) View.VISIBLE else View.GONE
        val recipeIngredient = selectedIngredients[ingredient.id]
        holder.amountEditText.setText(recipeIngredient?.quantity?.toString() ?: "")
        holder.unitEditText.setText(recipeIngredient?.unit ?: "")


        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                holder.amountUnitLayout.visibility = View.VISIBLE
                val newRecipeIngredient = RecipeIngredient(
                    recipeId = 0, // Will be set later
                    ingredientId = ingredient.id!!,
                    ingredientName = ingredient.name,
                    quantity = 0.0,
                    unit = ""
                )
                selectedIngredients[ingredient.id] = newRecipeIngredient
            } else {
                holder.amountUnitLayout.visibility = View.GONE
                selectedIngredients.remove(ingredient.id)
            }
        }

        holder.amountEditText.addTextChangedListener {
            val amount = it.toString().toDoubleOrNull() ?: 0.0
            selectedIngredients[ingredient.id]?.let { ri ->
                selectedIngredients[ingredient.id] = ri.copy(quantity = amount)
            }
        }

        holder.unitEditText.addTextChangedListener {
            val unit = it.toString()
            selectedIngredients[ingredient.id]?.let { ri ->
                selectedIngredients[ingredient.id] = ri.copy(unit = unit)
            }
        }
    }

    override fun getItemCount(): Int = ingredients.size

    fun getSelectedRecipeIngredients(): List<RecipeIngredient> {
        return selectedIngredients.values.toList()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.ingredientCheckBox)
        val amountUnitLayout: LinearLayout = view.findViewById(R.id.amountUnitLayout)
        val amountEditText: EditText = view.findViewById(R.id.amountEditText)
        val unitEditText: EditText = view.findViewById(R.id.unitEditText)
    }
}