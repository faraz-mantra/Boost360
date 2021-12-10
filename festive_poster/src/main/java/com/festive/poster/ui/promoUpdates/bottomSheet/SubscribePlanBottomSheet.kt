package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import com.festive.poster.R
import com.festive.poster.databinding.BsheetSubscribePlanValidityBinding
import com.festive.poster.utils.MarketPlaceUtils
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class SubscribePlanBottomSheet : BaseBottomSheetDialog<BsheetSubscribePlanValidityBinding, BaseViewModel>() {

    var session:UserSessionManager?=null
    companion object {
        @JvmStatic
        fun newInstance(): SubscribePlanBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = SubscribePlanBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_subscribe_plan_validity
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        /*binding?.btnGetPack?.setOnClickListener {
            dismiss()
        }*/
        session = UserSessionManager(requireActivity())


        binding?.btnGetPack?.setOnClickListener {
            gotoMarketPlace()
        }

        binding?.rivCloseBottomSheet?.setOnClickListener {
            dismiss()
        }

        binding?.linearRegularAmount?.setOnClickListener {
            updatePackageSelectionUI(0)
        }

        binding?.linearDiscountAmount?.setOnClickListener {
            updatePackageSelectionUI(1)
        }
    }

    private fun updatePackageSelectionUI(buttonPositionSelected: Int = 0){
        if (buttonPositionSelected == 0){
            binding?.ivSelectionIndicator1?.setImageResource(R.drawable.ic_tick_green_round)
            binding?.ivSelectionIndicator1?.setBackgroundResource(0)
            binding?.ivSelectionIndicator2?.setImageResource(0)
            binding?.ivSelectionIndicator2?.setBackgroundResource(R.drawable.bg_grey_stroke_circle)
            binding?.ivOfferPercent1?.visible()
            binding?.ivOfferPercent2?.gone()
            binding?.linearRegularAmount?.setBackgroundResource(R.drawable.bg_yellow_stroke_et)
            binding?.linearDiscountAmount?.setBackgroundResource(R.drawable.bg_grey_stroke_et)
        }else{
            binding?.ivSelectionIndicator1?.setImageResource(0)
            binding?.ivSelectionIndicator1?.setBackgroundResource(R.drawable.bg_grey_stroke_circle)
            binding?.ivSelectionIndicator2?.setImageResource(R.drawable.ic_tick_green_round)
            binding?.ivSelectionIndicator2?.setBackgroundResource(0)
            binding?.ivOfferPercent1?.gone()
            binding?.ivOfferPercent2?.visible()
            binding?.linearRegularAmount?.setBackgroundResource(R.drawable.bg_grey_stroke_et)
            binding?.linearDiscountAmount?.setBackgroundResource(R.drawable.bg_yellow_stroke_et)
        }
    }

    private fun gotoMarketPlace() {
        MarketPlaceUtils.initiateAddonMarketplace(session!!,true,"","",true,requireContext())
    }

}