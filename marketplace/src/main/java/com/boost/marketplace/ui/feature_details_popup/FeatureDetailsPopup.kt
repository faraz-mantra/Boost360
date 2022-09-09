package com.boost.marketplace.ui.feature_details_popup

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import com.boost.cart.CartActivity
import androidx.core.content.ContextCompat
import com.boost.cart.utils.Utils
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Constants
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.R
import com.boost.marketplace.databinding.PopupCustomdomainLearnDomainBinding
import com.boost.marketplace.ui.Compare_Plans.ComparePacksViewModel
import com.boost.marketplace.ui.details.call_track.CallTrackingActivity
import com.boost.marketplace.ui.details.domain.CustomDomainActivity
import com.framework.analytics.SentryController
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.pref.clientId
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_feature_details.*
import kotlinx.android.synthetic.main.feature_details_popup.*
import kotlinx.android.synthetic.main.view_no_selection.*
import kotlinx.android.synthetic.main.view_review_selection.*
import kotlinx.android.synthetic.main.view_select_number.*
import kotlinx.android.synthetic.main.view_select_website.*
import kotlinx.android.synthetic.main.view_select_website.selectWebsiteIwillDoItLater
import kotlinx.android.synthetic.main.view_select_website.selectWebsiteSubmit
import kotlinx.android.synthetic.main.view_select_website.tv_explore_select_website
import kotlinx.android.synthetic.main.view_selected_number.*
import kotlinx.android.synthetic.main.view_selected_website.*
import java.text.NumberFormat
import java.util.*

class FeatureDetailsPopup : BaseBottomSheetDialog<PopupCustomdomainLearnDomainBinding, ComparePacksViewModel>() {
    private var domainName: String? = null

    //    private var featuresModel: FeaturesModel? = null
    var experienceCode: String? = null
    var screenType: String? = null
    var fpName: String? = null
    var itemInCart = false
    var needMoreFeatureItemInCart = false
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

    override fun getLayout(): Int {
        return R.layout.feature_details_popup
    }

    override fun getViewModelClass(): Class<ComparePacksViewModel> {
        return ComparePacksViewModel::class.java
    }

    override fun onCreateView() {
         fpid = requireArguments().getString("fpid")
        isDeepLink = requireArguments().getBoolean("isDeepLink", false)
        deepLinkViewType = requireArguments().getString("deepLinkViewType") ?: ""
        deepLinkDay = requireArguments().getString("deepLinkDay")?.toIntOrNull() ?: 7

        experienceCode = requireArguments().getString("expCode")
        screenType = requireArguments().getString("screenType")
        fpName = requireArguments().getString("fpName")
        fpid = requireArguments().getString("fpid")
        fpTag = requireArguments().getString("fpTag")
        email = requireArguments().getString("email")
        mobileNo = requireArguments().getString("mobileNo")
        profileUrl = requireArguments().getString("profileUrl")
        accountType = requireArguments().getString("accountType")
        isOpenCardFragment = requireArguments().getBoolean("isOpenCardFragment", false)
        isOpenHomeFragment = requireArguments().getBoolean("isComingFromOrderConfirm", false)
        isOpenAddOnsFragment = requireArguments().getBoolean("isComingFromOrderConfirmActivation", false)
        widgetFeatureCode = requireArguments().getString("buyItemKey")
        userPurchsedWidgets = requireArguments().getStringArrayList("userPurchsedWidgets") ?: ArrayList()
//        val jsonString = requireArguments().getString("bundleData")
//        Log.v("jsonString", " " + jsonString)
//        featuresModel = Gson().fromJson<FeaturesModel>(jsonString, object : TypeToken<FeaturesModel>() {}.type)

        back_btn.setOnClickListener {
            dismiss()
        }

        // Default layout to open
        select_website_layout.visibility = View.VISIBLE
        selectWebsiteIwillDoItLater.setOnClickListener {
            hideAllLayout()
            select_domain_layout.visibility = View.VISIBLE
        }

        selectDomainIwillDoItLater.setOnClickListener {
            hideAllLayout()
            no_selection_layout.visibility = View.VISIBLE
        }

        selectWebsiteSubmit.setOnClickListener {
            hideAllLayout()
            selected_website_layout.visibility = View.VISIBLE
            tv_empty_selected_website.text = domainName
        }

        selectedNumberContinue.setOnClickListener {
            hideAllLayout()
            review_selection_layout.visibility = View.VISIBLE
        }

        selectedWebsiteContinueButton.setOnClickListener {
            // Onclick of continue button
            hideAllLayout()
            select_domain_layout.visibility = View.VISIBLE
            fpid?.let { viewModel?.loadNumberList(it, clientId) }
        }

        loadData()
        initMvvm()
    }

    private fun loadData() {
        try {
            val pref = activity?.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
            val fpTag = pref?.getString("GET_FP_DETAILS_TAG", null)
            fpTag?.let { DomainRequest(Constants.clientid, it) }
                ?.let { viewModel?.getSuggestedDomains(it) }
        } catch (e: Exception) {
            SentryController.captureException(e)
        }


    }

    private fun initMvvm() {
        viewModel?.updateCustomDomainsResultResult()?.observe(this) {
            for (singleDomain in it.domains) {
                if (singleDomain.isAvailable) {
                    domainName = singleDomain.name
                    tv_empty_select_website.text = singleDomain.name
                    break
                }
            }
            tv_explore_select_website.setOnClickListener {
                exploreDomainOptions()
            }
        }

        viewModel?.getCallTrackingDetails()?.observe(this) {
            if (it != null) {

                System.out.println("numberList" + it)
                val selectedNum = it[0]
                tv_empty_select_number.text = selectedNum
//                val dialogCard = SelectNumberBottomSheet()
//                val bundle = Bundle()

//                val content = SpannableString("Claim the above number\n@ ${numberprice}")
//                content.setSpan(
//                    StyleSpan(Typeface.BOLD),
//                    0,
//                    22,
//                    0
//                )
//                claim_button.setText(content)
//                claim_button.setOnClickListener {
//                    if (selectedNum != null) {
//                        if (!itemInCartStatus) {
//                            if (addonDetails != null) {
//                                prefs.storeCartOrderInfo(null)
//                                viewModel!!.addItemToCart1(addonDetails!!, this, selectedNum)
//                                val event_attributes: HashMap<String, Any> = HashMap()
//                                addonDetails!!.name?.let { it1 ->
//                                    event_attributes.put(
//                                        "Addon Name",
//                                        it1
//                                    )
//                                }
//                                event_attributes.put("Addon Price", addonDetails!!.price)
//                                event_attributes.put(
//                                    "Addon Discounted Price",
//                                    getDiscountedPrice(
//                                        addonDetails!!.price,
//                                        addonDetails!!.discount_percent
//                                    )
//                                )
//                                event_attributes.put(
//                                    "Addon Discount %",
//                                    addonDetails!!.discount_percent
//                                )
//                                event_attributes.put("Addon Validity", 1)
//                                event_attributes.put(
//                                    "Addon Feature Key",
//                                    addonDetails!!.boost_widget_key
//                                )
//                                addonDetails!!.target_business_usecase?.let { it1 ->
//                                    event_attributes.put(
//                                        "Addon Tag",
//                                        it1
//                                    )
//                                }
//                                WebEngageController.trackEvent(
//                                    ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART,
//                                    ADDONS_MARKETPLACE,
//                                    event_attributes
//                                )
//                                itemInCartStatus = true
//                            }
//                        }
//
//                    } else {
//                        Toasty.error(
//                            this,
//                            "Number not available, please select other",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                    val intent = Intent(this, CartActivity::class.java)
//                    intent.putExtra("fpid", fpid)
//                    intent.putExtra("expCode", experienceCode)
//                    intent.putExtra("isDeepLink", isDeepLink)
//                    intent.putExtra("deepLinkViewType", deepLinkViewType)
//                    intent.putExtra("deepLinkDay", deepLinkDay)
//                    intent.putExtra("isOpenCardFragment", isOpenCardFragment)
//                    intent.putExtra("accountType", accountType)
//                    intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
//                    if (email != null) {
//                        intent.putExtra("email", email)
//                    } else {
//                        intent.putExtra("email", "ria@nowfloats.com")
//                    }
//                    if (mobileNo != null) {
//                        intent.putExtra("mobileNo", mobileNo)
//                    } else {
//                        intent.putExtra("mobileNo", "9160004303")
//                    }
//                    intent.putExtra("profileUrl", profileUrl)
//                    startActivity(intent)
//                }
                choose_different_value.setText("Pick another number")
            }
        }
    }

    fun exploreDomainOptions() {
        val intent = Intent(
            activity,
            CustomDomainActivity::class.java
        )
        intent.putExtra("expCode", experienceCode)
        intent.putExtra("fpid", fpid)
//        intent.putExtra("bundleData", Gson().toJson(featuresModel))
//        intent.putExtra(
//            "AddonDiscountedPrice",
//            getDiscountedPrice(addonDetails!!.price, addonDetails!!.discount_percent)
//        )
        startActivity(intent)
    }

    fun showReviewSelection() {
        reviewSelectionLayout.visibility = View.VISIBLE
    }

    fun showSelectWebsiteLayout() {
        select_website_layout.visibility = View.VISIBLE
    }


    fun showNoSelectionLayout() {
        no_selection_layout.visibility = View.VISIBLE
    }

    fun hideAllLayout() {
        select_domain_layout.visibility = View.GONE
        review_selection_layout.visibility = View.GONE
        select_website_layout.visibility = View.GONE
        no_selection_layout.visibility = View.GONE
        selected_website_layout.visibility = View.GONE
        selected_number_layout.visibility = View.GONE
    }

}