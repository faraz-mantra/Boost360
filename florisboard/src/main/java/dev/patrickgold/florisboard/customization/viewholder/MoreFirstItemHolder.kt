package dev.patrickgold.florisboard.customization.viewholder

import com.framework.extensions.gone
import com.framework.extensions.visible
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.moreAction.ActionItem
import dev.patrickgold.florisboard.customization.model.response.moreAction.ActionItem.ActionData.Companion.fromType
import dev.patrickgold.florisboard.customization.util.RecyclerViewActionType
import dev.patrickgold.florisboard.databinding.ItemMoreFirstBinding

class MoreFirstItemHolder(binding: ItemMoreFirstBinding, val listener: OnItemClickListener?) : BaseRecyclerViewHolder<ItemMoreFirstBinding>(binding) {

  override fun bindTo(position: Int, item: BaseRecyclerItem?) {
    val data = item as? ActionItem ?: return
    binding.txtTitle.text = data.title
    val type = fromType(data.type)
    if (type?.icon != null) {
      binding.iconImage.visible()
      binding.iconImage.setImageResource(type.icon!!)
    } else binding.iconImage.gone()
    type?.arrowIcon?.let { binding.iconArrow.setImageResource(it) }
    binding.root.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.MORE_ACTION_CLICK.ordinal) }
  }
}