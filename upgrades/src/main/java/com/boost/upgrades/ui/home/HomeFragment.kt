package com.boost.upgrades.ui.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.biz2.nowfloats.boost.updates.data.remote.ApiInterface
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.FeatureDealsAdapter
import com.boost.upgrades.adapter.PackageViewPagerAdapter
import com.boost.upgrades.adapter.SimplePageTransformer
import com.boost.upgrades.adapter.UpgradeAdapter
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.api_model.GetAllFeatures.response.FeatureDeals
import com.boost.upgrades.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.upgrades.data.api_model.GetAllFeatures.response.PrimaryImage
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.model.WidgetModel
import com.boost.upgrades.database.LocalStorage
import com.boost.upgrades.interfaces.HomeListener
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.ui.details.DetailsFragment
import com.boost.upgrades.ui.features.ViewAllFeaturesFragment
import com.boost.upgrades.ui.myaddons.MyAddonsFragment
import com.boost.upgrades.ui.packages.PackageFragment
import com.boost.upgrades.ui.webview.WebViewFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.CART_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.MYADDONS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.PACKAGE_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.VIEW_ALL_FEATURE
import com.boost.upgrades.utils.HorizontalMarginItemDecoration
import com.boost.upgrades.utils.Utils.getRetrofit
import com.boost.upgrades.utils.Utils.longToast
import com.boost.upgrades.utils.WebEngageController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.home_fragment.*
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment(), HomeListener {

    lateinit var root: View
    private lateinit var viewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory

    lateinit var retrofit: Retrofit
    lateinit var ApiService: ApiInterface
    lateinit var localStorage: LocalStorage

    lateinit var upgradeAdapter: UpgradeAdapter

    lateinit var progressDialog: ProgressDialog

    lateinit var packageViewPagerAdapter: PackageViewPagerAdapter
    lateinit var featureDealsAdapter: FeatureDealsAdapter

    var cart_list: List<WidgetModel>? = null
    var badgeNumber = 0
    var fpRefferalCode: String = ""
//    val compositeDisposable = CompositeDisposable()

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.home_fragment, container, false)

        homeViewModelFactory = HomeViewModelFactory(requireNotNull(requireActivity().application))

        viewModel = ViewModelProviders.of(requireActivity(), homeViewModelFactory)
                .get(HomeViewModel::class.java)

        upgradeAdapter = UpgradeAdapter((activity as UpgradeActivity), ArrayList())
        packageViewPagerAdapter = PackageViewPagerAdapter(ArrayList(), (activity as UpgradeActivity), this)
        featureDealsAdapter = FeatureDealsAdapter(ArrayList(), ArrayList(), (activity as UpgradeActivity), this)

        //request retrofit instance
        ApiService = getRetrofit()
                .create(ApiInterface::class.java)

        progressDialog = ProgressDialog(requireContext())

        localStorage = LocalStorage.getInstance(context!!)!!

        cart_list = localStorage.getCartItems()

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setSpannableStrings()
        loadData()
        initMvvm()

        WebEngageController.trackEvent("ADDONS_MARKETPLACE Loaded", "ADDONS_MARKETPLACE", "")

        shimmer_view_container.startShimmer()


//        Glide.with(this).load(R.drawable.back_beau)
//                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
//                .into(back_image)

        imageView21.setOnClickListener {
            (activity as UpgradeActivity).finish()
        }



        imageViewCart1.setOnClickListener {
            (activity as UpgradeActivity).addFragment(CartFragment.newInstance(), CART_FRAGMENT)
        }

        initializePackageViewPager()
        initializeFeatureDeals()
        initializeRecycler()

        share_refferal_code_btn.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE REFFER_BOOST CLICKED", "Generic", "")
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent
                    .putExtra(
                            Intent.EXTRA_TEXT,
                            getString(R.string.referral_text_1) + fpRefferalCode
                    )
            sendIntent.type = "text/plain"
            try {
                startActivity(sendIntent)
            } catch (ex: ActivityNotFoundException) {
                Toasty.error(
                        requireContext(),
                        "Failed to share the referral code. Please try again.",
                        Toast.LENGTH_SHORT,
                        true
                ).show();

            }
        }

        share_fb_1.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE REFFER_BOOST CLICKED", "Facebook Messenger", "")
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent
                    .putExtra(
                            Intent.EXTRA_TEXT,
                            getString(R.string.referral_text_2) + fpRefferalCode
                    )
            sendIntent.type = "text/plain"
            sendIntent.setPackage("com.facebook.orca")
            try {
                startActivity(sendIntent)
            } catch (ex: ActivityNotFoundException) {

                Toasty.error(
                        requireContext(),
                        "Please Install Facebook Messenger",
                        Toast.LENGTH_SHORT,
                        true
                ).show();

            }
        }
        share_whatsapp_1.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE REFFER_BOOST CLICKED", "WHATSAPP", "")
            val whatsappIntent = Intent(Intent.ACTION_SEND)
            whatsappIntent.type = "text/plain"
            whatsappIntent.setPackage("com.whatsapp")
            whatsappIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.referral_text_1) + fpRefferalCode
            )
            try {
                Objects.requireNonNull(this).startActivity(whatsappIntent)
            } catch (ex: ActivityNotFoundException) {
                startActivity(
                        Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=com.whatsapp")
                        )
                )
            }

        }
        share_referal.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE REFFER_BOOST CLICKED", "CARD", "")
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                    Intent.EXTRA_TEXT,
                    R.string.referral_text_2
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

//        if(::localStorage.isInitialized) {
//            cart_list = localStorage.getCartItems()
//            if (cart_list != null) {
//                badgeNumber = cart_list!!.size
//                badge.setText(badgeNumber.toString())
//                Constants.CART_VALUE = badgeNumber
//            }
//        }


        view_my_addons.setOnClickListener {
            (activity as UpgradeActivity).addFragment(
                    MyAddonsFragment.newInstance(),
                    MYADDONS_FRAGMENT
            )
        }

        all_recommended_addons.setOnClickListener {
            (activity as UpgradeActivity).addFragment(
                    ViewAllFeaturesFragment.newInstance(),
                    VIEW_ALL_FEATURE
            )
        }


    }

    override fun onResume() {
        super.onResume()
//        val pos = 2
//        package_viewpager.postDelayed(Runnable { package_viewpager.setCurrentItem(pos) }, 100)
    }

    fun setSpannableStrings() {
        var fpId = (activity as UpgradeActivity)!!.fpid
        if (fpId != null) {
            val minLength = 5

            fpRefferalCode = ""
            for (c in fpId) {
                fpRefferalCode += (c + 1).toString().trim().toUpperCase()

                if (fpRefferalCode.length >= minLength)
                    break;
            }

            var lengthDiff = minLength - (fpRefferalCode.length % 5)

            while (lengthDiff-- > 0) {
                fpRefferalCode += lengthDiff.toString()
            }
        }
        fpRefferalCode = (activity as UpgradeActivity)!!.experienceCode + fpRefferalCode
        referral_code.setText(fpRefferalCode)


        val referralText = SpannableString(getString(R.string.upgrade_boost_tnc_link))
        referralText.setSpan(UnderlineSpan(), 0, referralText.length, 0)
        boost360_tnc.setText(referralText)
        boost360_tnc.setOnClickListener {
            WebEngageController.trackEvent("ADDONS_MARKETPLACE TnC Clicked", "ADDONS_MARKETPLACE", "")
            val webViewFragment: WebViewFragment = WebViewFragment.newInstance()
            val args = Bundle()
            args.putString("link", "https://www.getboost360.com/tnc")
            webViewFragment.arguments = args
            (activity as UpgradeActivity).addFragment(
                    webViewFragment,
                    Constants.WEB_VIEW_FRAGMENT
            )
        }

        val refText = SpannableString(getString(R.string.referral_card_explainer_text))
        refText.setSpan(StyleSpan(Typeface.BOLD), 18, 26, 0)
        refText.setSpan(StyleSpan(Typeface.BOLD), refText.length - 4, refText.length, 0)
        refText.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.common_text_color)),
                0,
                refText.length,
                0
        )
        ref_txt.setText(refText)
    }

    fun loadData() {
        val pref: SharedPreferences = (activity as UpgradeActivity).getSharedPreferences("nowfloatsPrefs", 0);
        var code: String = (activity as UpgradeActivity).experienceCode!!;
        if (!code.equals("null", true)) {
            viewModel.setCurrentExperienceCode(code, pref.getString("GET_FP_DETAILS_TAG", ""))
        }

        viewModel.loadUpdates((activity as UpgradeActivity).fpid!!, (activity as UpgradeActivity).clientid)
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun initMvvm() {

        viewModel.updatesError().observe(this, androidx.lifecycle.Observer {
            //            Snackbar.make(root, viewModel.errorMessage, Snackbar.LENGTH_LONG).show()
//            if (shimmer_view_container.isAnimationStarted) {
//                shimmer_view_container.stopShimmerAnimation()
//                shimmer_view_container.visibility = View.GONE
//            }
            longToast(requireContext(), "onFailure: " + it)
        })

//        viewModel.upgradeResult().observe(this, androidx.lifecycle.Observer {
//            updateRecycler(it)
//        })

        viewModel.getAllAvailableFeatures().observe(this, androidx.lifecycle.Observer {
            updateRecycler(it)
            recommended_features_section_subtitle.setText("Add these " + it.count() + " add-ons, recommended for your business.")
        })

        viewModel.getAllBundles().observe(this, androidx.lifecycle.Observer {
            val list = arrayListOf<Bundles>()
            for (item in it) {
                val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                list.add(Bundles(
                        item.bundle_key,
                        temp,
                        item.min_purchase_months,
                        item.name,
                        item.overall_discount_percent,
                        PrimaryImage(item.primary_image),
                        null,
                        null,
                        item.target_business_usecase
                ))
            }
            if (list.size > 0)
                updatePackageViewPager(list)
        })

        viewModel.getAllFeatureDeals().observe(this, androidx.lifecycle.Observer {
            if (it.size > 0) {
                var cartItems: List<CartModel> = arrayListOf()
                if (viewModel.cartResult.value != null) {
                    cartItems = viewModel.cartResult.value!!
                }
                updateFeatureDealsViewPager(it, cartItems)
            }
        })

        viewModel.getTotalActiveWidgetCount().observe(this, androidx.lifecycle.Observer {
            total_active_widget_count.setText(it.toString())
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
            if (it != null && it.size > 0) {
                badge.visibility = View.VISIBLE
                badgeNumber = it.size
                badge.setText(badgeNumber.toString())
                Constants.CART_VALUE = badgeNumber
            } else {
                badgeNumber = 0
                badge.visibility = View.GONE
            }
            //refresh FeatureDeals adaptor when cart is updated
            if (viewModel.allFeatureDealsResult.value != null) {
                val list = viewModel.allFeatureDealsResult.value!!
                if (list.size > 0) {
                    updateFeatureDealsViewPager(list, it)
                }
            }
        })
    }

    fun updateRecycler(list: List<FeaturesModel>) {
        if (shimmer_view_container.isShimmerStarted) {
            shimmer_view_container.stopShimmer()
            shimmer_view_container.visibility = View.GONE
        }
        upgradeAdapter.addupdates(list)
        recycler.adapter = upgradeAdapter
        upgradeAdapter.notifyDataSetChanged()
        recycler.setFocusable(false)
        back_image.setFocusable(true)
    }

    private fun initializeRecycler() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recycler.apply {
            layoutManager = gridLayoutManager
        }
    }

    fun updatePackageViewPager(list: List<Bundles>) {
        package_layout.visibility = View.VISIBLE
        package_viewpager.offscreenPageLimit = list.size
        packageViewPagerAdapter.addupdates(list)
        packageViewPagerAdapter.notifyDataSetChanged()
        //show dot indicator only when the (list.size > 2)
        if (list.size > 1) {
            dots_indicator.visibility = View.VISIBLE
        } else {
            dots_indicator.visibility = View.INVISIBLE
        }
    }

    fun updateFeatureDealsViewPager(list: List<FeatureDeals>, cartList: List<CartModel>) {
        feature_deals_layout.visibility = View.VISIBLE
        feature_deals_viewpager.offscreenPageLimit = if (list.size > 0) list.size else 1
        featureDealsAdapter.addupdates(list, cartList)
        featureDealsAdapter.notifyDataSetChanged()
        if (list.size > 1) {
            feature_deals_indicator.visibility = View.VISIBLE
        } else {
            feature_deals_indicator.visibility = View.INVISIBLE
        }
    }

    private fun initializePackageViewPager() {
        package_viewpager.adapter = packageViewPagerAdapter
        dots_indicator.setViewPager2(package_viewpager)

        package_viewpager.setPageTransformer(SimplePageTransformer())

        val itemDecoration = HorizontalMarginItemDecoration(
                requireContext(),
                R.dimen.viewpager_current_item_horizontal_margin
        )
        package_viewpager.addItemDecoration(itemDecoration)

    }

    private fun initializeFeatureDeals() {
        feature_deals_viewpager.adapter = featureDealsAdapter
        feature_deals_indicator.setViewPager2(feature_deals_viewpager)

        feature_deals_viewpager.setPageTransformer(SimplePageTransformer())

        val itemDecoration = HorizontalMarginItemDecoration(
                requireContext(),
                R.dimen.viewpager_current_item_horizontal_margin
        )
        feature_deals_viewpager.addItemDecoration(itemDecoration)

    }

    override fun onBackPressed() {
        if (::viewModel.isInitialized) {
            viewModel.getCartItems()
        }
    }

    override fun onPackageClicked(item: Bundles?) {
        val packageFragment = PackageFragment.newInstance()
        val args = Bundle()
        args.putString("bundleData", Gson().toJson(item))
        packageFragment.arguments = args
        (activity as UpgradeActivity).addFragment(packageFragment, PACKAGE_FRAGMENT)
    }

    override fun onAddFeatureDealItemToCart(item: FeaturesModel?, minMonth: Int) {
        if (item != null)
            viewModel.addItemToCart(item, minMonth)
    }

}
