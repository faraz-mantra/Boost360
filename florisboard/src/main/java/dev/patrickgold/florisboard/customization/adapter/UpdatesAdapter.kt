package dev.patrickgold.florisboard.customization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.model.response.Float
import dev.patrickgold.florisboard.customization.viewholder.FloatViewHolder


class UpdatesAdapter : ListAdapter<Float, FloatViewHolder>(ItemDiffUtilCallback<Float>()) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): FloatViewHolder {
        return FloatViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_item_update, parent, false)
        )
    }

    override fun onBindViewHolder(holder: FloatViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }
}