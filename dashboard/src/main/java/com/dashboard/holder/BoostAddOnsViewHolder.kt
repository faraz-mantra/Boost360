package com.dashboard.holder

import android.view.View
import com.appservice.utils.INITIAL_POSITION
import com.appservice.utils.ROTATED_POSITION
import com.appservice.utils.rotateImage
import com.dashboard.databinding.ItemBoostAddOnsBinding
import com.dashboard.model.AllBoostAddOnsData
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.framework.extensions.gone
import com.framework.extensions.visible

class BoostAddOnsViewHolder(binding: ItemBoostAddOnsBinding) : AppBaseRecyclerViewHolder<ItemBoostAddOnsBinding>(binding), RecyclerItemClickListener {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? AllBoostAddOnsData ?: return
    binding.txtTitle.text = data.title
    binding.txtSubTitle.text = data.subTitle
    if (data.isLastSeen) {
      binding.imgArrow.visibility = View.INVISIBLE
    } else {
      binding.imgArrow.rotation = if (data.isExpend) ROTATED_POSITION else INITIAL_POSITION
      binding.rvManageBusiness.visibility = if (data.isExpend) View.VISIBLE else View.GONE
      binding.contentMain.setOnClickListener {
        data.isExpend = data.isExpend.not()
        binding.imgArrow.rotateImage(data.isExpend)
        if (data.isExpend) binding.rvManageBusiness.visible() else binding.rvManageBusiness.gone()
      }
    }
    if (data.manageBusinessList.isNullOrEmpty().not()) {
      activity?.let {
        binding.rvManageBusiness.apply {
          val adapter1 = AppBaseRecyclerViewAdapter(it, data.manageBusinessList!!, this@BoostAddOnsViewHolder)
          adapter = adapter1
        }
      }
    }
    binding.executePendingBindings()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }
}
