package com.appservice.holder

import com.appservice.appointment.model.*
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemEcomAptSettingsBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.invisible
import com.framework.extensions.visible

class CatalogTileViewHolder(binding: RecyclerItemEcomAptSettingsBinding) : AppBaseRecyclerViewHolder<RecyclerItemEcomAptSettingsBinding>(
  binding = binding
) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val tilesItem = item as? AppointmentStatusResponse.TilesModel
    when (tilesItem?.tile) {
      is CatalogSetup -> {
        val catalogSetup = tilesItem.tile as? CatalogSetup
        when (catalogSetup?.isPending) {true -> { binding.civSetupCheck.gone();binding.ctvPending.visible()} else -> { binding.civSetupCheck.visible();binding.ctvPending.invisible() } }
        binding.ctvCatalogSetupSubheading.text = catalogSetup?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = catalogSetup?.getSubtitle()
        binding?.ctvCatalogSetupSubheading2.visible()

      }
      is PaymentCollectionSetup -> {
        val paymentCollectionSetup = tilesItem.tile as? PaymentCollectionSetup
        when (paymentCollectionSetup?.isPending) {true -> { binding.civSetupCheck.gone();binding.ctvPending.visible()} else -> { binding.civSetupCheck.visible();binding.ctvPending.invisible() } }
        binding.ctvCatalogSetupSubheading.text = paymentCollectionSetup?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = paymentCollectionSetup?.getSubtitle()
        binding?.ctvCatalogSetupSubheading2.visible()


      }
      is CustomerInvoicesSetup -> {
        val customerInvoicesSetup = tilesItem.tile as? CustomerInvoicesSetup
        binding.ctvCatalogSetupSubheading.text = customerInvoicesSetup?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = customerInvoicesSetup?.getSubtitle()
        when (customerInvoicesSetup?.isTaxInvoiceSetupComplete==false) {true -> { binding.civSetupCheck.gone();binding.ctvPending.visible()} else -> { binding.civSetupCheck.visible();binding.ctvPending.invisible() } }
        binding?.ctvCatalogSetupSubheading2.visible()


      }
      is ConsultationSetup -> {
        val consultationSetup = tilesItem.tile as? ConsultationSetup


      }
      is PoliciesSetup -> {
        val policiesSetup = tilesItem.tile as? PoliciesSetup

      }
      is DeliverySetupTile -> {
        val deliverySetupTile = tilesItem.tile as? DeliverySetupTile
        binding?.ctvCatalogSetupSubheading.text = deliverySetupTile?.getTitle()
        binding?.ctvCatalogSetupSubheading2.gone()
        when (deliverySetupTile?.isPending==true) {true -> { binding.civSetupCheck.gone();binding.ctvPending.visible()} else -> { binding.civSetupCheck.visible();binding.ctvPending.invisible() } }

      }
    }
//    binding.civCatalogSetupIcon.setImageIcon(tilesItem)
    val icon = tilesItem?.icon?.let { IconType.fromName(name = it) }
    binding.ctvCatalogSetupTitle.text = tilesItem?.title
    tilesItem?.icon.let { binding.civSetupIcon.setImageResource(icon?.icon!!) }
    binding?.catalogSetup.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.ON_CLICK_CATALOG_ITEM.ordinal) }

  }
}