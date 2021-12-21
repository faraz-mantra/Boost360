package dev.patrickgold.florisboard.customization.viewholder

import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.moreAction.ActionItem
import dev.patrickgold.florisboard.customization.util.RecyclerViewActionType
import dev.patrickgold.florisboard.databinding.ItemMoreSecondBinding

class MoreSecondItemHolder(binding: ItemMoreSecondBinding, val listener: OnItemClickListener?) : BaseRecyclerViewHolder<ItemMoreSecondBinding>(binding) {

  override fun bindTo(position: Int, item: BaseRecyclerItem?) {
    val data = item as? ActionItem ?: return
    binding.txtTitle.text = data.title
    val type = ActionItem.ActionData.fromType(data.type)
    type?.icon?.let { binding.iconEye.setImageResource(it) }
    type?.arrowIcon?.let { binding.iconShare.setImageResource(it) }
    binding.iconEye.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.EYE_MORE_ITEM_CLICK.ordinal) }
    binding.iconShare.setOnClickListener { listener?.onItemClick(position, item,RecyclerViewActionType.SHARE_MORE_ITEM_CLICK.ordinal) }
  }
}