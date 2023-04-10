package com.dashboard.holder

import android.view.View
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.dashboard.READY_MADE_UPDATES
import com.dashboard.databinding.ItemQuickActionBinding
import com.dashboard.model.live.quickAction.QuickActionItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.utils.addButtonClickActionTypeByUser
import com.dashboard.utils.checkIfButtonClickedByUserInPast

class QuickActionViewHolder(binding: ItemQuickActionBinding) : AppBaseRecyclerViewHolder<ItemQuickActionBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? QuickActionItem ?: return
    binding.txtTitle.text = data.title

    // Show/Hide of New Tag on Quick Actions, will disappear after one click
    binding.isNew.visibility = if (data.isNew && checkIfButtonClickedByUserInPast(data.quickActionType).not()) View.VISIBLE else View.INVISIBLE

    val type = QuickActionItem.QuickActionType.from(data.quickActionType)
    type?.icon?.let {
      if (data.isLotty && type.lotty != null) {
        LottieDrawable().apply {
          LottieCompositionFactory.fromRawRes(activity!!, type.lotty!!).addListener { result -> composition = result }
          binding.imgIcon.setImageDrawable(this)
          scale = 2F
          repeatCount = LottieDrawable.INFINITE
          playAnimation()
        }
      } else binding.imgIcon.setImageResource(it)
    }
    binding.mainContent.setOnClickListener {
      //start Show/Hide of New Tag on Quick Actions, will disappear after one click
      if ((data.quickActionType == READY_MADE_UPDATES).not()) {
        addButtonClickActionTypeByUser(data.quickActionType)
      }
      // end
      listener?.onItemClick(position, data, RecyclerViewActionType.QUICK_ACTION_ITEM_CLICK.ordinal)
    }
  }
}

