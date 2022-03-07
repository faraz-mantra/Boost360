package com.boost.marketplace.ui.coupons

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.utils.Constants.Companion.SCHEMA_ID
import com.boost.cart.utils.Constants.Companion.WEBSITE_ID
import com.boost.dbcenterapi.data.api_model.couponRequest.BulkPropertySegment
import com.boost.dbcenterapi.data.api_model.couponRequest.CouponRequest
import com.boost.dbcenterapi.data.api_model.couponRequest.ObjectKeys
import com.boost.dbcenterapi.data.api_model.getCouponResponse.Data
import com.boost.marketplace.R
import com.boost.marketplace.adapter.OfferCouponsAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityOfferCouponsBinding
import com.boost.marketplace.ui.videos.HelpVideosBottomSheet
import kotlinx.android.synthetic.main.activity_offer_coupons.*
import kotlin.collections.ArrayList

class OfferCouponsActivity : AppBaseActivity<ActivityOfferCouponsBinding, OfferCouponViewModel>() {

    private var couponData = ArrayList<Data>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer_coupons)
        supportActionBar?.hide()
        viewModel = ViewModelProvider(this).get(OfferCouponViewModel::class.java)
        viewModel.setApplicationLifecycle(application, this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(R.color.common_text_color))
        }

        back_button12.setOnClickListener {
            finish()
        }

        help.setOnClickListener {
            val videoshelp = HelpVideosBottomSheet()
            videoshelp.show(this.supportFragmentManager, HelpVideosBottomSheet::class.java.name)
        }

        loadOfferCoupons()
        initMvvm()
    }

    private fun loadOfferCoupons() {
        var bulkPropertySegment = ArrayList<ArrayList<BulkPropertySegment>>()
        val bulkObject1 = BulkPropertySegment(propertyDataType = "upgrade", propertyName = "upgrade", type = 5)
        var objectKeys = ObjectKeys(true, description = true, discountPercent = true, kid = true, termsandconditions = true, title = true)
        val bulkObject2 = BulkPropertySegment(0, 10, objectKeys, "coupon", "discount_coupons", 1)
        bulkPropertySegment.add(0, arrayListOf(bulkObject1, bulkObject2))
        viewModel.getCouponRedeem(CouponRequest(bulkPropertySegment, SCHEMA_ID, WEBSITE_ID))
    }

    private fun initMvvm() {
        viewModel.getCouponApiResult().observe(this) {
            if (it != null) {

                for (i in 0 until it.size) {

                    it[i].data?.let { it1 -> couponData.addAll(it1) }
                }
                System.out.println("CouponData" + couponData)

                val recyclerview = findViewById<RecyclerView>(R.id.offer_coupons_rv)
                recyclerview.layoutManager = LinearLayoutManager(this)

                val adapter = OfferCouponsAdapter(couponData)
                recyclerview.adapter = adapter
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_offer_coupons
    }

    override fun getViewModelClass(): Class<OfferCouponViewModel> {
        return OfferCouponViewModel::class.java
    }

}