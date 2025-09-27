package com.example.cocktailbook.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseExpandableListAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.cocktailbook.R
import com.example.cocktailbook.db.model.GlassType
import com.example.cocktailbook.db.model.Recipe
import java.util.*
import kotlin.math.roundToInt


class RecipeListAdapter(
    private val context: Context,
    private val titles: List<Recipe>
) : BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int): Recipe =
        titles[groupPosition]

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean =
        false

    override fun hasStableIds(): Boolean =
        false

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        var view = convertView

        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.recipe_title_layout, null)
        }

        val recipe = getGroup(groupPosition)
        val recipeTitle = view!!.findViewById(R.id.recipeTitle) as TextView
        recipeTitle.text = recipe.name
        recipeTitle.setRecipeGlassIcon(recipe.glassType)
        recipeTitle.setBackgroundColor(
            ContextCompat.getColor(
                context,
                if (recipe.available)
                    android.R.color.holo_green_light
                else
                    android.R.color.white
            )
        )

        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int = 1

    override fun getChild(groupPosition: Int, childPosition: Int): RecipeContent =
        getGroup(groupPosition).toRecipeContent()

    override fun getGroupId(groupPosition: Int): Long =
        groupPosition.toLong()

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {

        var view = convertView
        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.recipe_content_layout, null)
        }

        val recipeContent = getChild(groupPosition, childPosition)
        val recipeText = view!!.findViewById(R.id.recipeContent) as TextView
        recipeText.text = recipeContent.description

        val ingredientList = view.findViewById(R.id.recipeIngredients) as ListView
        ingredientList.layoutParams.height = recipeContent.ingredients.size * 60
        ingredientList.adapter = ArrayAdapter<String>(context, R.layout.recipe_ingredient,
            recipeContent.ingredients.map { it.toString() })

        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long =
        childPosition.toLong()

    override fun getGroupCount(): Int =
        titles.size
}

data class RecipeIngredientDto (
    val quantity: Double,
    val unit: String,
    val name: String
) {
    override fun toString(): String {
        return "$quantity $unit $name"
    }
}


data class RecipeContent (
    val ingredients: MutableList<RecipeIngredientDto> = arrayListOf(),
    val description: String
)

fun Recipe.toRecipeContent() =
    RecipeContent(
        ingredients
            .map { RecipeIngredientDto(it.quantity, it.unit, it.ingredientName) }
            .toMutableList(),
        description
    )

fun TextView.setRecipeGlassIcon(glassType: GlassType) {
    val imageResource = context.resources.getIdentifier(
        glassType.toString().toLowerCase(Locale.getDefault()),
        "drawable", context.packageName)
    val scaledDrawable = ContextCompat.getDrawable(context, imageResource)
    val pixelDrawableSize = (lineHeight * 0.9).roundToInt()
    scaledDrawable?.setBounds(0, 0, pixelDrawableSize, pixelDrawableSize)

    setCompoundDrawables(null, null, scaledDrawable, null)
}