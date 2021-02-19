package com.appservice.offers.details

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentAddNewOffersBinding
import com.framework.models.BaseViewModel

class AddNewOfferFragment : AppBaseFragment<FragmentAddNewOffersBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_add_new_offers
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

    }
}