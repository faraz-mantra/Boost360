package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.model.response.Photo

class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val image: ImageView = itemView.findViewById(R.id.imageView)

    fun bindTo(photo: Photo) {
        // bind views with data

        Picasso.get().load(photo.imageUri)
                .placeholder(R.drawable.placeholder_ic_image_padded)
                .fit().centerCrop()
                .into(image)
    }
}