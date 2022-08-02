package com.boost.marketplace.ui.pack_details

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.CartActivity
import com.boost.cart.adapter.SimplePageTransformerSmall
import com.boost.cart.adapter.ZoomOutPageTransformer
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.HorizontalMarginItemDecoration
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.R
import com.boost.marketplace.adapter.*
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityPackDetailsBinding
import com.boost.marketplace.interfaces.DetailsFragmentListener
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel
import com.bumptech.glide.Glide
import com.framework.webengageconstant.ADDONS_MARKETPLACE_COMPARE_PACKAGE_LOADED
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_pack_details.*

class PackDetailsActivity : AppBaseActivity<ActivityPackDetailsBinding, ComparePacksViewModel>(),
    DetailsFragmentListener {

    var experienceCode: String? = null
    var screenType: String? = null
    var fpName: String? = null
    var fpid: String? = null
    var fpTag: String? = null
    var email: String? = null
    var mobileNo: String? = null
    var profileUrl: String? = null
    var accountType: String? = null
    var isDeepLink: Boolean = false
    var isOpenCardFragment: Boolean = false
    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7
    var userPurchsedWidgets = ArrayList<String>()
    private var widgetFeatureCode: String? = null
    var isOpenHomeFragment: Boolean = false
    var isOpenAddOnsFragment: Boolean = false
    var refreshViewPager: Boolean = false


    var bundleData: Bundles? = null
    var featuresList: List<FeaturesModel>? = null
    var cartList: List<CartModel>? = null

    var badgeNumber = 0
    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0
    var featureCount = 0
    var cartCount = 0

    var packageInCartStatus = false
    lateinit var prefs: SharedPrefs
    var featuresHashMap: MutableMap<String?, FeaturesModel> = HashMap<String?, FeaturesModel>()
    var upgradeList = arrayListOf<Bundles>()
    lateinit var progressDialog: ProgressDialog
    lateinit var howToUseAdapter: HowToActivateAdapter
    lateinit var faqAdapter: PackDetailsFaqAdapter
    lateinit var benefitAdaptor: PackDetailsBenefitViewPagerAdapter
    lateinit var reviewAdaptor: TestimonialItemsAdapter
    lateinit var includedFeatureAdapter: IncludedFeatureAdapter


    companion object {
        fun newInstance() = PackDetailsActivity()
    }

    override fun getLayout(): Int {
        return R.layout.activity_pack_details
    }

    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        deepLinkViewType = intent.getStringExtra("deepLinkViewType") ?: ""
        deepLinkDay = intent.getStringExtra("deepLinkDay")?.toIntOrNull() ?: 7

        experienceCode = intent.getStringExtra("expCode")
        screenType = intent.getStringExtra("screenType")
        fpName = intent.getStringExtra("fpName")
        fpid = intent.getStringExtra("fpid")
        fpTag = intent.getStringExtra("fpTag")
        email = intent.getStringExtra("email")
        mobileNo = intent.getStringExtra("mobileNo")
        profileUrl = intent.getStringExtra("profileUrl")
        accountType = intent.getStringExtra("accountType")
        isOpenCardFragment = intent.getBooleanExtra("isOpenCardFragment", false)
        isOpenHomeFragment = intent.getBooleanExtra("isComingFromOrderConfirm", false)
        isOpenAddOnsFragment = intent.getBooleanExtra("isComingFromOrderConfirmActivation", false)
        widgetFeatureCode = intent.getStringExtra("buyItemKey")
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()
        bundleData = Gson().fromJson<Bundles>(
            intent.getStringExtra("bundleData"),
            object : TypeToken<Bundles>() {}.type
        )

        reviewAdaptor = TestimonialItemsAdapter(ArrayList())
        howToUseAdapter = HowToActivateAdapter(this, ArrayList())
        faqAdapter = PackDetailsFaqAdapter(this, ArrayList())
        benefitAdaptor = PackDetailsBenefitViewPagerAdapter(ArrayList(), this)
        includedFeatureAdapter = IncludedFeatureAdapter(this,ArrayList())

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        WebEngageController.trackEvent(
            ADDONS_MARKETPLACE_COMPARE_PACKAGE_LOADED,
            PAGE_VIEW,
            NO_EVENT_VALUE
        )
        prefs = SharedPrefs(this)

        viewModel = ViewModelProviders.of(this).get(ComparePacksViewModel::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }


        initializeViewPager()
        initializeCustomerViewPager()
        initializeHowToUseRecycler()
        initializeFAQRecycler()
        initializeIncludedFeature()
        initMvvm()

        binding?.back?.setOnClickListener {
            onBackPressed()
        }

        binding?.imageViewCart121?.setOnClickListener {
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

    private fun initializeIncludedFeature() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvIncludedFeatures?.apply {
            layoutManager = gridLayoutManager
            binding?.rvIncludedFeatures?.adapter = includedFeatureAdapter
        }
    }


    private fun initializeHowToUseRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.howToUseRecycler?.apply {
            layoutManager = gridLayoutManager
            binding?.howToUseRecycler?.adapter = howToUseAdapter
        }
    }

    private fun initializeFAQRecycler() {
        val gridLayoutManager = GridLayoutManager(applicationContext, 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.faqRecycler?.apply {
            layoutManager = gridLayoutManager
            binding!!.faqRecycler.adapter = faqAdapter
        }
    }

    private fun initializeViewPager() {
        binding?.benefitsViewpager?.adapter = benefitAdaptor
        binding?.benefitsIndicator?.setViewPager2(binding?.benefitsViewpager!!)
        binding?.benefitsViewpager?.offscreenPageLimit = 1

        binding?.benefitsViewpager?.setPageTransformer(SimplePageTransformerSmall())

        val itemDecoration = HorizontalMarginItemDecoration(
            applicationContext,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        binding?.benefitsViewpager?.addItemDecoration(itemDecoration)
    }


    private fun initializeCustomerViewPager() {
        binding?.whatOurCustomerViewpager?.adapter = reviewAdaptor
        binding?.whatOurCustomerIndicator?.setViewPager2(binding?.whatOurCustomerViewpager!!)
        binding?.whatOurCustomerViewpager?.offscreenPageLimit = 1

        binding?.whatOurCustomerViewpager?.setPageTransformer(ZoomOutPageTransformer())

        val itemDecoration = HorizontalMarginItemDecoration(
            applicationContext,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        binding?.whatOurCustomerViewpager?.addItemDecoration(itemDecoration)
    }


    private fun initMvvm() {
        if (bundleData?.name?.contains("Online")!!) {
            Glide.with(binding?.packageImg!!).load(bundleData?.primary_image!!.url)
                .into(binding?.packageImg!!)
            binding?.title?.text = bundleData?.name
            if(bundleData !=null){
                if(bundleData?.included_features != null){

                }
            }
            if (bundleData?.benefits != null && bundleData?.benefits?.isNotEmpty()!!) {
                binding?.containerKeyBenefits?.visibility = View.VISIBLE
                benefitAdaptor.addupdates(bundleData?.benefits!!)
                benefitAdaptor.notifyDataSetChanged()
            }else{
                binding?.containerKeyBenefits?.visibility = View.GONE
            }

            if (bundleData != null && bundleData?.how_to_activate != null && bundleData?.how_to_activate?.isNotEmpty()!!) {
                binding?.howToUseContainer?.visibility = View.VISIBLE
                binding?.howToUseContainer?.setOnClickListener {
                    if (binding?.howToUseRecycler?.visibility == View.VISIBLE) {
                        how_to_use_arrow.setImageResource(R.drawable.ic_arrow_down_gray)
                        how_to_use_recycler.visibility = View.GONE
                    } else {
                        how_to_use_arrow.setImageResource(R.drawable.ic_arrow_up_gray)
                        how_to_use_recycler.visibility = View.VISIBLE
                    }
                }
                if (bundleData!!.target_business_usecase != null) {
                    tv_how_to_use_title.text =
                        "How To Use " + bundleData!!.target_business_usecase
                }
                val steps =
                    bundleData?.how_to_activate
                howToUseAdapter.addupdates(steps!!)
                howToUseAdapter.notifyDataSetChanged()
            } else {
                binding?.howToUseContainer?.visibility = View.GONE

            }
            if (bundleData != null && bundleData?.testimonials != null && bundleData?.testimonials?.isNotEmpty()!!) {
                binding?.whatOurCustomerContainer?.visibility = View.VISIBLE
                reviewAdaptor.addupdates(bundleData?.testimonials!!)
                benefitAdaptor.notifyDataSetChanged()
            } else {
                binding?.whatOurCustomerContainer?.visibility = View.GONE

            }
            if (bundleData != null && bundleData?.frequently_asked_questions != null && bundleData?.frequently_asked_questions?.isNotEmpty()!! ) {
                faq_container.visibility = View.VISIBLE
                val faq =bundleData?.frequently_asked_questions!!
                faqAdapter.addupdates(faq)
                faqAdapter.notifyDataSetChanged()
            }else{
                faq_container.visibility = View.GONE

            }
        }


    }

    override fun imagePreviewPosition(list: ArrayList<String>, pos: Int) {
        TODO("Not yet implemented")
    }

    override fun onPackageClicked(item: Bundles?) {
        TODO("Not yet implemented")
    }

    override fun itemAddedToCart(status: Boolean) {
        TODO("Not yet implemented")
    }

    override fun goToCart() {
        TODO("Not yet implemented")
    }
}