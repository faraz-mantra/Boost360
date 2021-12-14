package com.boost.marketplace.ui.details

import android.view.View
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityFeatureDetailsBinding

class FeatureDetailsActivity : AppBaseActivity<ActivityFeatureDetailsBinding, FeatureDetailsViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_feature_details
    }

    override fun getViewModelClass(): Class<FeatureDetailsViewModel> {
        return FeatureDetailsViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.addItemToCart)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when(v){
            binding?.addItemToCart -> {
                val dialog = FeaturesDetailsDialog()
                dialog.show(this.supportFragmentManager,"FeatureDialog")
            }
        }
    }
}