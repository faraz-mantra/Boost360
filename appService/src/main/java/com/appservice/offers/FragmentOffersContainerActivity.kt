package com.appservice.offers

import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.appservice.offers.additionalinfo.AdditionalInfoFragment
import com.appservice.offers.details.AddNewOfferFragment
import com.appservice.offers.offerlisting.OfferListingFragment
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.models.BaseViewModel

class FragmentOffersContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {
    private var fragmentType: FragmentType? = null
    private var addNewOfferFragment: AddNewOfferFragment? = null
    private var additionalInfoFragment: AdditionalInfoFragment? = null
    private var offerListingFragment: OfferListingFragment? = null
    override fun getLayout(): Int {
        return R.layout.activity_fragment_container
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}