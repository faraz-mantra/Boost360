package com.appservice.holder

import android.view.View
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemEcomAptSettingsBinding
import com.appservice.model.aptsetting.*
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.invisible
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
        when (catalogSetup?.isPending) {
          true -> {
            binding.civSetupCheck.gone();binding.ctvPending.visible()
          }
          else -> {
            binding.civSetupCheck.visible();binding.ctvPending.invisible()
          }
        }
      }
      is PaymentCollectionSetup -> {
        val paymentCollectionSetup = tilesItem.tile as? PaymentCollectionSetup
        binding.ctvCatalogSetupSubheading.text = paymentCollectionSetup?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = paymentCollectionSetup?.getSubtitle()
        binding.ctvCatalogSetupSubheading2.visible()
        when (paymentCollectionSetup?.isPending) {
          true -> {
            binding.civSetupCheck.gone();binding.ctvPending.visible()
          }
          else -> {
            binding.civSetupCheck.visible();binding.ctvPending.invisible()
          }
        }

      }
      is CustomerInvoicesSetup -> {
        val customerInvoicesSetup = tilesItem.tile as? CustomerInvoicesSetup
        binding.ctvCatalogSetupSubheading.text = customerInvoicesSetup?.getTitle()
        binding.ctvCatalogSetupSubheading2.text = customerInvoicesSetup?.getSubtitle()
        binding.ctvCatalogSetupSubheading2.visible()
        when (customerInvoicesSetup?.isTaxInvoiceSetupComplete == false) {
          true -> {
            binding.civSetupCheck.gone();binding.ctvPending.visible()
          }
          else -> {
            binding.civSetupCheck.visible();binding.ctvPending.invisible()
          }
        }
      }
      is ConsultationSetup -> {
        val consultationSetup = tilesItem.tile as? ConsultationSetup
        binding.ctvCatalogSetupSubheading.text = fromHtml(tilesItem.description?.replace("#", consultationSetup?.getIsEnableText() ?: ""))
        binding.ctvCatalogSetupSubheading2.gone()
        when (consultationSetup?.isGeneralAppointmentEnabled == false) {
          true -> {
            binding.civSetupCheck.gone();binding.ctvPending.visible()
          }
          else -> {
            binding.civSetupCheck.visible();binding.ctvPending.invisible()
          }
        }
      }
      is PoliciesSetup -> {
        val policiesSetup = tilesItem.tile as? PoliciesSetup
      }
      is DeliverySetupTile -> {
        val deliverySetupTile = tilesItem.tile as? DeliverySetupTile
        binding.ctvCatalogSetupSubheading.text = deliverySetupTile?.getTitle()
        binding.ctvCatalogSetupSubheading2.gone()
        when (deliverySetupTile?.isPending == true) {
          true -> {
            binding.civSetupCheck.gone();binding.ctvPending.visible()
          }
          else -> {
            binding.civSetupCheck.visible();binding.ctvPending.invisible()
          }
        }
      }
    }
    val icon = tilesItem?.icon?.let { IconType.fromName(name = it) }
    binding.ctvCatalogSetupTitle.text = tilesItem?.title
    tilesItem?.icon.let { binding.civSetupIcon.setImageResource(icon?.icon!!) }
    val colorTinIcon = if (binding.civSetupCheck.visibility == View.VISIBLE) R.color.colorAccent else R.color.grey_BBBBBB
    activity?.let { ContextCompat.getColor(it, colorTinIcon) }?.let { binding.civSetupIcon.setTintColor(it) }
    binding.catalogSetup.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.ON_CLICK_CATALOG_ITEM.ordinal) }
  }
}