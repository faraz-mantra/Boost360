package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import com.bumptech.glide.Glide
import com.framework.utils.DateUtils.KEYBOARD_DISPLAY_DATE
import com.framework.utils.DateUtils.getDate
import com.framework.views.customViews.CustomButton
import com.framework.views.customViews.CustomImageView
import com.framework.views.customViews.CustomTextView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.FloatUpdate
import dev.patrickgold.florisboard.databinding.AdapterItemUpdateBinding
import dev.patrickgold.florisboard.databinding.PaginationLoaderKeyboardBinding

class FloatUpdateViewHolder(binding: AdapterItemUpdateBinding, val listener: OnItemClickListener?) : BaseRecyclerViewHolder<AdapterItemUpdateBinding>(binding) {

  override fun bindTo(position: Int, item: BaseRecyclerItem?) {
    val float = item as? FloatUpdate ?: return
    if (float.imageUri?.isNotEmpty() == true) {
      binding.imageView.visibility = View.VISIBLE
      Glide.with(binding.imageView).load(float.imageUri).placeholder(R.drawable.placeholder_image_n).into(binding.imageView)
    } else {
      binding.imageView.visibility = View.GONE
    }
    binding.tvDescription.text = float.message
    val timeString = (float.createdOn?.subSequence(float.createdOn?.indexOf("(")!! + 1, float.createdOn?.length!! - 2) as String).toLong()
    binding.ctvDateTime.text = getDate(timeString, KEYBOARD_DISPLAY_DATE)
    binding.btnShare.setOnClickListener { listener?.onItemClick(position, float) }
  }
}

