package com.marketplace.ui.home

import com.framework.base.BaseResponse
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.marketplace.R
import com.marketplace.base.AppBaseFragment
import com.marketplace.databinding.FragmentMarketplaceHomeBinding
import com.marketplace.model.features.FeatureResponse
import com.marketplace.rest.TaskCode
import com.marketplace.viewmodel.MarketPlaceHomeViewModel

class MarketPlaceHomeFragment :AppBaseFragment<FragmentMarketplaceHomeBinding,MarketPlaceHomeViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_marketplace_home
    }

    override fun getViewModelClass(): Class<MarketPlaceHomeViewModel> {
        return MarketPlaceHomeViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
//        hitApi(
//            viewModel?.getAllFeatures(),""
//        )
        viewModel?.getAllFeatures()?.observeOnce(viewLifecycleOwner,{
            val response = it as? FeatureResponse
        })
    }

    override fun onSuccess(it: BaseResponse) {
        super.onSuccess(it)
        when (it.taskcode){
            TaskCode.GET_ALL_FEATURES.ordinal -> {
                val response = it as? FeatureResponse
            }
        }
    }

    override fun onFailure(it: BaseResponse) {
        super.onFailure(it)
    }
}