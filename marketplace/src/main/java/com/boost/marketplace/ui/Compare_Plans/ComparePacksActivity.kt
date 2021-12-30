package com.boost.marketplace.ui.Compare_Plans

import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityComparePacksBinding

class ComparePacksActivity: AppBaseActivity<ActivityComparePacksBinding, ComparePacksViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_compare_packs
    }


    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
    }


}