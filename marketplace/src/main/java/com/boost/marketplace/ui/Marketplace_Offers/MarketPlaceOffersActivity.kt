package com.boost.marketplace.ui.Marketplace_Offers

import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMarketplaceoffersBinding


class MarketPlaceOffersActivity : AppBaseActivity<ActivityMarketplaceoffersBinding, MarketPlaceOffersViewModel>() {

    override fun getLayout(): Int {
        return R.layout.activity_marketplaceoffers



    }

    override fun getViewModelClass(): Class<MarketPlaceOffersViewModel> {
        return MarketPlaceOffersViewModel::class.java
    }





}





//    lateinit var binding: MarketPlaceOffersActivity

  //  override fun onCreate(savedInstanceState: Bundle?) {
     //   super.onCreate(savedInstanceState)
    //  binding= DataBindingUtil.setContentView(this, R.layout.activity_marketplaceoffers)
//
//        initializeDetailsRecycler()
//        initializeTermsRecycler()
//
//        binding.dateFromToLayout.setOnClickListener {
//
//        }
//        binding.availCouponSubmit.setOnClickListener {
//
//        }
//
//        binding.package_back.setOnClickListener {
//
//        }
//        binding.help.setOnClickListener {
//
//        }
//    }
//
//    private fun initializeDetailsRecycler() {
//        val gridLayoutManager = LinearLayoutManager(MarketPlaceOffersActivity())
//        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
//        recyclerOfferDetails.apply {
//            layoutManager = gridLayoutManager
//        }
//    }
//
//    private fun initializeTermsRecycler() {
//        val gridLayoutManager = LinearLayoutManager(MarketPlaceOffersActivity())
//        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
//        recyclerTerms.apply {
//            layoutManager = gridLayoutManager
//        }
//    }
//}
