package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Picasso
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.Photo

class PhotoViewHolder(itemView: View, val listener: OnItemClickListener?) : BaseRecyclerViewHolder(itemView) {

    private val image: ImageView = itemView.findViewById(R.id.imageView)
    private val itemCardView = itemView

    override fun bindTo(position: Int, item: BaseRecyclerItem?) {
        val photo = item as Photo
        // bind views with data
        Picasso.get().load(photo.imageUri)
                .placeholder(R.drawable.placeholder_ic_image_padded)
                .fit().centerCrop()
                .into(image)

        itemCardView.setOnClickListener { listener?.onItemClick(position, photo) }
    }
}