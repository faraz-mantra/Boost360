package com.appservice.ui.testimonial.newflow.holder


import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemTestimonialListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.testimonial.newflow.model.DataItem
import com.framework.glide.util.glideLoad

class TestimonialListingHolder(binding: RecyclerItemTestimonialListingBinding) :
  AppBaseRecyclerViewHolder<RecyclerItemTestimonialListingBinding>(binding) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? DataItem) ?: return
    binding.ctvCustomerName.text = data.reviewerName
    binding.ctvCustomerDescription.text = data.testimonialBody
    binding.ctvCustomerDesignation.text = data.testimonialTitle
    binding.ctvCustomerReview.text = data.message


    apply {
      activity?.glideLoad(
        binding.civTestimonialImage,
        data.profileImage?.tileImage,
        R.drawable.placeholder_image_n
      )
    }
    binding.root.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        RecyclerViewActionType.TESTIMONIAL_ITEM_CLICK.ordinal
      )
    }
    binding.rivDeleteTestimonial.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        RecyclerViewActionType.DELETE_TESTIMONIAL.ordinal
      )
    }
    binding.rivEditTestimonial.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        RecyclerViewActionType.EDIT_TESTIMONIAL.ordinal
      )
    }
  }

}