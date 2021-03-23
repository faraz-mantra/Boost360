package dev.patrickgold.florisboard.customization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.model.response.Float
import dev.patrickgold.florisboard.customization.viewholder.FloatViewHolder


class UpdatesAdapter : ListAdapter<Float, FloatViewHolder>(UserItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): FloatViewHolder {
        return FloatViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_item_text, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FloatViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }
}

class UserItemDiffCallback : DiffUtil.ItemCallback<Float>() {
    override fun areItemsTheSame(oldItem: Float, newItem: Float): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Float, newItem: Float): Boolean = oldItem == newItem
}