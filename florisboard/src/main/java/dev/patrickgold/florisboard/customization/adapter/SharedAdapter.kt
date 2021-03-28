package dev.patrickgold.florisboard.customization.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import dev.patrickgold.florisboard.customization.viewholder.*
import java.util.*


class SharedAdapter<T : BaseRecyclerItem>(list: ArrayList<T>, val listener: OnItemClickListener? = null)
    : BaseRecyclerViewAdapter<T>(list, listener) {

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
}