package dev.patrickgold.florisboard.customization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails
import dev.patrickgold.florisboard.customization.viewholder.DetailsViewHolder


class DetailsAdapter(private val listener: OnItemClickListener) : ListAdapter<CustomerDetails, DetailsViewHolder>(ItemDiffUtilCallback<CustomerDetails>()) {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): DetailsViewHolder {
        return DetailsViewHolder(
                LayoutInflater.from(parent.context)
                        .inflate(R.layout.adapter_item_details, parent, false),
                listener
        )
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }
}