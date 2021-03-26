package dev.patrickgold.florisboard.customization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.patrickgold.florisboard.customization.viewholder.DetailsViewHolder
import dev.patrickgold.florisboard.customization.viewholder.FloatViewHolder
import dev.patrickgold.florisboard.customization.viewholder.PhotoViewHolder
import dev.patrickgold.florisboard.customization.viewholder.ProductViewHolder


class SharedAdapter(private var listener: OnItemClickListener? = null) : ListAdapter<BaseRecyclerItem, BaseRecyclerViewHolder>(ItemDiffUtilCallback<BaseRecyclerItem>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemType = FeaturesEnum.values()[viewType]
        val view = inflater.inflate(itemType.getLayout(), parent, false)
        return when (itemType) {
            FeaturesEnum.UPDATES -> FloatViewHolder(view,listener)
            FeaturesEnum.PRODUCTS -> ProductViewHolder(view,listener)
            FeaturesEnum.PHOTOS -> PhotoViewHolder(view,listener)
            FeaturesEnum.DETAILS -> DetailsViewHolder(view,listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).getViewType()
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        holder.bindTo(position, getItem(position))
    }
}