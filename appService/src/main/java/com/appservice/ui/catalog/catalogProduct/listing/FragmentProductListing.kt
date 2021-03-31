package com.appservice.ui.catalog.catalogProduct.listing

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentProductListingBinding
import com.appservice.ui.model.ItemsItem
import com.appservice.viewmodel.ProductViewModel

class FragmentProductListing : AppBaseFragment<FragmentProductListingBinding, ProductViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_product_listing
    }

    override fun getViewModelClass(): Class<ProductViewModel> {
        return ProductViewModel::class.java
    }

    companion object {
        fun newInstance(isNonPhysicalExperience: Boolean?, currencyType: String?, fpId: String?, fpTag: String?, clientId: String?, externalSourceId: String?, applicationId: String?, userProfileId: String?): FragmentProductListing {
            val bundle = Bundle()
            bundle.putBoolean(IntentConstant.NON_PHYSICAL_EXP_CODE.name, isNonPhysicalExperience!!)
            bundle.putString(IntentConstant.CURRENCY_TYPE.name, "INR")
            bundle.putString(IntentConstant.FP_ID.name, fpId)
            bundle.putString(IntentConstant.FP_TAG.name, fpTag)
            bundle.putString(IntentConstant.USER_PROFILE_ID.name, userProfileId)
            bundle.putString(IntentConstant.CLIENT_ID.name, clientId)
            bundle.putString(IntentConstant.EXTERNAL_SOURCE_ID.name, externalSourceId)
            bundle.putString(IntentConstant.APPLICATION_ID.name, applicationId)
            val productListingFragment = FragmentProductListing()
            productListingFragment.arguments = bundle
            return productListingFragment
        }

        private const val STORAGE_CODE = 120
        var defaultShareGlobal = true
        var shareType = 2
        var shareProduct: ItemsItem? = null
        fun newInstance(): FragmentProductListing {
            return FragmentProductListing()
        }
    }

}