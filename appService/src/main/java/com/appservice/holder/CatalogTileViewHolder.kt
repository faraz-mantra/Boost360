package com.appservice.holder

import android.content.res.ColorStateList
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemEcomAptSettingsBinding
import com.appservice.model.aptsetting.*
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.fromHtml

class CatalogTileViewHolder(binding: RecyclerItemEcomAptSettingsBinding) : AppBaseRecyclerViewHolder<RecyclerItemEcomAptSettingsBinding>(binding = binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val tilesItem = item as? AppointmentStatusResponse.TilesModel
    var bgCardColor: Int = R.color.white
    when (tilesItem?.tile) {
      is CatalogSetup -> {
        val catalogData = tilesItem.tile as? CatalogSetup
        binding.ctvCatalogSetupSubheading.text = catalogData?.getTitle(activity)
        binding.ctvCatalogSetupSubheading2.text = catalogData?.getSubtitle()
        binding.ctvCatalogSetupSubheading2.visible()
        checkAndHideStatusBseOnFlag(catalogData?.isEmptyData(), catalogData?.isPending)
      }
      is PaymentCollectionSetup -> {
        val paymentData = tilesItem.tile as? PaymentCollectionSetup
        binding.ctvCatalogSetupSubheading.text = paymentData?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = paymentData?.getSubtitle()
        binding.ctvCatalogSetupSubheading2.visible()
        checkAndHideStatusBseOnFlag(paymentData?.isEmptyData(), paymentData?.isPending,paymentData?.getPendingFailedIconUsingStatus())
        bgCardColor = paymentData?.getBgColorUsingStatus() ?: R.color.white
      }
      is CustomerInvoicesSetup -> {
        val invoiceData = tilesItem.tile as? CustomerInvoicesSetup
        binding.ctvCatalogSetupSubheading.text = invoiceData?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = invoiceData?.getSubtitle()
        binding.ctvCatalogSetupSubheading2.visible()
        checkAndHideStatusBseOnFlag(invoiceData?.isEmptyData(), invoiceData?.isPending,invoiceData?.getPendingFailedIconUsingStatus())
        bgCardColor = invoiceData?.getBgColorUsingStatus() ?: R.color.white
      }
      is ConsultationSetup -> {
        val consultData = tilesItem.tile as? ConsultationSetup
        binding.ctvCatalogSetupSubheading.text = fromHtml(tilesItem.description?.replace("#", consultData?.getIsEnableText() ?: ""))
        binding.ctvCatalogSetupSubheading2.gone()
        checkAndHideStatusBseOnFlag(false, consultData?.isPending)
      }
      is PoliciesSetup -> {
        val policiesSetup = tilesItem.tile as? PoliciesSetup
      }
      is DeliverySetupTile -> {
        val deliveryData = tilesItem.tile as? DeliverySetupTile
        binding.ctvCatalogSetupSubheading.text = deliveryData?.getTitle()
        binding.ctvCatalogSetupSubheading2.gone()
        checkAndHideStatusBseOnFlag(false, deliveryData?.isPending)
      }
    }
    binding.mainContent.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, bgCardColor)))
    val icon = tilesItem?.icon?.let { IconType.fromName(name = it) }
    binding.ctvCatalogSetupTitle.text = tilesItem?.title
    tilesItem?.icon.let { binding.civSetupIcon.setImageResource(icon?.icon!!) }
    binding.civSetupIcon.setTintColor(ContextCompat.getColor(binding.root.context, if (binding.ctvPending.isVisible) R.color.grey_BBBBBB else R.color.colorAccent))
    binding.imgArrowRight.isVisible = binding.ctvPending.isVisible
    binding.btnEdit.isVisible = binding.ctvPending.isVisible.not()
    binding.catalogSetup.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.ON_CLICK_CATALOG_ITEM.ordinal) }
  }

  private fun checkAndHideStatusBseOnFlag(isEmpty: Boolean?, isPending: Boolean?, @DrawableRes icon: Int? = R.drawable.ic_clock_filled) {
    when {
      isEmpty == true -> isHideShowStatus(isCheck = false, isVerPending = false, isPending = true,icon)
      isPending == true -> isHideShowStatus(isCheck = false, isVerPending = true, isPending = false,icon)
      else -> isHideShowStatus(isCheck = true, isVerPending = false, isPending = false,icon)
    }
  }

  private fun isHideShowStatus(isCheck: Boolean, isVerPending: Boolean, isPending: Boolean, @DrawableRes icon: Int?) {
    binding.civSetupCheck.isVisible = isCheck
    binding.ctvVerificationPending.isVisible = isVerPending
    binding.ctvPending.isVisible = isPending
    icon?.let { binding.iconPendingFailed.setImageResource(icon) }
  }
}