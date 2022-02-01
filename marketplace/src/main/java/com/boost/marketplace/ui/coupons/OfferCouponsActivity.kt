package com.boost.marketplace.ui.coupons

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.couponRequest.BulkPropertySegment
import com.boost.dbcenterapi.data.api_model.couponRequest.CouponRequest
import com.boost.dbcenterapi.data.api_model.couponRequest.ObjectKeys
import com.boost.dbcenterapi.data.api_model.getCouponResponse.Data
import com.boost.dbcenterapi.data.api_model.getCouponResponse.GetCouponResponse
import com.boost.dbcenterapi.data.api_model.getCouponResponse.GetCouponResponseItem
import com.boost.marketplace.R
import com.boost.marketplace.adapter.OfferCouponsAdapter
import com.boost.marketplace.data.OfferCoupons
import java.util.*
import kotlin.collections.ArrayList

class OfferCouponsActivity : AppCompatActivity() {

  private lateinit var viewModel: OfferCouponViewModel
  private var couponResponse: GetCouponResponse? = null
  private var couponData = arrayListOf<Data>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_offer_coupons)
    supportActionBar?.hide()
    viewModel = ViewModelProvider(this).get(OfferCouponViewModel::class.java)
    viewModel.setApplicationLifecycle(application,  this)
    loadOfferCoupons()
    initMvvm()
    initializeOfferCouponsRv()
  }

  private fun loadOfferCoupons() {
    val schemaId = "5e5877a701921c02011ca983"
    val websiteId = "5e7a3cf46e0572000109a5b2"
    var bulkPropertySegment = ArrayList<ArrayList<BulkPropertySegment>>()
    val bulkObject1 = BulkPropertySegment(propertyDataType = "upgrade", propertyName = "upgrade", type = 5)
    var objectKeys = ObjectKeys(true, description = true, discountPercent = true, kid = true, termsandconditions = true, title = true)
    val bulkObject2 = BulkPropertySegment(0, 10, objectKeys, "coupon", "discount_coupons", 1)
//    bulkPropertySegment.add(Collections.singletonList(bulkObject1) as ArrayList<BulkPropertySegment>)
//    bulkPropertySegment.add(Collections.singletonList(bulkObject2) as ArrayList<BulkPropertySegment>)
    bulkPropertySegment.add(0, arrayListOf(bulkObject1,bulkObject2))
    viewModel.getCouponRedeem(CouponRequest(bulkPropertySegment, schemaId, websiteId))
  }

  private fun initMvvm() {
    viewModel.getCouponApiResult().observe(this, {
      if (it != null) {
        couponResponse = it
        for (items in couponData){
          couponData.add(Data(items.code,items.description,items.discountPercent,items.kid,items.termsandconditions,items.title))
        }
      }
    })
  }

  private fun initializeOfferCouponsRv() {
    val recyclerview = findViewById<RecyclerView>(R.id.offer_coupons_rv)
    recyclerview.layoutManager = LinearLayoutManager(this)

//    val data = ArrayList<OfferCoupons>()
//    for (i in 1..20) {
//      data.add(OfferCoupons("25PAY", "Get flat INR 25/- cashback using Amazon Pay.", "Use code 25PAY & get flat INR 25/- cashback on transactions above INR 99/-. Learn More"))
//    }
    val adapter = OfferCouponsAdapter(couponData)
    recyclerview.adapter = adapter
  }
}