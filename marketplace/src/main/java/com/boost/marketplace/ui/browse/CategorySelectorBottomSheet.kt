package com.boost.marketplace.ui.browse

import android.app.Activity
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.adapter.AddonsCategorySelectorAdapter
import com.boost.marketplace.databinding.BottomSheetCategorySelectorBinding
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CategorySelectorListener
import com.framework.analytics.SentryController
import com.framework.base.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_category_selector.*

class CategorySelectorBottomSheet(val activity: Activity, val listener: CategorySelectorListener):
  BaseBottomSheetDialog<BottomSheetCategorySelectorBinding, BrowseFeaturesViewModel>(),
  AddonsListener
{

  lateinit var adapter: AddonsCategorySelectorAdapter

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_category_selector
  }

  override fun getViewModelClass(): Class<BrowseFeaturesViewModel> {
    return BrowseFeaturesViewModel::class.java
  }


  override fun onCreateView() {
    dialog.behavior.isDraggable = true
    viewModel!!.setApplicationLifecycle(activity.application, this.viewLifecycleOwner)
    adapter = AddonsCategorySelectorAdapter(activity, arrayListOf(), listener)
    recyclerView.adapter = adapter
    initMvvm()
    try {
      viewModel!!.loadAllFeaturesfromDB()
    } catch (e: Exception) {
      SentryController.captureException(e)
    }

    close_btn.setOnClickListener {
      dismiss()
    }

  }

  fun initMvvm(){
    viewModel!!.getAllAvailableFeatures().observe(this, androidx.lifecycle.Observer {
      updateAddonCategoryRecycler(it)
    })
  }

  fun updateAddonCategoryRecycler(list: List<FeaturesModel>) {
    val addonsCategoryTypes = arrayListOf<String>()
    for (singleFeaturesModel in list) {
      if (singleFeaturesModel.target_business_usecase != null && !addonsCategoryTypes.contains(
          singleFeaturesModel.target_business_usecase
        )
      ) {
          addonsCategoryTypes.add(singleFeaturesModel.target_business_usecase!!)
      }
    }

    adapter.addupdates(addonsCategoryTypes)
    recyclerView.isFocusable = false
//        back_image.isFocusable = true
  }

  override fun onAddonsClicked(item: FeaturesModel) {
    TODO("Not yet implemented")
  }

}