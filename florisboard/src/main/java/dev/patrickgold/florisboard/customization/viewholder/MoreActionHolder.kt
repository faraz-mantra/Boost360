package dev.patrickgold.florisboard.customization.viewholder

import dev.patrickgold.florisboard.customization.adapter.*
import dev.patrickgold.florisboard.customization.model.response.moreAction.ActionItem
import dev.patrickgold.florisboard.customization.model.response.moreAction.MoreData
import dev.patrickgold.florisboard.customization.model.response.moreAction.MoreData.MoreActionData.Companion.fromType
import dev.patrickgold.florisboard.databinding.ItemActionMoreBinding

class MoreActionHolder(binding: ItemActionMoreBinding, val listener: OnItemClickListener?) : BaseRecyclerViewHolder<ItemActionMoreBinding>(binding), OnItemClickListener {

  private var adapterMoreAction: SharedAdapter<ActionItem>? = null

  override fun bindTo(position: Int, item: BaseRecyclerItem?) {
    val data = (item as? MoreData) ?: return
    binding.txtTitle.text = data.title
    fromType(data.type)?.icon?.let { binding.iconImage.setImageResource(it) }
//    if (this.adapterMoreAction == null) {
//      this.adapterMoreAction = SharedAdapter(data.items ?: arrayListOf(), this)
//      binding.rvMoreItem.adapter = this.adapterMoreAction
//    } else this.adapterMoreAction?.notifyNewList(data.items ?: arrayListOf())
    NanoChipClass(binding.root.context, binding.rvMoreItem, data.items ?: arrayListOf(), this)
  }


  override fun onItemClick(pos: Int, item: BaseRecyclerItem, actionType: Int) {
    listener?.onItemClick(pos, item, actionType)
  }
}