package com.boost.marketplace.ui.Packs

import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityPacksBinding

class PacksActivity : AppBaseActivity<ActivityPacksBinding, PacksViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_packs
    }

    override fun getViewModelClass(): Class<PacksViewModel> {
        TODO("Not yet implemented")
    }

    override fun onCreateView() {
        super.onCreateView()
    }
}