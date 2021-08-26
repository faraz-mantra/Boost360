package dev.patrickgold.florisboard.customization.viewholder

import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.framework.glide.util.glideLoad
import com.framework.utils.setNoDoubleClickListener
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.staff.DataItem
import dev.patrickgold.florisboard.databinding.ItemStaffProfileBinding
import java.util.*
import kotlin.collections.ArrayList

class StaffProfileViewHolder(binding: ItemStaffProfileBinding, val listener: OnItemClickListener?) : BaseRecyclerViewHolder<ItemStaffProfileBinding>(binding) {

  override fun bindTo(position: Int, item: BaseRecyclerItem?) {
    val data = item as? DataItem ?: return
    binding.imageIcon.let { binding.root.context.glideLoad(it, data.image ?: "", R.drawable.placeholder_image_n) }
    binding.titleName.apply {
      text = data.name?.capitalize(Locale.ROOT)
      paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }
    binding.titleType.text = data.specialData()
    if (item.isAvailable == true) {
      binding.btnShare.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.blue_accent_10))
      binding.btnShare.setTextColor(ContextCompat.getColor(binding.root.context, R.color.accent_blue))
      binding.imageIcon.removeGreyscale()
      binding.btnShare.setNoDoubleClickListener({ listener?.onItemClick(position, data) })
    } else {
      binding.btnShare.setBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.bg_grey_light))
      binding.btnShare.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gray_light_4))
      binding.imageIcon.makeGreyscale()
    }
  }
}