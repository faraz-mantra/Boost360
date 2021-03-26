package dev.patrickgold.florisboard.customization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.patrickgold.florisboard.customization.viewholder.DetailsViewHolder
import dev.patrickgold.florisboard.customization.viewholder.FloatViewHolder
import dev.patrickgold.florisboard.customization.viewholder.PhotoViewHolder
import dev.patrickgold.florisboard.customization.viewholder.ProductViewHolder


class SharedAdapter : ListAdapter<BaseRecyclerItem, BaseRecyclerViewHolder>(ItemDiffUtilCallback<BaseRecyclerItem>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemType = FeaturesEnum.values()[viewType]
        val view = inflater.inflate(itemType.getLayout(), parent, false)
        return when (itemType) {
            FeaturesEnum.UPDATES -> FloatViewHolder(view)
            FeaturesEnum.PRODUCTS -> ProductViewHolder(view)
            FeaturesEnum.PHOTOS -> PhotoViewHolder(view)
            FeaturesEnum.DETAILS -> DetailsViewHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getViewType()
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }
}