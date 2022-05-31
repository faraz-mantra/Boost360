package com.boost.marketplace.ui.details.call_track

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
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
import com.boost.marketplace.ui.details.FeatureDetailsViewModel
import com.boost.marketplace.ui.details.domain.*
import com.framework.analytics.SentryController
import kotlinx.android.synthetic.main.activity_call_tracking.*
import kotlinx.android.synthetic.main.activity_my_current_plan.*


class CallTrackingActivity :
    AppBaseActivity<ActivityCallTrackingBinding, FeatureDetailsViewModel>(),
    HomeListener {
    lateinit var numberList: ArrayList<String>

    override fun getLayout(): Int {
        return R.layout.activity_call_tracking
    }

    companion object {
        fun newInstance() = CallTrackingActivity()
    }


    override fun getViewModelClass(): Class<FeatureDetailsViewModel> {
        return FeatureDetailsViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FeatureDetailsViewModel::class.java]
        viewModel.setApplicationLifecycle(application, this)
        numberList = intent.getStringArrayListExtra("list")!!
        initMvvm()
    }

    override fun onCreateView() {
        super.onCreateView()


        binding?.addonsBack?.setOnClickListener {
            super.onBackPressed()
        }
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
        binding?.etCallTrack?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0?.length!! > 2) {
                    binding?.ivCross?.visibility = View.GONE
                    binding?.btnSearch?.visibility = View.VISIBLE
                    binding?.btnSearch?.setOnClickListener {
                        updateAllItemBySearchValue(p0.toString())
                        binding?.btnSearch?.visibility = View.GONE

                    }
                    binding?.ivCross?.visibility = View.VISIBLE

                } else {
//                    totalFreeItemList?.let { updateFreeAddonsRecycler(it) }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding?.ivCross?.setOnClickListener {
            binding?.etCallTrack?.setText("")
            binding?.etCallTrack?.hint = "Search for a sequence of digits ..."
        }
        binding?.btnSearch?.setOnClickListener {
            updateAllItemBySearchValue(binding?.etCallTrack?.text.toString())
            binding?.btnSearch?.visibility = GONE
            binding?.ivCross?.visibility = VISIBLE
            binding?.etCallTrack?.setBackgroundResource(R.drawable.custom_domain_edit_text_bg)
        }

    }

    private fun initMvvm() {
        val recyclerview = findViewById<RecyclerView>(R.id.rv_number_list)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = NumberListAdapter(this, numberList, null,this)
        recyclerview.adapter = adapter
    }

    fun updateAllItemBySearchValue(searchValue: String) {
        var freeitemList: ArrayList<String> = arrayListOf()

        for (number in numberList!!) {
            if (number.lowercase()?.indexOf(searchValue.lowercase()) != -1) {
                freeitemList.add(number)
            }
        }
        val recyclerview = findViewById<RecyclerView>(R.id.rv_number_list)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = NumberListAdapter(this, numberList, searchValue,this)
        recyclerview.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onPackageClicked(item: Bundles?) {
    }

    override fun onPromoBannerClicked(item: PromoBanners?) {
    }

    override fun onShowHidePromoBannerIndicator(status: Boolean) {
    }

    override fun onPartnerZoneClicked(item: PartnerZone?) {
    }

    override fun onShowHidePartnerZoneIndicator(status: Boolean) {
    }

    override fun onAddFeatureDealItemToCart(item: FeaturesModel?, minMonth: Int) {
    }

    override fun onAddonsCategoryClicked(categoryType: String) {
    }

    override fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel) {
    }

    override fun onPackageAddToCart(item: Bundles?, image: ImageView) {
    }
}