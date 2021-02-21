package com.appservice.offers.details

import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentAddNewOffersBinding
import com.appservice.offers.startOfferFragmentActivity
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
        setOnClickListener(binding?.btnOtherInfo,binding?.payServiceView)

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnOtherInfo -> {
                startOfferFragmentActivity(requireActivity(), FragmentType.OFFER_ADDITIONAL_INFO, isResult = true)
            }
        }
    }

    companion object {
        fun newInstance(): AddNewOfferFragment {
            return AddNewOfferFragment()
        }
    }
}