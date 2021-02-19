package com.appservice.offers.offerlisting

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentOfferListingBinding
import com.framework.models.BaseViewModel

class OfferListingFragment : AppBaseFragment<FragmentOfferListingBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_offer_listing
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}