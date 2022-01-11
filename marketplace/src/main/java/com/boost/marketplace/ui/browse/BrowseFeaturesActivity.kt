package com.boost.marketplace.ui.browse

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Utils
import com.boost.marketplace.R
import com.boost.marketplace.adapter.BrowseParentFeaturesAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityBrowseFeaturesBinding
import com.boost.marketplace.ui.home.MarketPlaceHomeViewModel
import com.framework.analytics.SentryController
import kotlinx.android.synthetic.main.activity_browse_features.*
import kotlinx.android.synthetic.main.activity_marketplace.*

class BrowseFeaturesActivity :
    AppBaseActivity<ActivityBrowseFeaturesBinding, BrowseFeaturesViewModel>() {

    lateinit var adapter: BrowseParentFeaturesAdapter
    lateinit var progressDialog: ProgressDialog

    override fun getLayout(): Int {
        return R.layout.activity_browse_features
    }

    override fun getViewModelClass(): Class<BrowseFeaturesViewModel> {
        return BrowseFeaturesViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        progressDialog = ProgressDialog(this)

        adapter = BrowseParentFeaturesAdapter(arrayListOf(), this)
        viewModel.setApplicationLifecycle(application,this)
        try {
            viewModel.loadAllFeaturesfromDB()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
        initMvvm()
        initializeAddonCategoryRecycler()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initMvvm() {
        viewModel.updatesError().observe(this, androidx.lifecycle.Observer {
            Utils.longToast(applicationContext, "onFailure: " + it)
        })

        viewModel.getAllAvailableFeatures().observe(this, androidx.lifecycle.Observer {
            updateAddonCategoryRecycler(it)
        })

        viewModel.updatesLoader().observe(this, androidx.lifecycle.Observer {
            if (it) {
                val status = "Loading. Please wait..."
                progressDialog.setMessage(status)
                progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })
    }


    private fun initializeAddonCategoryRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        browse_features_rv.apply {
            layoutManager = gridLayoutManager
            browse_features_rv.adapter = adapter
        }
    }


    fun updateAddonCategoryRecycler(list: List<FeaturesModel>) {
        val addonsCategoryTypes = arrayListOf<String>()
        for (singleFeaturesModel in list) {
            if (singleFeaturesModel.target_business_usecase != null && !addonsCategoryTypes.contains(
                    singleFeaturesModel.target_business_usecase
                )
            ) {
                addonsCategoryTypes.add(singleFeaturesModel.target_business_usecase!!)
            }
        }

//        if (shimmer_view_recomm_addons.isShimmerStarted) {
//            shimmer_view_recomm_addons.stopShimmer()
//            shimmer_view_recomm_addons.visibility = View.GONE
//        }
//        if (shimmer_view_addon_category.isShimmerStarted) {
//            shimmer_view_addon_category.stopShimmer()
//            shimmer_view_addon_category.visibility = View.GONE
//        }
        adapter.addupdates(addonsCategoryTypes)
        browse_features_rv.adapter = adapter
        adapter.notifyDataSetChanged()
        browse_features_rv.isFocusable = false
//        back_image.isFocusable = true
    }


}