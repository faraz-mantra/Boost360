package dev.patrickgold.florisboard.customization.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T : BaseRecyclerItem>(
        val list: ArrayList<T>,
        private var itemClickListener: OnItemClickListener?,
) : RecyclerView.Adapter<BaseRecyclerViewHolder>() {

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        holder.bindTo(position, list[position])
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].getViewType()
    }

    override fun getItemCount(): Int = list.size

    fun appendList(newList: List<T>) {
        this.list.addAll(newList)
        notifyDataSetChanged()
    }

    fun submitList(newList: List<T>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}