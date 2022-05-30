package com.boost.marketplace.ui.browse

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.boost.cart.CartActivity
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Constants
import com.boost.dbcenterapi.utils.Utils
import com.boost.marketplace.R
import com.boost.marketplace.adapter.BrowseParentFeaturesAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityBrowseFeaturesBinding
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CategorySelectorListener
import com.boost.marketplace.ui.details.FeatureDetailsActivity
import com.framework.analytics.SentryController
import kotlinx.android.synthetic.main.activity_browse_features.*
import kotlinx.android.synthetic.main.activity_marketplace.*

class BrowseFeaturesActivity :
    AppBaseActivity<ActivityBrowseFeaturesBinding, BrowseFeaturesViewModel>(),
    AddonsListener, CategorySelectorListener {

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
    lateinit var categorySelectorBottomSheet: CategorySelectorBottomSheet

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
        deepLinkDay = intent.getIntExtra("deepLinkDay", 7)
        experienceCode = intent.getStringExtra("expCode")
        fpid = intent.getStringExtra("fpid")
        email = intent.getStringExtra("email")
        mobileNo = intent.getStringExtra("mobileNo")
        profileUrl = intent.getStringExtra("profileUrl")
        accountType = intent.getStringExtra("accountType")
        isOpenCardFragment = intent.getBooleanExtra("isOpenCardFragment", false)
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: java.util.ArrayList()


        progressDialog = ProgressDialog(this)
        categorySelectorBottomSheet = CategorySelectorBottomSheet(this, this)

        adapter = BrowseParentFeaturesAdapter(arrayListOf(), this,this)
        viewModel.setApplicationLifecycle(application,this)
        categoryType = intent.getStringExtra("categoryType") ?: ""
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()
        try {
            viewModel.loadAllFeaturesfromDB()
            viewModel.getCategoriesFromAssetJson(this, experienceCode)
        } catch (e: Exception) {
            SentryController.captureException(e)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }

        initMvvm()
        initializeAddonCategoryRecycler()
        binding?.browseSearch?.setOnClickListener {
            val intent= Intent(this,SearchActivity::class.java)
            intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
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
            startActivity(intent)
        }
        binding?.browseFeaturesBack?.setOnClickListener {
            finish()
        }
        binding?.options?.setOnClickListener {
            categorySelectorBottomSheet.show(this.supportFragmentManager, "CATEGORY_SELECTOR")
        }
        binding?.browseCart?.setOnClickListener {
            val intent = Intent(
                applicationContext,
                CartActivity::class.java
            )
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
            startActivity(intent)
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

        viewModel.cartResult().observe(this, androidx.lifecycle.Observer {
            cart_list = it
            itemInCartStatus = false
            if (cart_list != null && cart_list!!.size > 0) {
                badge121.visibility = View.VISIBLE
                for (item in cart_list!!) {
                    if (item.feature_code == singleWidgetKey) {
                        itemInCartStatus = true
                        break
                    }
                }
                badgeNumber = cart_list!!.size
                badge121.setText(badgeNumber.toString())
                Constants.CART_VALUE = badgeNumber
            } else {
                badgeNumber = 0
                badge121.visibility = View.GONE
                itemInCartStatus = false
            }
        })

        viewModel.categoryResult().observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                adapter.updateAccountType(it.lowercase())
                adapter.notifyDataSetChanged()
                desc.text = "Features that "+it.lowercase()+" are liking most."
            }

        })
    }


    private fun initializeAddonCategoryRecycler() {
//        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
//        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
//        browse_features_rv.apply {
//            layoutManager = gridLayoutManager
//        }
        browse_features_rv.adapter = adapter
    }


    fun updateAddonCategoryRecycler(list: List<FeaturesModel>) {
        val addonsCategoryTypes = arrayListOf<String>()
        addonsCategoryTypes.add("Trending")
        for (singleFeaturesModel in list) {
            if (singleFeaturesModel.target_business_usecase != null && !addonsCategoryTypes.contains(
                    singleFeaturesModel.target_business_usecase
                )
            ) {
                if(categoryType.isEmpty() || categoryType.equals(singleFeaturesModel.target_business_usecase)) {
                    addonsCategoryTypes.add(singleFeaturesModel.target_business_usecase!!)
                }
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
        adapter.notifyDataSetChanged()
        browse_features_rv.isFocusable = false
//        back_image.isFocusable = true
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCartItems()
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
        startActivity(intent)
    }

    override fun onCategoryClicked(item: String) {
        categorySelectorBottomSheet.dismiss()
        trend_title.setText(item)
        val addonsCategoryTypes = arrayListOf<String>()
        addonsCategoryTypes.add(item)
        adapter.addupdates(addonsCategoryTypes)
        adapter.notifyDataSetChanged()
        browse_features_rv.isFocusable = false
    }


}