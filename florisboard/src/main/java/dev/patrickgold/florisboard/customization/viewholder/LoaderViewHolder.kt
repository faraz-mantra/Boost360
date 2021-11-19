package dev.patrickgold.florisboard.customization.viewholder

import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.databinding.PaginationLoaderKeyboardBinding

class LoaderViewHolder(binding: PaginationLoaderKeyboardBinding, val listener: OnItemClickListener?) : BaseRecyclerViewHolder<PaginationLoaderKeyboardBinding>(binding) {

    override fun bindTo(position: Int, item: BaseRecyclerItem?) {

    }
}