package com.boost.marketplace.ui.popup.call_track

import android.app.Application
import android.app.ProgressDialog
import android.content.Intent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.boost.cart.CartActivity
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.R
import com.boost.marketplace.databinding.CallTrackingAddToCartPopupBinding
import com.boost.marketplace.ui.details.FeatureDetailsViewModel
import com.boost.marketplace.ui.details.call_track.CallTrackingActivity
import com.framework.base.BaseBottomSheetDialog
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SelectNumberBottomSheet :
    BaseBottomSheetDialog<CallTrackingAddToCartPopupBinding, FeatureDetailsViewModel>() {

    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
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
    var userPurchsedWidgets = java.util.ArrayList<String>()
    var itemInCartStatus = false
    lateinit var singleAddon: FeaturesModel
    lateinit var progressDialog: ProgressDialog
    lateinit var prefs: SharedPrefs

    override fun getLayout(): Int {
        return R.layout.call_tracking_add_to_cart_popup
    }

    companion object {
        fun newInstance() = SelectNumberBottomSheet()
    }

    override fun getViewModelClass(): Class<FeatureDetailsViewModel> {
        return FeatureDetailsViewModel::class.java
    }

    override fun onCreateView() {
        experienceCode = requireArguments().getString("expCode")
        fpid = requireArguments().getString("fpid")
        isDeepLink = requireArguments().getBoolean("isDeepLink", false)
        deepLinkViewType = requireArguments().getString("deepLinkViewType") ?: ""
        deepLinkDay = requireArguments().getString("deepLinkDay")?.toIntOrNull() ?: 7
        email = requireArguments().getString("email")
        mobileNo = requireArguments().getString("mobileNo")
        profileUrl = requireArguments().getString("profileUrl")
        accountType = requireArguments().getString("accountType")
        isOpenCardFragment = requireArguments().getBoolean("isOpenCardFragment", false)
        userPurchsedWidgets =
            requireArguments().getStringArrayList("userPurchsedWidgets") ?: java.util.ArrayList()
        val jsonString = requireArguments().getString("bundleData")
        singleAddon =
            Gson().fromJson<FeaturesModel>(jsonString, object : TypeToken<FeaturesModel>() {}.type)
        viewModel?.setApplicationLifecycle(Application(), this)
        viewModel = ViewModelProviders.of(this).get(FeatureDetailsViewModel::class.java)
        progressDialog = ProgressDialog(context)
        prefs = SharedPrefs(baseActivity)


        binding?.tvTitle?.text = requireArguments().getString("phone_number")
        val select_another_no =
            "We have selected the above virtual phone number for you. You can also select another number."
        val ss =
            SpannableString("We have selected the above virtual phone number for you. You can also select another number.")
        val string = SpannableString("select another number.")

        ss.setSpan(
            UnderlineSpan(),
            select_another_no.indexOf(string.toString()),
            select_another_no.length,
            0
        )
        ss.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorAccent)),
            select_another_no.indexOf(string.toString()),
            select_another_no.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding?.tvSelectNumber?.text = ss
        binding?.tvSelectNumber?.setOnClickListener {
            fpid = requireArguments().getString("fpid")

            val intent = Intent(requireContext(), CallTrackingActivity::class.java)
            intent.putExtra("fpid", fpid)
            intent.putExtra("bundleData", Gson().toJson(singleAddon))
            intent.putExtra(
                "AddonDiscountedPrice",
                getDiscountedPrice(singleAddon!!.price, singleAddon!!.discount_percent)
            )
            intent.putExtra("expCode", experienceCode)
            intent.putExtra("isDeepLink", isDeepLink)
            intent.putExtra("deepLinkViewType", deepLinkViewType)
            intent.putExtra("deepLinkDay", deepLinkDay)
            intent.putExtra("isOpenCardFragment", isOpenCardFragment)
            intent.putExtra(
                "accountType",
                accountType
            )
            intent.putExtra(
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
        binding?.backBtn?.setOnClickListener {
            dismiss()
        }
        binding?.tvCart?.setOnClickListener {
            dismiss()
            if (!itemInCartStatus) {
                if (singleAddon != null) {
                    prefs.storeCartOrderInfo(null)
                    viewModel!!.addItemToCart1(singleAddon, baseActivity)
                    val event_attributes: HashMap<String, Any> = HashMap()
                    singleAddon.name?.let { it1 -> event_attributes.put("Addon Name", it1) }
                    event_attributes.put("Addon Price", singleAddon.price)
                    event_attributes.put(
                        "Addon Discounted Price",
                        getDiscountedPrice(singleAddon.price, singleAddon.discount_percent)
                    )
                    event_attributes.put("Addon Discount %", singleAddon.discount_percent)
                    event_attributes.put("Addon Validity", 1)
                    event_attributes.put("Addon Feature Key", singleAddon.boost_widget_key)
                    singleAddon.target_business_usecase?.let { it1 ->
                        event_attributes.put(
                            "Addon Tag",
                            it1
                        )
                    }
                    WebEngageController.trackEvent(
                        ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART,
                        ADDONS_MARKETPLACE,
                        event_attributes
                    )
                    itemInCartStatus = true
                }
            }
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("fpid", fpid)
            intent.putExtra("expCode", experienceCode)
            intent.putExtra("isDeepLink", isDeepLink)
            intent.putExtra("deepLinkViewType", deepLinkViewType)
            intent.putExtra("deepLinkDay", deepLinkDay)
            intent.putExtra("isOpenCardFragment", isOpenCardFragment)
            intent.putExtra("accountType", accountType)
            intent.putStringArrayListExtra("userPurchsedWidgets", userPurchsedWidgets)
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

    private fun getDiscountedPrice(price: Double, discountPercent: Int): Double {
        return price - ((discountPercent / 100) * price)
    }
}