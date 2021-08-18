package com.marketplace.ui.home

import android.util.Log
import android.view.View
import com.framework.base.BaseResponse
import com.framework.extensions.observeOnce
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.marketplace.R
import com.marketplace.base.AppBaseFragment
import com.marketplace.constant.RecyclerViewItemType
import com.marketplace.databinding.FragmentMarketplaceHomeBinding
import com.marketplace.model.features.FeatureResponse
import com.marketplace.model.features.PromoBanner
import com.marketplace.recyclerView.AppBaseRecyclerViewAdapter
import com.marketplace.recyclerView.BaseRecyclerViewItem
import com.marketplace.recyclerView.RecyclerItemClickListener
import com.marketplace.rest.TaskCode
import com.marketplace.viewmodel.MarketPlaceHomeViewModel
import kotlinx.android.synthetic.main.fragment_marketplace_home.*

class MarketPlaceHomeFragment : AppBaseFragment<FragmentMarketplaceHomeBinding, MarketPlaceHomeViewModel>(), RecyclerItemClickListener {


    private var adapterPromoBanner: AppBaseRecyclerViewAdapter<PromoBanner>? = null
    private var list: ArrayList<PromoBanner>? = null

    override fun getLayout(): Int {
        return R.layout.fragment_marketplace_home
    }

    override fun getViewModelClass(): Class<MarketPlaceHomeViewModel> {
        return MarketPlaceHomeViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
//        hitApi(
//            viewModel?.getAllFeatures(),""
//        )
        viewModel?.getAllFeatures()?.observe(viewLifecycleOwner, {
            val response = it as? FeatureResponse
            Log.v("Api", response.toString())

            if (response != null) {
                if (response.data?.get(0)?.promoBanners != null && response.data!![0].promoBanners?.size!! > 0) {

                    response.data!![0].promoBanners?.let { it1 -> list?.addAll(it1) }

                    if(mp_shimmer_view_banner.isShimmerStarted){
                        mp_shimmer_view_banner.stopShimmer()
                        mp_shimmer_view_banner.visibility = View.GONE
                        list?.let { setPromoBanner(it) }

                    }


                }
            }
        })


    }

    override fun onSuccess(it: BaseResponse) {
        super.onSuccess(it)
        when (it.taskcode) {
            TaskCode.GET_ALL_FEATURES.ordinal -> {
                val response = it as? FeatureResponse
            }
        }
    }

    override fun onFailure(it: BaseResponse) {
        super.onFailure(it)
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        TODO("Not yet implemented")
    }


    private fun setPromoBanner(promoBannerFilter: ArrayList<PromoBanner>) {
        binding?.mpBannerLayout?.visibility = View.VISIBLE
        binding?.mpBannerViewpager?.apply {
            if (promoBannerFilter.isNotEmpty()) {
                promoBannerFilter.map {
                    it.recyclerViewItemType = RecyclerViewItemType.PROMO_BANNER_ITEM_VIEW.getLayout()
                }
                if (adapterPromoBanner == null) {
                    adapterPromoBanner =
                        AppBaseRecyclerViewAdapter(baseActivity, promoBannerFilter, this@MarketPlaceHomeFragment)
                    offscreenPageLimit = 3
                    adapter = adapterPromoBanner
                    binding?.mpBannerDotIndicator?.setViewPager2(this)
                    setPageTransformer { page, position ->
                        OffsetPageTransformer().transformPage(page, position)
                    }
                } else adapterPromoBanner?.notify(promoBannerFilter)
            }
        }
    }


}