package com.boost.marketplace.holder

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.boost.marketplace.R
import com.boost.marketplace.constant.RecyclerViewActionType
import com.boost.marketplace.databinding.ItemBoostAddOnsBinding
import com.boost.marketplace.infra.api.models.live.addOns.AllBoostAddOnsData
import com.boost.marketplace.infra.utils.INITIAL_POSITION
import com.boost.marketplace.infra.utils.ROTATED_POSITION
import com.boost.marketplace.infra.utils.rotateImage
import com.boost.marketplace.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewHolder
import com.boost.marketplace.recyclerView.BaseRecyclerViewItem
import com.boost.marketplace.recyclerView.RecyclerItemClickListener
import com.framework.extensions.gone
import com.framework.extensions.visible

class BoostAddOnsViewHolder(binding: ItemBoostAddOnsBinding) :
  AppBaseRecyclerViewHolder<ItemBoostAddOnsBinding>(binding), RecyclerItemClickListener {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? AllBoostAddOnsData ?: return
    binding.txtTitle.text = data.title
    binding.txtSubTitle.text = data.subTitle

    if (data.isLastSeen.not()) {
      activity?.resources?.getDimensionPixelSize(R.dimen.size_74)
        ?.let { binding.contentParent.layoutParams.height = it }
      activity?.resources?.getDimensionPixelSize(R.dimen.size_10)
        ?.let { binding.view.layoutParams.height = it }
      activity?.let {
        binding.imgSymbol.setTintColor(
          ContextCompat.getColor(
            it,
            R.color.greyish_brown
          )
        )
      }
      binding.mainContent.background =
        activity?.let { ContextCompat.getDrawable(it, R.color.white_smoke) }
      setExpendStatusView(data)
      binding.imgArrow.rotation = if (data.isExpend) ROTATED_POSITION else INITIAL_POSITION
    } else {
      activity?.resources?.getDimensionPixelSize(R.dimen.size_40)
        ?.let { binding.contentParent.layoutParams.height = it }
      activity?.resources?.getDimensionPixelSize(R.dimen.size_16)
        ?.let { binding.view.layoutParams.height = it }
      setLastSeenView(data)
    }

    if (data.manageBusinessList.isNullOrEmpty().not()) {
      binding.rvManageBusiness.visible()
      activity?.let {
        binding.rvManageBusiness.apply {
          visible()
          val adapter1 =
            AppBaseRecyclerViewAdapter(it, data.manageBusinessList!!, this@BoostAddOnsViewHolder)
          val layoutManager1: GridLayoutManager = object : GridLayoutManager(context, 4) {
            override fun canScrollVertically(): Boolean {
              return false
            }
          }
          layoutManager = layoutManager1
          adapter = adapter1
        }
      }
    } else binding.rvManageBusiness.gone()

    binding.contentParent.setOnClickListener {
      if (data.isLastSeen.not()) {
        data.isExpend = data.isExpend.not()
        binding.imgArrow.rotateImage(data.isExpend)
        setExpendStatusView(data)
      }
    }

    binding.executePendingBindings()
  }

  private fun setExpendStatusView(data: AllBoostAddOnsData) {
    activity?.let {
      binding.imgArrow.setTintColor(
        ContextCompat.getColor(
          it,
          if (data.isExpend) R.color.grey_dim_2 else R.color.colorAccent
        )
      )
    }
    activity?.let {
      binding.txtTitle.setTextColor(
        ContextCompat.getColor(
          it,
          if (data.isExpend) R.color.greyish_brown else R.color.colorAccent
        )
      )
    }
    activity?.let {
      binding.contentParent.setCardBackgroundColor(
        ContextCompat.getColorStateList(
          it,
          if (data.isExpend) R.color.white_smoke else R.color.white
        )
      )
    }
    binding.contentParent.cardElevation = if (data.isExpend) 0F else 2F
    binding.rvManageBusiness.visibility = if (data.isExpend) View.VISIBLE else View.GONE
  }

  private fun setLastSeenView(data: AllBoostAddOnsData) {
    binding.imgArrow.visibility = View.INVISIBLE
    binding.mainContent.background = activity?.let { ContextCompat.getDrawable(it, R.color.white) }
    activity?.let {
      binding.contentParent.setCardBackgroundColor(
        ContextCompat.getColorStateList(
          it,
          R.color.white
        )
      )
    }
    binding.contentParent.cardElevation = 0F
    activity?.let {
      binding.imgSymbol.setTintColor(
        ContextCompat.getColor(
          it,
          R.color.colorAccent
        )
      )
    }
    activity?.let { binding.txtTitle.setTextColor(ContextCompat.getColor(it, R.color.colorAccent)) }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    if (actionType == RecyclerViewActionType.BUSINESS_ADD_ONS_CLICK.ordinal) {
      listener?.onItemClick(position, item, RecyclerViewActionType.BUSINESS_ADD_ONS_CLICK.ordinal)
    }
  }
}
