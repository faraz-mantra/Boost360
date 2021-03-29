package dev.patrickgold.florisboard.customization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.patrickgold.florisboard.customization.viewholder.*
import java.util.*


class SharedAdapter<T : BaseRecyclerItem?>(val list: ArrayList<T?>, val listener: OnItemClickListener? = null)
    : RecyclerView.Adapter<BaseRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemType = FeaturesEnum.values()[viewType]
        val view = inflater.inflate(itemType.getLayout(), parent, false)
        return when (itemType) {
            FeaturesEnum.LOADER -> LoaderViewHolder(view, listener)
            FeaturesEnum.UPDATES -> FloatViewHolder(view, listener)
            FeaturesEnum.PRODUCTS -> ProductViewHolder(view, listener)
            FeaturesEnum.PHOTOS -> PhotoViewHolder(view, listener)
            FeaturesEnum.DETAILS -> DetailsViewHolder(view, listener)
        }
    }

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        holder.bindTo(position, list[position])
    }

    override fun getItemViewType(position: Int): Int {
        return list[position]?.getViewType() ?: FeaturesEnum.LOADER.ordinal
    }

    override fun getItemCount(): Int = list.size

    fun submitList(newList: List<T>, hasMoreItems: Boolean = false) {
        if (list != newList) {
            // list is not same, then append new items
            list.addAll(newList)
        }
        // for pagination
        if (hasMoreItems) {
            list.add(null)
        }
        notifyDataSetChanged()
    }

    fun clearList() {
        list.clear()
        notifyDataSetChanged()
    }
}