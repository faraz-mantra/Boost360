package com.appservice.holder

import android.view.View
import com.appservice.AppServiceApplication
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ItemTestimonialListBinding
import com.appservice.model.testimonial.response.TestimonialData
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.glide.util.glideLoad
import com.framework.pref.UserSessionManager

class TestimonialViewHolder(binding: ItemTestimonialListBinding) : AppBaseRecyclerViewHolder<ItemTestimonialListBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    val data = item as? TestimonialData ?: return
    binding.titleName.text = data.getTitleName()
    binding.titleDec.text = data.getTitleDesc()
    val testimonialTitle = data.getTestimonialName()
    binding.testimonialTitle.visibility = if (testimonialTitle.isEmpty()) View.GONE else View.VISIBLE
    binding.testimonialTitle.text = testimonialTitle
    binding.testimonialDesc.text = data.getTestimonialDesc()
    activity?.glideLoad(binding.imageIcon, data.getImageUrl(), R.drawable.placeholder_image_n)
    binding.editImg.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.EDIT_TESTIMONIAL_CLICK.ordinal) }
    binding.root.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.EDIT_TESTIMONIAL_CLICK.ordinal) }
  }
}
