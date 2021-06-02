package com.dashboard.controller.ui.website_theme.bottomsheet

import android.view.View
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
  private var secondaryItem: SecondaryItem? = null
  private var primaryItem: PrimaryItem? = null
  private var secondaryFontAdapter: AppBaseRecyclerViewAdapter<SecondaryItem>? = null
  private var primaryFontsAdapter: AppBaseRecyclerViewAdapter<PrimaryItem>? = null
  private var secondaryFontList: ArrayList<SecondaryItem>? = null
  private var primaryFontList: ArrayList<PrimaryItem>? = null
  var onPrimaryClicked: (value: PrimaryItem) -> Unit = { }
  var onSecondaryClicked: (value: SecondaryItem) -> Unit = { }
  var isPrimaryFontSelection = false

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_select_font
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.rivCloseBottomSheet, binding?.btnDone)
    this.primaryFontList = arguments?.get(IntentConstant.FONT_LIST_PRIMARY.name) as? ArrayList<PrimaryItem>
    this.secondaryFontList = arguments?.get(IntentConstant.FONT_LIST_SECONDARY.name) as? ArrayList<SecondaryItem>
    when {
      primaryFontList != null -> {
        isPrimaryFontSelection = true
        this.primaryFontsAdapter = AppBaseRecyclerViewAdapter(baseActivity, primaryFontList?: arrayListOf(), this@BottomSheetSelectFont)
        binding?.rvFont?.adapter = primaryFontsAdapter
        binding?.ctvSubheading?.text = getString(R.string.the_font_you_want_to_change_as_primary)
      }
      else -> {
        isPrimaryFontSelection = false
        this.secondaryFontAdapter = AppBaseRecyclerViewAdapter(baseActivity, secondaryFontList?: arrayListOf(), this@BottomSheetSelectFont)
        binding?.rvFont?.adapter = secondaryFontAdapter
        binding?.ctvSubheading?.text = getString(R.string.the_font_you_want_to_change_as_secondary)

      }
    }

  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.PRIMARY_FONT_SELECTED.ordinal -> {
        this.primaryItem = item as? PrimaryItem
        primaryFontList?.forEach {
          it.isSelected = it == item
        }
        binding?.rvFont?.post { primaryFontsAdapter?.notifyDataSetChanged() }
      }
      RecyclerViewActionType.SECONDARY_FONT_SELECTED.ordinal -> {
        this.secondaryItem = item as? SecondaryItem
        secondaryFontList?.forEach {
          it.isSelected = it == item
        }
        binding?.rvFont?.post { secondaryFontAdapter?.notifyDataSetChanged() }
      }

    }

  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.rivCloseBottomSheet -> {
        dismiss()
      }
      binding?.btnDone -> {
        when (isPrimaryFontSelection) {
          true -> onPrimaryClicked(primaryItem!!)
          else -> onSecondaryClicked(secondaryItem!!)

        }
        dismiss()
      }
    }
  }

}