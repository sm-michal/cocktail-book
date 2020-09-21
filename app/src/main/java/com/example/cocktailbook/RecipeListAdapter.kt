package com.example.cocktailbook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.cocktailbook.db.model.Recipe


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

        val recipeTitle = view!!.findViewById(R.id.recipeTitle) as TextView
        val recipe = getGroup(groupPosition)
        recipeTitle.text = recipe.name
        if (recipe.available) {
            recipeTitle.setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
        }


        return view!!
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

        val recipeText = convertView!!.findViewById(R.id.recipeContent) as TextView
        recipeText.text = """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. 
            Vivamus semper eu ex ut elementum. In eu quam sit amet lectus dignissim efficitur 
            et vel tortor. Morbi ac sollicitudin eros. 
        """

        return view!!
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
)


data class RecipeContent (
    val ingredients: List<RecipeIngredientDto>,
    val description: String
)

fun Recipe.toRecipeContent() =
    RecipeContent(
        ingredients.map { it ->
            RecipeIngredientDto(it.quantity, it.unit, it.ingredientName)
        },
        description
    )