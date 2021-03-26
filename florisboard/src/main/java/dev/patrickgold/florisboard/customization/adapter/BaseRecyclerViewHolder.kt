package dev.patrickgold.florisboard.customization.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    abstract fun bindTo(item: BaseRecyclerItem?)
    override fun onClick(v: View?) {

    }
}
