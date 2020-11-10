package com.dashboard.holder

import android.view.View
import androidx.core.content.ContextCompat
import com.appservice.utils.INITIAL_POSITION
import com.appservice.utils.ROTATED_POSITION
import com.appservice.utils.rotateImage
import com.dashboard.R
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

    if (data.isLastSeen.not()) {
      activity?.let { binding.imgSymbol.setTintColor(ContextCompat.getColor(it, R.color.greyish_brown)) }
      setExpendStatusView(data)
      binding.imgArrow.rotation = if (data.isExpend) ROTATED_POSITION else INITIAL_POSITION
      binding.contentParent.setOnClickListener {
        data.isExpend = data.isExpend.not()
        binding.imgArrow.rotateImage(data.isExpend)
        setExpendStatusView(data)
      }
    } else setLastSeenView(data)

    if (data.manageBusinessList.isNullOrEmpty().not()) {
      activity?.let {
        binding.rvManageBusiness.apply {
          visible()
          val adapter1 = AppBaseRecyclerViewAdapter(it, data.manageBusinessList!!, this@BoostAddOnsViewHolder)
          adapter = adapter1
        }
      }
    } else binding.rvManageBusiness.gone()

    binding.executePendingBindings()
  }

  private fun setExpendStatusView(data: AllBoostAddOnsData) {
    activity?.let { binding.imgArrow.setTintColor(ContextCompat.getColor(it, if (data.isExpend) R.color.grey_dim_2 else R.color.colorAccent)) }
    activity?.let { binding.txtTitle.setTextColor(ContextCompat.getColor(it, if (data.isExpend) R.color.greyish_brown else R.color.colorAccent)) }
    activity?.let { binding.contentParent.setCardBackgroundColor(ContextCompat.getColorStateList(it, if (data.isExpend) R.color.white_smoke else R.color.white)) }
    binding.contentParent.cardElevation = if (data.isExpend) 0F else 2F
    binding.rvManageBusiness.visibility = if (data.isExpend) View.VISIBLE else View.GONE
  }

  private fun setLastSeenView(data: AllBoostAddOnsData) {
    binding.imgArrow.visibility = View.INVISIBLE
    binding.mainContent.background = activity?.let { ContextCompat.getDrawable(it, R.color.white) }
    activity?.let { binding.contentParent.setCardBackgroundColor(ContextCompat.getColorStateList(it, R.color.white)) }
    binding.contentParent.cardElevation = 0F
    activity?.let { binding.imgSymbol.setTintColor(ContextCompat.getColor(it, R.color.colorAccent)) }
    activity?.let { binding.txtTitle.setTextColor(ContextCompat.getColor(it, R.color.colorAccent)) }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }
}
