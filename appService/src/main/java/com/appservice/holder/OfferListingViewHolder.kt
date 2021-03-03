package com.appservice.holder

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemOfferBinding
import com.appservice.offers.models.OfferModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.invisible
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import java.text.NumberFormat
import java.util.*


class OfferListingViewHolder(binding: RecyclerItemOfferBinding) : AppBaseRecyclerViewHolder<RecyclerItemOfferBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = (item as? OfferModel) ?: return
        apply { activity?.glideLoad(binding.rivOfferImage, data.featuredImage?.tileImage, R.drawable.placeholder_image_n) }
        binding.ctvOffersDescription.text = data.description
        binding.ctvOffersHeading.text = data.name
        binding.ctvOffersPricing.text = "₹ ${NumberFormat.getNumberInstance(Locale.US).format(data.discountAmount)} OFF"
       if (data.category.isNullOrEmpty().not()){ binding.ctvOffersCategory.text = "on ${data.category?:""}"}
        binding.root.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.OFFER_ITEM_CLICK.ordinal) }
        binding.shareData.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.OFFER_DATA_SHARE_CLICK.ordinal) }
        binding.shareWhatsapp.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.OFFER_WHATS_APP_SHARE.ordinal) }
        if (data.isAvailable == false) {
            with(binding) {
                cvRoot.setCardBackgroundColor(getApplicationContext()?.resources?.getColor(R.color.grey_f9f9f9)!!)
                rivOfferImage.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })
                cvStatus.setTextColor(getApplicationContext()?.resources?.getColor(R.color.grey_light_3)!!)
                cvStatus.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getApplicationContext()!!, R.drawable.ic_offer_inactive), null, null, null)
                ctvOffersHeading.setTextColor(getApplicationContext()?.resources?.getColor(R.color.gray_828282)!!)
                cvStatus.text = getApplicationContext()?.getString(R.string.inactive)
                shareData.invisible()
                shareWhatsapp.invisible()
            }


        } else {
            with(binding) {
                cvRoot.setCardBackgroundColor(getApplicationContext()?.resources?.getColor(R.color.white)!!)
                shareData.visible()
                ctvOffersHeading.setTextColor(getApplicationContext()?.resources?.getColor(R.color.yellow_ffb900)!!)
                cvStatus.text = getApplicationContext()?.getString(R.string.active)
                shareWhatsapp.visible()
                cvStatus.setTextColor(getApplicationContext()?.resources?.getColor(R.color.green_27AE60)!!)
                cvStatus.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getApplicationContext()!!, R.drawable.ic_dot_green), null, null, null)
                rivOfferImage.clearColorFilter()


            }
        }
    }
}