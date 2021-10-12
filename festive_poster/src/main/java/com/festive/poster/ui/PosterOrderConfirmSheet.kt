package com.festive.poster.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.festive.poster.R
import com.festive.poster.databinding.BsheetCustomizePosterBinding
import com.festive.poster.databinding.SheetOrderConfirmBinding
import com.festive.poster.databinding.SheetPosterPaymentBinding
import com.festive.poster.models.PosterCustomizationModel
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterSharedViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.FESTIVAL_POSTER_ORDER_SUCCESS
import com.framework.webengageconstant.FESTIVAL_POSTER_PAY_LATER_SCREEN
import com.framework.webengageconstant.FESTIVAL_POSTER_PURCHASED
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.util.FileUtil

class PosterOrderConfirmSheet: BaseBottomSheetDialog<SheetOrderConfirmBinding, BaseViewModel>() {


    private var packTag: String?=null
    private var sharedViewModel: FestivePosterSharedViewModel?=null


    companion object{
        val BK_TAG="BK_TAG"
        @JvmStatic
        fun newInstance(tag:String): PosterOrderConfirmSheet {
            val bundle = Bundle().apply {
                putString(PosterListFragment.BK_TAG,tag)
            }
            val fragment =PosterOrderConfirmSheet()
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

        WebEngageController.trackEvent(FESTIVAL_POSTER_ORDER_SUCCESS)
        packTag= arguments?.getString(PosterListFragment.BK_TAG)
        setOnClickListener(binding?.btnConfirm)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnConfirm->{
                WebEngageController.trackEvent(FESTIVAL_POSTER_PURCHASED)
                addFragmentReplace(R.id.container,PosterListFragment.newInstance(sharedViewModel?.customizationDetails?.value?.tag!!),true)
                dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }


}