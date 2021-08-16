package com.dashboard.holder

import android.view.View
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemDrawerViewBinding
import com.dashboard.model.live.drawerData.DrawerHomeData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class HomeDrawerViewHolder(binding: ItemDrawerViewBinding) :
  AppBaseRecyclerViewHolder<ItemDrawerViewBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? DrawerHomeData ?: return
    binding.textViewName.text = data.title
    val navType = DrawerHomeData.NavType.from(data.navType)
    navType?.icon?.let { binding.imageViewIcon.setImageResource(it) }
    binding.txtViewNew.visibility = if (data.isNewBtnShow) View.VISIBLE else View.INVISIBLE
    binding.txtViewNew.text = if (data.isNewBtnShow) data.newBtnText else "NEW"
    binding.viewLock.visibility = if (data.isLockShow) View.VISIBLE else View.INVISIBLE
    binding.viewUp.visibility = if (data.isUpLineShow) View.VISIBLE else View.INVISIBLE
    binding.viewDown.visibility = if (data.isBottomLineShow) View.VISIBLE else View.INVISIBLE
    binding.mainContent.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        RecyclerViewActionType.NAV_CLICK_ITEM_CLICK.ordinal
      )
    }
  }
}
