package com.boost.marketplace.ui.details.call_track

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.Html
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
import com.boost.marketplace.Adapters.FreeAddonsAdapter
import com.boost.marketplace.R
import com.boost.marketplace.adapter.MatchNumberListAdapter
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


class CallTrackingActivity :
    AppBaseActivity<ActivityCallTrackingBinding, FeatureDetailsViewModel>(),
    HomeListener {
    lateinit var numberList: ArrayList<String>
    lateinit var numberListAdapter: NumberListAdapter
    lateinit var matchNumberListAdapter: MatchNumberListAdapter



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
        matchNumberListAdapter = MatchNumberListAdapter(this, ArrayList(),null,this)
        initMvvm()
    }

    override fun onCreateView() {
        super.onCreateView()

        numberListAdapter = NumberListAdapter(this, ArrayList(), null, this)
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
                        tv_available_no.text = "Search results"
                        binding?.btnSearch?.visibility = View.GONE

                    }
                    binding?.ivCross?.visibility = View.VISIBLE

                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding?.ivCross?.setOnClickListener {
            binding?.etCallTrack?.setText("")
            binding?.etCallTrack?.hint = "Search for a sequence of digits ..."
            initMvvm()
            binding?.tvSearchResult?.visibility = GONE
            binding?.rvNumberListRelated?.visibility = GONE
        }


    }

    private fun initMvvm() {
        val recyclerview = findViewById<RecyclerView>(R.id.rv_number_list)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = NumberListAdapter(this, numberList, null, this)
        recyclerview.adapter = adapter
    }

    private fun updateNumberList(list: ArrayList<String>,searchValue: String?) {
        numberListAdapter.addupdates(list)
        numberListAdapter = NumberListAdapter(this,list,searchValue,this)
        binding?.rvNumberList?.adapter = numberListAdapter
        numberListAdapter.notifyDataSetChanged()
    }

    private fun updateEveryNumberList(list: ArrayList<String>,searchValue: Char?) {
        matchNumberListAdapter.addupdates(list)
        matchNumberListAdapter = MatchNumberListAdapter(this,list,searchValue,this)
        binding?.rvNumberListRelated?.visibility = VISIBLE
        binding?.rvNumberListRelated?.adapter = matchNumberListAdapter
        numberListAdapter.notifyDataSetChanged()
    }

    fun updateAllItemBySearchValue(searchValue: String) {
        var exactMatchList: ArrayList<String> = arrayListOf()
        var everyMatchList: ArrayList<String> = arrayListOf()
        var isMatching : Boolean = false
        var searchChar:Char? = null

        for (number in numberList) {
            for (i in searchValue.indices){
                if(number.contains(searchValue[i])){
                    searchChar = searchValue[i]
                    everyMatchList.add(number)
                }
            }
//            if (number.contains(searchValue,true)) {
//                exactMatchList.add(number)
//            }
        }

//        for(number in numberList){
//            if(number.lowercase()?.indexOf(searchValue.lowercase()) != -1 ){
//                val index= number.lowercase()?.indexOf(searchValue.lowercase())
//                System.out.println("index" +index)
//                if (number.contains(searchValue)) {
//                    exactMatchList.add(number)
//                }else{
//                    everyMatchList.add(number)
//                }
//            }
//        }
//        updateNumberList(exactMatchList,searchValue)
        updateEveryNumberList(everyMatchList,searchChar)

        binding?.tvSearchResult?.visibility = VISIBLE
        binding?.tvSearchResult?.text = exactMatchList.size.toString()+" numbers found with "+"‘"+searchValue+"’"

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