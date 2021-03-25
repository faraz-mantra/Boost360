package dev.patrickgold.florisboard.customization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.model.response.Photo
import dev.patrickgold.florisboard.customization.viewholder.PhotoViewHolder


class PhotosAdapter : ListAdapter<Photo, PhotoViewHolder>(ItemDiffUtilCallback<Photo>()) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): PhotoViewHolder {
        return PhotoViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_item_photos, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }
}