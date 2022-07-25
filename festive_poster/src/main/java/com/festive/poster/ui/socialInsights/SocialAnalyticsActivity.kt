package com.festive.poster.ui.socialInsights

import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivitySocialAnalyticsBinding
import com.framework.models.BaseViewModel

class SocialAnalyticsActivity : AppBaseActivity<ActivitySocialAnalyticsBinding, BaseViewModel>() {

    override fun getLayout(): Int {
        return R.layout.activity_social_analytics
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
    }
}