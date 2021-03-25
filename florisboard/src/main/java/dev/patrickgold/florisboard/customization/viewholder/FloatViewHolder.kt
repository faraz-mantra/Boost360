package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.model.response.Float

class FloatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val desc: TextView = itemView.findViewById(R.id.textView)
    val image: ImageView = itemView.findViewById(R.id.imageView)

    fun bindTo(float: Float) {
        // bind views with data
        desc.text = float.message

        if (float.imageUri?.isNotEmpty() == true) {
            image.visibility = View.VISIBLE
            Glide.with(image).load(float.imageUri)
                    .placeholder(R.drawable.default_product_image).into(image)
        }else{
            image.visibility = View.GONE
        }
    }
}