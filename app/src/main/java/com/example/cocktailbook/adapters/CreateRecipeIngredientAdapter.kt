package com.example.cocktailbook.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.example.cocktailbook.databinding.CreateRecipeIngredientItemBinding
import com.example.cocktailbook.db.model.Ingredient
import com.example.cocktailbook.db.model.RecipeIngredient

class CreateRecipeIngredientAdapter(private val ingredients: List<Ingredient>) :
    RecyclerView.Adapter<CreateRecipeIngredientAdapter.ViewHolder>() {

    private val selectedIngredients = mutableMapOf<Long, RecipeIngredient>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CreateRecipeIngredientItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.binding.ingredientCheckBox.text = ingredient.name

        ingredient.id?.let { ingredientId ->
            holder.binding.ingredientCheckBox.isChecked = selectedIngredients.containsKey(ingredientId)

            holder.binding.amountUnitLayout.visibility = if (holder.binding.ingredientCheckBox.isChecked) View.VISIBLE else View.GONE
            val recipeIngredient = selectedIngredients[ingredientId]
            holder.binding.amountEditText.setText(recipeIngredient?.quantity?.toString() ?: "")
            holder.binding.unitEditText.setText(recipeIngredient?.unit ?: "")


            holder.binding.ingredientCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    holder.binding.amountUnitLayout.visibility = View.VISIBLE
                    val newRecipeIngredient = RecipeIngredient(
                        recipeId = 0, // Will be set later
                        ingredientId = ingredientId,
                        ingredientName = ingredient.name,
                        quantity = 0.0,
                        unit = ""
                    )
                    selectedIngredients[ingredientId] = newRecipeIngredient
                } else {
                    holder.binding.amountUnitLayout.visibility = View.GONE
                    selectedIngredients.remove(ingredientId)
                }
            }

            holder.binding.amountEditText.addTextChangedListener {
                val amount = it.toString().toDoubleOrNull() ?: 0.0
                selectedIngredients[ingredientId]?.let { ri ->
                    selectedIngredients[ingredientId] = ri.copy(quantity = amount)
                }
            }

            holder.binding.unitEditText.addTextChangedListener {
                val unit = it.toString()
                selectedIngredients[ingredientId]?.let { ri ->
                    selectedIngredients[ingredientId] = ri.copy(unit = unit)
                }
            }
        }
    }

    override fun getItemCount(): Int = ingredients.size

    fun getSelectedRecipeIngredients(): List<RecipeIngredient> {
        return selectedIngredients.values.toList()
    }

    class ViewHolder(val binding: CreateRecipeIngredientItemBinding) : RecyclerView.ViewHolder(binding.root)
}