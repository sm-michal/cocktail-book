package com.example.cocktailbook

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView


class RecipeListAdapter(
    private val context: Context,
    private val titles: List<String>,
    private val contents: Map<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int): Any =
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

        val recipeTitle = convertView!!.findViewById(R.id.recipeTitle) as TextView

        recipeTitle.text = getGroup(groupPosition) as String

        return view!!
    }

    override fun getChildrenCount(groupPosition: Int): Int =
        contents[titles[groupPosition]]?.size ?: 0

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        contents[titles[groupPosition]]?.get(childPosition) ?: ""

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