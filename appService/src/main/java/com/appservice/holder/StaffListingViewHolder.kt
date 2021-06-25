package com.appservice.holder

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.View
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemStaffListingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.model.staffModel.DataItem
import com.framework.glide.util.glideLoad

class StaffListingViewHolder(binding: RecyclerItemStaffListingBinding) : AppBaseRecyclerViewHolder<RecyclerItemStaffListingBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val data = item as DataItem
        binding.ctvName.text = "${data.name}"
        binding.civImage.let { activity?.glideLoad(it, data.image.toString(), R.drawable.placeholder_image_n) }
        val specialisationsItem: ArrayList<String> = ArrayList()
        data.specialisations?.forEach { specialisationsItem.add(it.value!!) }
        binding.ctvSpecialization.text = specialisationsItem.joinToString(separator = ",")
        binding.btnViewProfile.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.STAFF_LISTING_CLICK.ordinal) }

        if (item.isAvailable == false) {
            binding.linearRoot.setBackgroundColor(getApplicationContext()?.resources?.getColor(R.color.grey_f9f9f9)!!)
            binding.civImage.colorFilter= ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f)})
            binding.btnViewProfile.background = ContextCompat.getDrawable(getApplicationContext()!!, R.drawable.rounded_stroke_grey_4)
            binding.btnViewProfile.setTextColor(getApplicationContext()?.resources?.getColor(R.color.grey_light_3)!!)
            binding.cardOverlay.visibility = View.VISIBLE
        } else {
            binding.linearRoot.setBackgroundColor(getApplicationContext()?.resources?.getColor(R.color.white)!!)
            binding.btnViewProfile.background = ContextCompat.getDrawable(getApplicationContext()!!, R.drawable.rounded_stroke_orange_4)
            binding.btnViewProfile.setTextColor(getApplicationContext()?.resources?.getColor(R.color.orange)!!)
            binding.cardOverlay.visibility = View.GONE
            binding.civImage.clearColorFilter()
        }
    }

}
