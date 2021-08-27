package com.appservice.holder

import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemEcomAptSettingsBinding
import com.appservice.model.catalog.IconType
import com.appservice.model.catalog.TilesItem
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem

class CatalogTileViewHolder(binding: RecyclerItemEcomAptSettingsBinding) : AppBaseRecyclerViewHolder<RecyclerItemEcomAptSettingsBinding>(
  binding = binding
) {
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val tilesItem = item as? TilesItem
//    binding.civCatalogSetupIcon.setImageIcon(tilesItem)
    val icon = tilesItem?.icon?.let { IconType.fromName(name = it) }
    tilesItem?.icon.let { binding?.civCatalogSetupIcon.setImageResource(icon?.icon!!) }
    binding?.ctvCatalogSetupSubheading.text = tilesItem?.description
    binding.ctvCatalogSetupTitle.text = tilesItem?.title
    binding?.catalogSetup.setOnClickListener { listener?.onItemClick(position,item,RecyclerViewActionType.ON_CLICK_CATALOG_ITEM.ordinal) }

  }
}