package com.example.cocktailbook.adapters

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.example.cocktailbook.R
import com.example.cocktailbook.db.model.GlassType

class GlassAdapter(private val context: Context, private val glasses: Array<GlassType>) : BaseAdapter() {

    var selectedPosition = -1

    override fun getCount(): Int = glasses.size

    override fun getItem(position: Int): Any = glasses[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView = if (convertView == null) {
            ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(250, 250)
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(8, 8, 8, 8)
            }
        } else {
            convertView as ImageView
        }

        val glass = glasses[position]
        val resourceId = context.resources.getIdentifier(
            glass.name.lowercase(),
            "drawable",
            context.packageName
        )
        if (resourceId != 0) {
            imageView.setImageResource(resourceId)
        } else {
            // Set a placeholder if the image is not found
            imageView.setImageResource(R.drawable.ic_launcher_background)
        }


        if (position == selectedPosition) {
            imageView.setBackgroundColor(Color.LTGRAY)
        } else {
            imageView.setBackgroundColor(Color.TRANSPARENT)
        }

        return imageView
    }
}