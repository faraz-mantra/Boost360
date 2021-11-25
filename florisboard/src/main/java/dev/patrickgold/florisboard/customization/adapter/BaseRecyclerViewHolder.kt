package dev.patrickgold.florisboard.customization.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewHolder<Binding : ViewDataBinding> constructor(var binding: Binding) : RecyclerView.ViewHolder(binding.root) {
    abstract fun bindTo(position: Int, item: BaseRecyclerItem?)
}
