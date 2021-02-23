package com.appservice.holder

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Path
import android.view.View
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemOfferBinding
import com.appservice.offers.models.OfferModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.enums.DrawableDirection
import com.framework.enums.setDrawable
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
        binding.ctvOffersCategory.text = "on ${data.category}"
        binding.root.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.OFFER_ITEM_CLICK.ordinal) }
        binding.shareData.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.OFFER_DATA_SHARE_CLICK.ordinal) }
        binding.shareWhatsapp.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.OFFER_WHATS_APP_SHARE.ordinal) }
        if (data.isAvailable == false) {
            binding.cvRoot.setCardBackgroundColor(getApplicationContext()?.resources?.getColor(R.color.grey_f9f9f9)!!)
            binding.rivOfferImage.colorFilter= ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f)})
            binding.cvStatus.setTextColor(getApplicationContext()?.resources?.getColor(R.color.grey_light_3)!!)
            binding.cvStatus.setDrawable(getResources()?.getDrawable(R.drawable.ic_dot_gray),DrawableDirection.LEFT)
            binding.shareData.invisible()
            binding.shareWhatsapp.invisible()

        } else {
            binding.cvRoot.setCardBackgroundColor(getApplicationContext()?.resources?.getColor(R.color.white)!!)
            binding.shareData.visible()
            binding.shareWhatsapp.visible()
            binding.cvStatus.setTextColor(getApplicationContext()?.resources?.getColor(R.color.green_27AE60)!!)
            binding.cvStatus.setDrawable(getResources()?.getDrawable(R.drawable.ic_dot_green),DrawableDirection.LEFT)
            binding.rivOfferImage.clearColorFilter()
        }
    }
}