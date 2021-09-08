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
        binding.ctvCatalogSetupTitle.text = tilesItem.title
        when (catalogSetup?.isPending) {true -> { binding.civSetupCheck.gone();binding.ctvPending.visible()} else -> { binding.civSetupCheck.visible();binding.ctvPending.gone() } }
        binding.ctvCatalogSetupSubheading.text = catalogSetup?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = catalogSetup?.getSubtitle()
        val icon = tilesItem.icon?.let { IconType.fromName(name = it) }
        tilesItem.icon.let { binding.civSetupIcon.setImageResource(icon?.icon!!) }
      }
      is PaymentCollectionSetup -> {
        val paymentCollectionSetup = tilesItem.tile as? PaymentCollectionSetup
        val icon = tilesItem.icon?.let { IconType.fromName(name = it) }
        when (paymentCollectionSetup?.isPending) {true -> { binding.civSetupCheck.gone();binding.ctvPending.visible()} else -> { binding.civSetupCheck.visible();binding.ctvPending.gone() } }
        tilesItem.icon.let { binding.civSetupIcon.setImageResource(icon?.icon!!) }
        binding.ctvCatalogSetupTitle.text = tilesItem.title
        binding.ctvCatalogSetupSubheading.text = paymentCollectionSetup?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = paymentCollectionSetup?.getSubtitle()

      }
      is CustomerInvoicesSetup -> {
        val customerInvoicesSetup = tilesItem.tile as? CustomerInvoicesSetup
        val icon = tilesItem.icon?.let { IconType.fromName(name = it) }
        tilesItem.icon.let { binding.civSetupIcon.setImageResource(icon?.icon!!) }
        binding.ctvCatalogSetupTitle.text = tilesItem.title
        binding.ctvCatalogSetupSubheading.text = customerInvoicesSetup?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = customerInvoicesSetup?.getSubtitle()
        when (customerInvoicesSetup?.isTaxInvoiceSetupComplete) {true -> { binding.civSetupCheck.gone();binding.ctvPending.visible()} else -> { binding.civSetupCheck.visible();binding.ctvPending.gone() } }


      }
      is ConsultationSetup -> {
        val consultationSetup = tilesItem.tile as? ConsultationSetup
        val icon = tilesItem?.icon?.let { IconType.fromName(name = it) }
        tilesItem?.icon.let { binding?.civSetupIcon.setImageResource(icon?.icon!!) }
        binding.ctvCatalogSetupTitle.text = tilesItem?.title


      }
      is PoliciesSetup -> {
        val policiesSetup = tilesItem.tile as? PoliciesSetup
        val icon = tilesItem?.icon?.let { IconType.fromName(name = it) }
        tilesItem?.icon.let { binding?.civSetupIcon.setImageResource(icon?.icon!!) }
        binding?.ctvCatalogSetupSubheading.text = tilesItem?.description
        binding.ctvCatalogSetupTitle.text = tilesItem?.title

      }
    }
//    binding.civCatalogSetupIcon.setImageIcon(tilesItem)

    binding?.catalogSetup.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.ON_CLICK_CATALOG_ITEM.ordinal) }

  }
}