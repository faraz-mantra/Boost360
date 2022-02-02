package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import com.appservice.utils.capitalizeUtil
import com.bumptech.glide.Glide
import com.framework.utils.DateUtils.KEYBOARD_DISPLAY_DATE
import com.framework.utils.DateUtils.getDate
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.FloatUpdate
import dev.patrickgold.florisboard.customization.util.RecyclerViewActionType
import dev.patrickgold.florisboard.databinding.AdapterItemUpdateBinding

class FloatUpdateViewHolder(binding: AdapterItemUpdateBinding, val listener: OnItemClickListener?) : BaseRecyclerViewHolder<AdapterItemUpdateBinding>(binding) {

  override fun bindTo(position: Int, item: BaseRecyclerItem?) {
    val float = item as? FloatUpdate ?: return
    if (float.imageUri?.isNotEmpty() == true) {
      binding.cardThumbnail.visibility = View.VISIBLE
      Glide.with(binding.imageView).load(float.imageUri).placeholder(R.drawable.placeholder_image_n).into(binding.imageView)
    } else {
      binding.cardThumbnail.visibility = View.GONE
    }
    binding.tvDescription.textColor = ContextCompat.getColor(binding.root.context, R.color.black_4a4a4a)
    binding.tvDescription.setTextSize(38F)
    binding.tvDescription.text = float.getMessageLength()
    val timeString = (float.createdOn?.subSequence(float.createdOn?.indexOf("(")!! + 1, float.createdOn?.length!! - 2) as String).toLong()
    binding.ctvDateTime.text = getDate(timeString, KEYBOARD_DISPLAY_DATE)
    binding.btnShare.setOnClickListener { listener?.onItemClick(position, float, RecyclerViewActionType.FLOAT_UPDATE_CLICK.ordinal) }
  }
}

