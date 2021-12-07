package com.boost.marketplace.ui.home.MarketplaceV2_ComparePacks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.marketplace.R
import com.boost.marketplace.databinding.MarketplaceofferdetailsBinding
import kotlinx.android.synthetic.main.marketplaceofferdetails.*


class MarketPlaceOfferDetails : AppCompatActivity() {

    lateinit var binding: MarketplaceofferdetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      binding= DataBindingUtil.setContentView(this, R.layout.marketplaceofferdetails)

        val termsconditions= listOf("welcome to our terms and conditions policy.","please go thorugh all the details")
        recyclerTerms.adapter= OfferTermsAdapter(termsconditions)

        val privacyconditions= listOf("welcome to our privacy conditions policy.","please go thorugh all the details")
        recyclerOfferDetails.adapter= OfferTermsAdapter(privacyconditions)

        initializeRecycler()
        initializeTermsRecycler()

//        binding.dateFromToLayout.setOnClickListener {
//
//        }
//        binding.availCouponSubmit.setOnClickListener {
//
//        }
//
//        binding.packageBack.setOnClickListener {
//
//        }
//        binding.help.setOnClickListener {
//
//        }
    }

    private fun initializeRecycler() {
        val gridLayoutManager = LinearLayoutManager(MarketPlaceOfferDetails())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerOfferDetails.apply {
            layoutManager = gridLayoutManager
        }
    }

    private fun initializeTermsRecycler() {
        val gridLayoutManager = LinearLayoutManager(MarketPlaceOfferDetails())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerTerms.apply {
            layoutManager = gridLayoutManager
        }
    }
}
