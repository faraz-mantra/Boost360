package com.appservice.offers.additionalinfo

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentOfferAdditionalInfoBinding
import com.appservice.offers.offerlisting.OfferListingFragment
import com.framework.models.BaseViewModel

class AdditionalInfoFragment: AppBaseFragment<FragmentOfferAdditionalInfoBinding, BaseViewModel>() {
    override fun getLayout(): Int {
       return R.layout.fragment_offer_additional_info
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
    companion object{
        fun newInstance(): AdditionalInfoFragment {
            return AdditionalInfoFragment()
        }
    }
}