package com.boost.marketplace.ui.browse

import android.content.Intent
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityBrowseFeaturesBinding
import com.boost.marketplace.ui.home.MarketPlaceHomeViewModel

class BrowseFeaturesActivity : AppBaseActivity<ActivityBrowseFeaturesBinding, BrowseFeaturesViewModel>() {

  override fun getLayout(): Int {
    return R.layout.activity_browse_features
  }

  override fun getViewModelClass(): Class<BrowseFeaturesViewModel> {
    return BrowseFeaturesViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()

    binding?.browseSearch?.setOnClickListener {
      val intent= Intent(this,SearchActivity::class.java)
      startActivity(intent)
    }
  }
}

