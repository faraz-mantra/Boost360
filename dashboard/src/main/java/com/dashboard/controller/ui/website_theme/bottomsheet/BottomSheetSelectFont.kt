package com.dashboard.controller.ui.website_theme.bottomsheet

import com.dashboard.R
import com.dashboard.constant.IntentConstant
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.BottomSheetSelectFontBinding
import com.dashboard.model.websitetheme.PrimaryItem
import com.dashboard.model.websitetheme.SecondaryItem
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetSelectFont : BaseBottomSheetDialog<BottomSheetSelectFontBinding, BaseViewModel>(), RecyclerItemClickListener {
  private var secondaryFontAdapter: AppBaseRecyclerViewAdapter<SecondaryItem>? = null
  private var primaryFontsAdapter: AppBaseRecyclerViewAdapter<PrimaryItem>? = null
  private var secondaryFontList: ArrayList<SecondaryItem>? = null
  private var primaryFontList: ArrayList<PrimaryItem>? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_select_font
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    this.primaryFontList = arguments?.get(IntentConstant.FONT_LIST_PRIMARY.name) as? ArrayList<PrimaryItem>
    this.secondaryFontList = arguments?.get(IntentConstant.FONT_LIST_SECONDARY.name) as? ArrayList<SecondaryItem>
    when {
      primaryFontList != null -> {
        this.primaryFontsAdapter = AppBaseRecyclerViewAdapter(baseActivity, primaryFontList!!, this@BottomSheetSelectFont)
        binding?.rvFont?.adapter = primaryFontsAdapter
      }
      else -> {
        this.secondaryFontAdapter = AppBaseRecyclerViewAdapter(baseActivity, secondaryFontList!!, this@BottomSheetSelectFont)
        binding?.rvFont?.adapter =secondaryFontAdapter
      }
    }

  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.PRIMARY_FONT_SELECTED.ordinal -> {
        primaryFontList?.forEach {
          it.isSelected = it == item
        }
        binding?.rvFont?.post { primaryFontsAdapter?.notifyDataSetChanged() }
      }
      RecyclerViewActionType.SECONDARY_FONT_SELECTED.ordinal -> {
        secondaryFontList?.forEach {
          it.isSelected = it == item
        }
        binding?.rvFont?.post { secondaryFontAdapter?.notifyDataSetChanged() }
      }

    }

  }
}