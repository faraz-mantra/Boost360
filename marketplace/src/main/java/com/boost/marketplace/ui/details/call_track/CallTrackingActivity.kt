package com.boost.marketplace.ui.details.call_track

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PartnerZone
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PromoBanners
import com.boost.dbcenterapi.data.api_model.call_track.CallTrackListResponse
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel
import com.boost.marketplace.R
import com.boost.marketplace.adapter.NumberListAdapter
import com.boost.marketplace.adapter.OfferCouponsAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityCallTrackingBinding
import com.boost.marketplace.interfaces.HomeListener
import com.boost.marketplace.ui.coupons.OfferCouponViewModel
import com.boost.marketplace.ui.details.domain.*
import com.framework.analytics.SentryController


class CallTrackingActivity : AppBaseActivity<ActivityCallTrackingBinding, CallTrackViewModel>(),
    HomeListener {
    lateinit var numberList: CallTrackListResponse

    override fun getLayout(): Int {
        return R.layout.activity_call_tracking
    }

    companion object {
        fun newInstance() = CallTrackingActivity()
    }


    override fun getViewModelClass(): Class<CallTrackViewModel> {
        return CallTrackViewModel::class.java
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[CallTrackViewModel::class.java]
        viewModel.setApplicationLifecycle(application, this)

        loadData()
        initMvvm()
    }

    override fun onCreateView() {
        super.onCreateView()


        binding?.help?.setOnClickListener {
            val dialogCard = CallTrackingHelpBottomSheet()
            dialogCard.show(
                this.supportFragmentManager,
                CallTrackingHelpBottomSheet::class.java.name
            )
        }
        binding?.btnSelectNumber?.setOnClickListener {
            val dialogCard = SelectedNumberBottomSheet()
            dialogCard.show(this.supportFragmentManager, SelectedNumberBottomSheet::class.java.name)
        }

    }

    private fun loadData() {
        try {
            viewModel.loadNumberList(
                intent.getStringExtra("fpid") ?: "",
                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"

            )
        } catch (e: Exception) {
            SentryController.captureException(e)
        }

    }

    private fun initMvvm() {
        viewModel.getCallTrackingDetails().observe(this) {
            if (it != null) {

                System.out.println("numberList" + it)

                val recyclerview = findViewById<RecyclerView>(R.id.rv_number_list)
                recyclerview.layoutManager = LinearLayoutManager(this)

                val adapter = NumberListAdapter(this, it, this)
                recyclerview.adapter = adapter
            }
        }
    }

    override fun onPackageClicked(item: Bundles?) {
        TODO("Not yet implemented")
    }

    override fun onPromoBannerClicked(item: PromoBanners?) {
        TODO("Not yet implemented")
    }

    override fun onShowHidePromoBannerIndicator(status: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onPartnerZoneClicked(item: PartnerZone?) {
        TODO("Not yet implemented")
    }

    override fun onShowHidePartnerZoneIndicator(status: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onAddFeatureDealItemToCart(item: FeaturesModel?, minMonth: Int) {
        TODO("Not yet implemented")
    }

    override fun onAddonsCategoryClicked(categoryType: String) {
        TODO("Not yet implemented")
    }

    override fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel) {
        TODO("Not yet implemented")
    }

    override fun onPackageAddToCart(item: Bundles?, image: ImageView) {
        TODO("Not yet implemented")
    }
}