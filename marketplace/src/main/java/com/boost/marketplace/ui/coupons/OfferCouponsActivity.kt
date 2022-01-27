package com.boost.marketplace.ui.coupons

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R
import com.boost.marketplace.adapter.OfferCouponsAdapter
import com.boost.marketplace.data.OfferCoupons

class OfferCouponsActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_offer_coupons)
    supportActionBar?.hide()
    initializeOfferCouponsRv()
  }

  private fun initializeOfferCouponsRv() {
    val recyclerview = findViewById<RecyclerView>(R.id.offer_coupons_rv)

    recyclerview.layoutManager = LinearLayoutManager(this)

    val data = ArrayList<OfferCoupons>()
    for (i in 1..20) {
      data.add(OfferCoupons("25PAY", "Get flat INR 25/- cashback using Amazon Pay.", "Use code 25PAY & get flat INR 25/- cashback on transactions above INR 99/-. Learn More"))
    }
    val adapter = OfferCouponsAdapter(data)

    recyclerview.adapter = adapter
  }
}