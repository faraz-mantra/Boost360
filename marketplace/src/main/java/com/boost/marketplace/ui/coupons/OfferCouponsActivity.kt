package com.boost.marketplace.ui.coupons

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.boost.marketplace.R

class OfferCouponsActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_offer_coupons)
    supportActionBar?.hide()
  }
}