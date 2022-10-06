package com.festive.poster.ui.promoUpdates.bottomSheet

import android.os.Bundle
import com.festive.poster.R
import com.festive.poster.databinding.BsheetBusinessPromotionAddPurchaseCartBinding
import com.festive.poster.utils.MarketPlaceUtils
import com.festive.poster.viewmodels.UpdateStudioPurchaseViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.rest.NetworkResult
import com.framework.utils.showSnackBarNegative

class BusinessPromotionAddToCartBottomSheet :
    BaseBottomSheetDialog<BsheetBusinessPromotionAddPurchaseCartBinding, UpdateStudioPurchaseViewModel>() {

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

    override fun getViewModelClass(): Class<UpdateStudioPurchaseViewModel> {
        return UpdateStudioPurchaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.rivCloseBottomSheet?.setOnClickListener { dismiss() }
        binding?.btnAddGoToCart?.setOnClickListener {

            MarketPlaceUtils.initiateAddonMarketplace(
                sessionManager!!,
                viewModel?.updateStudioFeature?.feature_code,
                requireActivity(),
                null,
                true
                )
        }
        observeApis()
    }

    private fun observeApis() {
        viewModel?.featurePurchaseData?.observe(this){
            when(it){
                is NetworkResult.Loading->{
                    binding?.progressBar?.visible()
                    binding!!.dataLayout!!.gone()
                }
                is NetworkResult.Success->{
                    viewModel?.updateStudioFeature?:return@observe
                    binding?.progressBar?.gone()
                    binding!!.dataLayout!!.visible()
                    setDataOnUi()

                }
                is NetworkResult.Error->{
                    binding?.progressBar?.gone()
                    binding!!.dataLayout!!.gone()
                    showSnackBarNegative(requireActivity(),getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun setDataOnUi() {
        val updateStudioFeature = viewModel?.updateStudioFeature!!

        binding?.tvMonthlyPrice?.text =getString(R.string.placeholder_per_month,
            updateStudioFeature.price.toString())
        binding?.tvYearlyPrice?.text = getString(R.string.or_placeholder_per_year,
            (updateStudioFeature.price*12).toString()
        )
    }
}