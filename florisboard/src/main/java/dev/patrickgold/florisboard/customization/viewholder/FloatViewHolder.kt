package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.model.response.Float

class FloatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindTo(float: Float) {
        // bind views with data
        itemView.findViewById<TextView>(R.id.textView).text = float.message
    }
}