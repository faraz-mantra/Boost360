package com.appservice.holder

import com.appservice.appointment.model.*
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemEcomAptSettingsBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
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
        binding?.ctvCatalogSetupTitle.text = catalogSetup?.getTitle()
        binding?.ctvCatalogSetupSubheading.text = catalogSetup?.getSubtitle()
        binding.ctvCatalogSetupSubheading2.invisible()
      }
      is PaymentCollectionSetup -> {
        val paymentCollectionSetup = tilesItem.tile as? PaymentCollectionSetup
        binding.ctvCatalogSetupSubheading2.visible()

      }
      is CustomerInvoicesSetup -> {
        val customerInvoicesSetup = tilesItem.tile as? CustomerInvoicesSetup

      }
      is ConsultationSetup -> {
        val consultationSetup = tilesItem.tile as? ConsultationSetup

      }
      is PoliciesSetup -> {
        val policiesSetup = tilesItem.tile as? PoliciesSetup

      }
    }
//    binding.civCatalogSetupIcon.setImageIcon(tilesItem)
    val icon = tilesItem?.icon?.let { IconType.fromName(name = it) }
    tilesItem?.icon.let { binding?.civSetupIcon.setImageResource(icon?.icon!!) }
    binding?.ctvCatalogSetupSubheading.text = tilesItem?.description
    binding.ctvCatalogSetupTitle.text = tilesItem?.title
    binding?.catalogSetup.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.ON_CLICK_CATALOG_ITEM.ordinal) }

  }
}