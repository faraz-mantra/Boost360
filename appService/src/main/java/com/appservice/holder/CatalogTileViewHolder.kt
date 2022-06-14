package com.appservice.holder

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
    when (tilesItem?.tile) {
      is CatalogSetup -> {
        val catalogSetup = tilesItem.tile as? CatalogSetup
        binding.ctvCatalogSetupSubheading.text = catalogSetup?.getTitle(activity)
        binding.ctvCatalogSetupSubheading2.text = catalogSetup?.getSubtitle()
        binding.ctvCatalogSetupSubheading2.visible()
        checkAndHideStatusBseOnFlag(catalogSetup?.isEmptyData(), catalogSetup?.isPending)
      }
      is PaymentCollectionSetup -> {
        val paymentCollectionSetup = tilesItem.tile as? PaymentCollectionSetup
        binding.ctvCatalogSetupSubheading.text = paymentCollectionSetup?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = paymentCollectionSetup?.getSubtitle()
        binding.ctvCatalogSetupSubheading2.visible()
        checkAndHideStatusBseOnFlag(paymentCollectionSetup?.isEmptyData(), paymentCollectionSetup?.isPending)
      }
      is CustomerInvoicesSetup -> {
        val customerInvoicesSetup = tilesItem.tile as? CustomerInvoicesSetup
        binding.ctvCatalogSetupSubheading.text = customerInvoicesSetup?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = customerInvoicesSetup?.getSubtitle()
        binding.ctvCatalogSetupSubheading2.visible()
        checkAndHideStatusBseOnFlag(customerInvoicesSetup?.isEmptyData(), customerInvoicesSetup?.isPending)
      }
      is ConsultationSetup -> {
        val consultationSetup = tilesItem.tile as? ConsultationSetup
        binding.ctvCatalogSetupSubheading.text = fromHtml(tilesItem.description?.replace("#", consultationSetup?.getIsEnableText() ?: ""))
        binding.ctvCatalogSetupSubheading2.gone()
        checkAndHideStatusBseOnFlag(false, consultationSetup?.isPending)
      }
      is PoliciesSetup -> {
        val policiesSetup = tilesItem.tile as? PoliciesSetup
      }
      is DeliverySetupTile -> {
        val deliverySetupTile = tilesItem.tile as? DeliverySetupTile
        binding.ctvCatalogSetupSubheading.text = deliverySetupTile?.getTitle()
        binding.ctvCatalogSetupSubheading2.gone()
        checkAndHideStatusBseOnFlag(false, deliverySetupTile?.isPending)
      }
    }
    val icon = tilesItem?.icon?.let { IconType.fromName(name = it) }
    binding.ctvCatalogSetupTitle.text = tilesItem?.title
    tilesItem?.icon.let { binding.civSetupIcon.setImageResource(icon?.icon!!) }
    binding.civSetupIcon.setTintColor(ContextCompat.getColor(binding.root.context, if (binding.ctvPending.isVisible) R.color.grey_BBBBBB else R.color.colorAccent))
    binding.imgArrowRight.isVisible = binding.ctvPending.isVisible
    binding.btnEdit.isVisible = binding.ctvPending.isVisible.not()
    binding.catalogSetup.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.ON_CLICK_CATALOG_ITEM.ordinal) }
  }

  private fun checkAndHideStatusBseOnFlag(isEmpty: Boolean?, isPending: Boolean?) {
    when {
      isEmpty == true -> isHideShowStatus(isCheck = false, isVerPending = false, isPending = true)
      isPending == true -> isHideShowStatus(isCheck = false, isVerPending = true, isPending = false)
      else -> isHideShowStatus(isCheck = true, isVerPending = false, isPending = false)
    }
  }

  private fun isHideShowStatus(isCheck: Boolean, isVerPending: Boolean, isPending: Boolean) {
    binding.civSetupCheck.isVisible = isCheck
    binding.ctvVerificationPending.isVisible = isVerPending
    binding.ctvPending.isVisible = isPending
  }
}