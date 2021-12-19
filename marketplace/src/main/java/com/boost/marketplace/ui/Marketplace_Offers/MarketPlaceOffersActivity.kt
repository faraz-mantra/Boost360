package com.boost.marketplace.ui.Marketplace_Offers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.marketplace.R
import com.boost.marketplace.databinding.MarketplaceoffersBinding
import kotlinx.android.synthetic.main.marketplaceoffers.*


class MarketPlaceOffersActivity : AppCompatActivity() {

    lateinit var binding: MarketplaceoffersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      binding= DataBindingUtil.setContentView(this, R.layout.activity_marketplaceoffers)

        initializeDetailsRecycler()
        initializeTermsRecycler()

        binding.dateFromToLayout.setOnClickListener {

        }
        binding.availCouponSubmit.setOnClickListener {

        }

        binding.packageBack.setOnClickListener {

        }
        binding.help.setOnClickListener {

        }
    }

    private fun initializeDetailsRecycler() {
        val gridLayoutManager = LinearLayoutManager(MarketPlaceOffersActivity())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerOfferDetails.apply {
            layoutManager = gridLayoutManager
        }
    }

    private fun initializeTermsRecycler() {
        val gridLayoutManager = LinearLayoutManager(MarketPlaceOffersActivity())
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerTerms.apply {
            layoutManager = gridLayoutManager
        }
    }
}
