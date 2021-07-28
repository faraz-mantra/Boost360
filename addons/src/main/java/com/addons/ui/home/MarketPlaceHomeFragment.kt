package com.addons.ui.home

import com.addons.R
import com.addons.base.AppBaseFragment
import com.addons.databinding.FrgamentMarketplaceHomeBinding
import com.framework.models.BaseViewModel

class MarketPlaceHomeFragment :AppBaseFragment<FrgamentMarketplaceHomeBinding,BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.frgament_marketplace_home
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
    }
}