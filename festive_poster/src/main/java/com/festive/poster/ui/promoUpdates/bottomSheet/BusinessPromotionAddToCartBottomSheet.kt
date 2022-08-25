package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import com.festive.poster.R
import com.festive.poster.databinding.BsheetBusinessPromotionAddPurchaseCartBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BusinessPromotionAddToCartBottomSheet : BaseBottomSheetDialog<BsheetBusinessPromotionAddPurchaseCartBinding, BaseViewModel>() {

    companion object {

        @JvmStatic
        fun newInstance(): BusinessPromotionAddToCartBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = BusinessPromotionAddToCartBottomSheet()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_business_promotion_add_purchase_cart
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.rivCloseBottomSheet?.setOnClickListener { dismiss() }
        binding?.btnAddGoToCart?.setOnClickListener { dismiss() }
    }
}