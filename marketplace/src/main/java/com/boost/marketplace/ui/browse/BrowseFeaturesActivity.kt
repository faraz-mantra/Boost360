package com.boost.marketplace.ui.browse

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Utils
import com.boost.marketplace.R
import com.boost.marketplace.adapter.BrowseParentFeaturesAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityBrowseFeaturesBinding
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.ui.details.FeatureDetailsActivity
import com.framework.analytics.SentryController
import kotlinx.android.synthetic.main.activity_browse_features.*

class BrowseFeaturesActivity :
    AppBaseActivity<ActivityBrowseFeaturesBinding, BrowseFeaturesViewModel>(),AddonsListener {


    var singleWidgetKey: String? = null
    var badgeNumber = 0
    var addonDetails: FeaturesModel? = null
    var cart_list: List<CartModel>? = null
    var itemInCartStatus = false
    var widgetLearnMoreLink: String? = null

    var experienceCode: String? = null
    var fpid: String? = null
    var email: String? = null
    var mobileNo: String? = null
    var profileUrl: String? = null
    var accountType: String? = null
    var isDeepLink: Boolean = false
    var isOpenCardFragment: Boolean = false

    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7





    lateinit var adapter: BrowseParentFeaturesAdapter
    lateinit var progressDialog: ProgressDialog
    var userPurchsedWidgets = ArrayList<String>()
    var categoryType = String()

    override fun getLayout(): Int {
        return R.layout.activity_browse_features
    }

    override fun getViewModelClass(): Class<BrowseFeaturesViewModel> {
        return BrowseFeaturesViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        deepLinkViewType = intent.getStringExtra("deepLinkViewType") ?: ""
        deepLinkDay = intent.getStringExtra("deepLinkDay")?.toIntOrNull() ?: 7
        experienceCode = intent.getStringExtra("expCode")
        fpid = intent.getStringExtra("fpid")
        email = intent.getStringExtra("email")
        mobileNo = intent.getStringExtra("mobileNo")
        profileUrl = intent.getStringExtra("profileUrl")
        accountType = intent.getStringExtra("accountType")
        isOpenCardFragment = intent.getBooleanExtra("isOpenCardFragment", false)
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: java.util.ArrayList()


        progressDialog = ProgressDialog(this)

        adapter = BrowseParentFeaturesAdapter(arrayListOf(), this,this)
        viewModel.setApplicationLifecycle(application,this)
        categoryType = intent.getStringExtra("categoryType") ?: ""
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()
        try {
            viewModel.loadAllFeaturesfromDB()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
        initMvvm()
        initializeAddonCategoryRecycler()
        binding?.browseSearch?.setOnClickListener {
            val intent= Intent(this,SearchActivity::class.java)
            intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
            startActivity(intent)
        }
        binding?.browseFeaturesBack?.setOnClickListener {
            finish()
        }
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

    override fun onAddonsClicked(item: FeaturesModel) {
        val intent = Intent(this, FeatureDetailsActivity::class.java)

        intent.putExtra("fpid", fpid)
        intent.putExtra("expCode", experienceCode)
        intent.putExtra("isDeepLink", isDeepLink)
        intent.putExtra("deepLinkViewType", deepLinkViewType)
        intent.putExtra("deepLinkDay", deepLinkDay)
        intent.putExtra("isOpenCardFragment", isOpenCardFragment)
        intent.putExtra(
            "accountType",
            accountType
        )
        intent.putStringArrayListExtra(
            "userPurchsedWidgets",
            userPurchsedWidgets
        )
        if (email != null) {
            intent.putExtra("email", email)
        } else {
            intent.putExtra("email", "ria@nowfloats.com")
        }
        if (mobileNo != null) {
            intent.putExtra("mobileNo", mobileNo)
        } else {
            intent.putExtra("mobileNo", "9160004303")
        }
        intent.putExtra("profileUrl", profileUrl)
        intent.putExtra("itemId", item.feature_code)

//                                                            startActivity(intent)


//                intent.putExtra("itemId", it.feature_code)
//                startActivity(intent)
        // intent.putExtra("itemId", item!!.cta_feature_key)
        startActivity(intent)
    }


}