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
    binding?.btnDone?.isEnabled = false
    this.primaryFontList = arguments?.get(IntentConstant.FONT_LIST_PRIMARY.name) as? ArrayList<PrimaryItem>
    this.secondaryFontList = arguments?.get(IntentConstant.FONT_LIST_SECONDARY.name) as? ArrayList<SecondaryItem>
    when {
      primaryFontList != null -> {
        isPrimaryFontSelection = true
        this.primaryFontsAdapter = AppBaseRecyclerViewAdapter(baseActivity, primaryFontList ?: arrayListOf(), this@BottomSheetSelectFont)
        binding?.rvFont?.adapter = primaryFontsAdapter
        binding?.ctvSubheading?.text = getString(R.string.default_theme_font)
        binding?.ctvHeading?.text = getString(R.string.select_primary_font)
      }
      else -> {
        isPrimaryFontSelection = false
        this.secondaryFontAdapter = AppBaseRecyclerViewAdapter(baseActivity, secondaryFontList ?: arrayListOf(), this@BottomSheetSelectFont)
        binding?.rvFont?.adapter = secondaryFontAdapter
        binding?.ctvSubheading?.text = getString(R.string.default_theme_font)
        binding?.ctvHeading?.text = getString(R.string.select_secondary_font)
      }
    }

  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.PRIMARY_FONT_SELECTED.ordinal -> {
        this.primaryItem = item as? PrimaryItem
        binding?.btnDone?.isEnabled = !(primaryItem?.isSelected == true && primaryItem?.isNewSelected == true)
        primaryFontList?.forEach {
          if (item != it) it.isNewSelected = false
        }
        binding?.rvFont?.post { primaryFontsAdapter?.notifyDataSetChanged() }
      }
      RecyclerViewActionType.SECONDARY_FONT_SELECTED.ordinal -> {
        this.secondaryItem = item as? SecondaryItem
        binding?.btnDone?.isEnabled = !(secondaryItem?.isSelected == true && secondaryItem?.isNewSelected == true)
        secondaryFontList?.forEach {
          if (item != it) it.isNewSelected = false
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