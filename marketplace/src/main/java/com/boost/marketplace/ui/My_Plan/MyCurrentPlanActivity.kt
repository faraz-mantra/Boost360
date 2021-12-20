package com.boost.marketplace.ui.My_Plan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMarketplaceoffersBinding
import com.boost.marketplace.databinding.ActivityMyCurrentPlanBinding
import com.boost.marketplace.ui.Marketplace_Offers.MarketPlaceOffersViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyCurrentPlanActivity : AppBaseActivity<ActivityMyCurrentPlanBinding,MyCurrentPlanViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_my_current_plan
    }

    override fun getViewModelClass(): Class<MyCurrentPlanViewModel> {
        return MyCurrentPlanViewModel::class.java
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding?.help?.setOnClickListener {
            val intent = Intent(this, MyPlanHistoryOrdersActivity::class.java)
            startActivity(intent)

        }

    }

}