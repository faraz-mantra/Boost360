package com.festive.poster.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.festive.poster.R
import com.festive.poster.databinding.SheetOrderConfirmBinding
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.FESTIVAL_POSTER_ORDER_SUCCESS
import com.framework.webengageconstant.FESTIVAL_POSTER_VIEW_PACK_CLICK

class PosterOrderConfirmSheet : BaseBottomSheetDialog<SheetOrderConfirmBinding, BaseViewModel>() {


  private var packTag: String? = null
  private var sharedViewModel: FestivePosterSharedViewModel? = null


  companion object {
    val BK_TAG = "BK_TAG"

    @JvmStatic
    fun newInstance(tag: String): PosterOrderConfirmSheet {
      val bundle = Bundle().apply {
        putString(PosterListFragment.BK_TAG, tag)
      }
      val fragment = PosterOrderConfirmSheet()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.sheet_order_confirm
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    sharedViewModel = ViewModelProvider(requireActivity()).get(FestivePosterSharedViewModel::class.java)
    val selectedPosterPack = sharedViewModel?.selectedPosterPack
    binding?.tvPosterPackName?.text = selectedPosterPack?.tagsModel?.name + " pack of ${selectedPosterPack?.posterList?.size} posters"
    binding?.tvDesc?.text = getString(R.string.order_confirm_message, selectedPosterPack?.price?.toInt().toString())
    WebEngageController.trackEvent(FESTIVAL_POSTER_ORDER_SUCCESS, event_value = HashMap())
    packTag = arguments?.getString(PosterListFragment.BK_TAG)
    setOnClickListener(binding?.btnConfirm)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnConfirm -> {
        WebEngageController.trackEvent(FESTIVAL_POSTER_VIEW_PACK_CLICK, event_value = HashMap())
        addFragment(R.id.container, PosterListFragment.newInstance(sharedViewModel?.selectedPosterPack?.tagsModel?.tag!!), true,true)
        dismiss()
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

  }


}