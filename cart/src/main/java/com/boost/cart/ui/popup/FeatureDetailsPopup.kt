package com.boost.cart.ui.popup

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.boost.cart.CartActivity
import com.boost.cart.R
import com.boost.cart.interfaces.CartFragmentListener
import com.boost.cart.ui.home.CartViewModel
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.Constants
import com.boost.dbcenterapi.utils.SharedPrefs
import com.framework.analytics.SentryController
import com.framework.pref.clientId
import com.framework.utils.RootUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.feature_details_bottomsheet.no_selection_layout
import kotlinx.android.synthetic.main.feature_details_bottomsheet.review_selection_layout
import kotlinx.android.synthetic.main.feature_details_bottomsheet.select_website_layout
import kotlinx.android.synthetic.main.feature_details_bottomsheet.selected_number_layout
import kotlinx.android.synthetic.main.feature_details_bottomsheet.selected_website_layout
import kotlinx.android.synthetic.main.feature_details_bottomsheet.view.riv_close_bottomSheet
import kotlinx.android.synthetic.main.feature_details_bottomsheet.view.select_website_layout
import kotlinx.android.synthetic.main.feature_details_bottomsheet.view.selected_website_layout
import kotlinx.android.synthetic.main.feature_details_popup.*
import kotlinx.android.synthetic.main.feature_details_popup.view.*
import kotlinx.android.synthetic.main.view_review_selection.*
import kotlinx.android.synthetic.main.view_review_selection.view.*
import kotlinx.android.synthetic.main.view_select_number.*
import kotlinx.android.synthetic.main.view_select_number.view.*
import kotlinx.android.synthetic.main.view_select_website.*
import kotlinx.android.synthetic.main.view_select_website.view.*
import kotlinx.android.synthetic.main.view_select_website.view.selectWebsiteSubmit
import kotlinx.android.synthetic.main.view_selected_number.*
import kotlinx.android.synthetic.main.view_selected_number.view.*
import kotlinx.android.synthetic.main.view_selected_website.*
import kotlinx.android.synthetic.main.view_selected_website.view.*

class FeatureDetailsPopup(val listener: CartFragmentListener) : DialogFragment() {
    private var domainName: String? = null
    var experienceCode: String? = null
    var screenType: String? = null
    var fpName: String? = null
    var itemInCart = false
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
    var vmn: String? = null
    var bundleData: Bundles? = null
    lateinit var prefs: SharedPrefs
    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0
    lateinit var singleAddon: FeaturesModel
    lateinit var viewModel: CartViewModel
    private var selectedNum: String? = null

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view  =  inflater.inflate(R.layout.feature_details_popup, container,false)
        viewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)
        vmn = requireArguments().getString("vmn",null)
        prefs = SharedPrefs(requireActivity())
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
        isOpenAddOnsFragment =
            requireArguments().getBoolean("isComingFromOrderConfirmActivation", false)
        widgetFeatureCode = requireArguments().getString("buyItemKey")
        userPurchsedWidgets =
            requireArguments().getStringArrayList("userPurchsedWidgets") ?: ArrayList()

        val jsonString = requireArguments().getString("bundleData")
        bundleData = Gson().fromJson<Bundles>(jsonString, object : TypeToken<Bundles>() {}.type)

        loadData()
        initMvvm()

        view.riv_close_bottomSheet.setOnClickListener {
            dismiss()
        }

        if (vmn!!.equals("true")){
            vmnSelection(view)
        }else if(vmn!!.equals("false")){
            domainSelction(view)
        }else if (vmn!!.equals("null")){
            defaultLayout(view)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        //clear previous existing data
        if(prefs.getSelectedDomainName().isNullOrEmpty().not()) {
            selectedNames()
            domainName = prefs.getSelectedDomainName()
            tv_empty_select_website.text = prefs.getSelectedDomainName()
            tv_empty_selected_website.text = prefs.getSelectedDomainName()
            tv_title_domain.text = prefs.getSelectedDomainName()
        }
        if(prefs.getSelectedVMNName().isNullOrEmpty().not()) {
            selectedNames()
            selectedNum = prefs.getSelectedVMNName()
            tv_call_expert_select_domain.text = prefs.getSelectedVMNName()
            tv_empty_select_number.text = prefs.getSelectedVMNName()
            tv_title_number.text = prefs.getSelectedVMNName()
        }
    }

    private fun defaultLayout(view: View){

        view.riv_close_bottomSheet.setOnClickListener {
            prefs.storeSelectedDomainName(null)
            prefs.storeSelectedVMNName(null)
            dismiss()
        }

        view.select_website_layout.visibility = View.VISIBLE
        view.selectWebsiteIwillDoItLater.setOnClickListener {
            domainName = null
            prefs.storeSelectedDomainName(null)
            hideAllLayout()
            view.select_number_layout.visibility = View.VISIBLE
            view.topImageView.setImageResource(R.drawable.vmn_selection_point)
        }

        view.selectWebsiteSubmit.setOnClickListener {
            view.selectWebsiteSubmit.setBackgroundResource(R.drawable.disabled_button)
            view.tv_empty_select_website.visibility=View.GONE
            view.tv_selected_domain.visibility=View.VISIBLE
            view.selectWebsiteIwillDoItLater.isClickable=false
            Handler().postDelayed({
                hideAllLayout()
                view.select_number_layout.visibility = View.VISIBLE
                view.topImageView.setImageResource(com.boost.cart.R.drawable.vmn_selection_point)
                listener.featureDetailsPopup(domainName!!)
            }, 1000)
        }

        view.selectedWebsiteContinueButton.setOnClickListener {
            hideAllLayout()
            view.select_number_layout.visibility = View.VISIBLE
            view.topImageView.setImageResource(com.boost.cart.R.drawable.vmn_selection_point)
            listener.featureDetailsPopup(domainName!!)
        }

        view.tv_explore_select_website1.setOnClickListener {
            exploreDomainOptionsDefault()
            view.tv_empty_selected_website.text = domainName
            listener.featureDetailsPopup(domainName!!)
        }

        view.tv_explore_select_website.setOnClickListener {
            exploreDomainOptionsDefault()
            listener.featureDetailsPopup(domainName!!)
            Handler().postDelayed({
                hideAllLayout()
                view.selected_website_layout.visibility=View.VISIBLE
            }, 1000)
        }

        view.tv_explore_text_select_vmn.setOnClickListener {
            listener.featureDetailsPopupvmn(selectedNum!!)
            exploreVmnOptionsDefault()
            Handler().postDelayed({
                hideAllLayout()
                view.selected_number_layout.visibility=View.VISIBLE
            }, 1000)
        }

        view.tv_explore_select_number.setOnClickListener {
            exploreVmnOptionsDefault()
        }

        view.selectedNumberContinue.setOnClickListener {
            hideAllLayout()
            view.review_selection_layout.visibility = View.VISIBLE
            view.topImageView.setImageResource(com.boost.cart.R.drawable.review_selection_point)
            listener.featureDetailsPopup(selectedNum!!)
        }

        view.tv_edit.setOnClickListener {
            exploreDomainOptionsDefault()
        }

        view.tv_edit_number.setOnClickListener {
            exploreVmnOptionsDefault()
        }

        view.selectVmnSubmit.setOnClickListener {
            selectedNum?.let { it1 -> listener.featureDetailsPopupvmn(it1) }
            view.selectVmnSubmit.setBackgroundResource(R.drawable.disabled_button)
            view.tv_call_expert_select_domain.visibility=View.GONE
            view.tv_vmn_selected_txt.visibility=View.VISIBLE
            view.selectVmnIwillDoItLater.isClickable=false
            Handler().postDelayed({
                hideAllLayout()
                view.review_selection_layout.visibility = View.VISIBLE
                selectedNames()
                view.topImageView.setImageResource(com.boost.cart.R.drawable.review_selection_point)
                view.tv_title_number.text = selectedNum
            }, 1000)
        }

        view.selectVmnIwillDoItLater.setOnClickListener {
            selectedNum = null
            prefs.storeSelectedVMNName(null)
            hideAllLayout()
            view.review_selection_layout.visibility = View.VISIBLE
            selectedNames()
            view.topImageView.setImageResource(R.drawable.review_selection_point)
        }

        view.selectedNumberContinue.setOnClickListener {
            hideAllLayout()
            listener.featureDetailsPopupvmn(selectedNum!!)
            view.review_selection_layout.visibility = View.VISIBLE
            selectedNames()
            view.topImageView.setImageResource(R.drawable.review_selection_point)
        }

        view.tv_edit_number.setOnClickListener {
            exploreVmnOptionsDefault()
        }

        view.tv_edit.setOnClickListener {
            exploreDomainOptionsDefault()
        }

        view.domain_select_red.setOnClickListener {
            exploreDomainOptionsDefault()
        }

        view.vmn_select_red.setOnClickListener {
            exploreVmnOptionsDefault()
        }

        view.continueToCart.setOnClickListener {
            dismiss()
        }

    }

    private  fun vmnSelection(view: View){
        view.select_website_layout?.visibility = View.GONE
        view.topImageView.visibility=View.GONE
        view.select_number_layout?.visibility = View.VISIBLE
        view.select_number_layout?.setBackgroundResource(R.color.transparent)
        view.selectVmnIwillDoItLater.setText("Skip & continue to cart")

        view.selectVmnIwillDoItLater.setOnClickListener {
            dismiss()
        }
        view.tv_explore_text_select_vmn.setOnClickListener {
            exploreVmnOptions()
        }
        view.selectVmnSubmit.setOnClickListener {
            prefs.storeSelectedVMNName(selectedNum)
            listener.featureDetailsPopupvmn(selectedNum!!)
            view.selectVmnSubmit.setBackgroundResource(R.drawable.disabled_button)
            view.tv_call_expert_select_domain.visibility=View.GONE
            view.tv_vmn_selected_txt.visibility=View.VISIBLE
            view.selectVmnIwillDoItLater.isClickable=false
            Handler().postDelayed({
                dismiss()
            }, 1000)
        }
    }

    private fun domainSelction(view: View) {

        view.select_website_layout.visibility = View.VISIBLE
        view.topImageView.visibility=View.GONE
        view.select_website_layout.setBackgroundResource(R.color.transparent)
        view.selectWebsiteIwillDoItLater.setText("Skip & continue to cart")
        view.selectWebsiteIwillDoItLater.setOnClickListener {
            dismiss()
        }

        view.selectWebsiteSubmit.setOnClickListener {
            prefs.storeSelectedDomainName(domainName)
            listener.featureDetailsPopup(domainName!!)
            view.selectWebsiteSubmit.setBackgroundResource(R.drawable.disabled_button)
            view.tv_empty_select_website.visibility=View.GONE
            view.tv_selected_domain.visibility=View.VISIBLE
            view.selectWebsiteIwillDoItLater.isClickable=false
            Handler().postDelayed({
                dismiss()
            }, 1000)
        }

        view.tv_explore_select_website.setOnClickListener {
            exploreDomainOptions()
        }
    }

    private fun addToCart() {
        if (bundleData != null) {
            prefs.storeAddedPackageDesc(bundleData!!.desc ?: "")

            val itemIds = arrayListOf<String>()
            for (i in bundleData!!.included_features) {
                itemIds.add(i.feature_code)
            }

            CompositeDisposable().add(
                AppDatabase.getInstance(Application())!!
                    .featuresDao()
                    .getallFeaturesInList(itemIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            var bundleMonthlyMRP = 0.0
                            val minMonth: Int =
                                if (!prefs.getYearPricing() && bundleData!!.min_purchase_months != null && bundleData!!.min_purchase_months!! > 1) bundleData!!.min_purchase_months!! else 1

                            for (singleItem in it) {
                                for (item in bundleData!!.included_features) {
                                    if (singleItem.feature_code == item.feature_code) {
                                        bundleMonthlyMRP += RootUtil.round(
                                            singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0),
                                            2
                                        )
                                    }
                                }
                            }
                            offeredBundlePrice = (bundleMonthlyMRP * minMonth)
                            originalBundlePrice = (bundleMonthlyMRP * minMonth)

                            if (bundleData!!.overall_discount_percent > 0)
                                offeredBundlePrice = RootUtil.round(
                                    originalBundlePrice - (originalBundlePrice * bundleData!!.overall_discount_percent / 100),
                                    2
                                )
                            else
                                offeredBundlePrice = originalBundlePrice

                            //clear cartOrderInfo from SharedPref to requestAPI again
                            prefs.storeCartOrderInfo(null)

                            viewModel.addItemToCartPackage1(
                                CartModel(
                                    bundleData!!._kid,
                                    null,
                                    null,
                                    bundleData!!.name,
                                    "",
                                    bundleData!!.primary_image!!.url,
                                    offeredBundlePrice,
                                    originalBundlePrice,
                                    bundleData!!.overall_discount_percent,
                                    1,
                                    if (!prefs.getYearPricing() && bundleData!!.min_purchase_months != null) bundleData!!.min_purchase_months!! else 1,
                                    "bundles",
                                    null,
                                    ""
                                )
                            )
                        },
                        {
                            it.printStackTrace()
                        }
                    )
            )
        }

        val intent = Intent(
            activity,
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
        dismiss()
    }

    private fun loadData() {
        try {
            val pref = activity?.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
            val fpTag = pref?.getString("GET_FP_DETAILS_TAG", null)
            fpTag?.let { DomainRequest(Constants.clientid, it) }
                ?.let { viewModel.getSuggestedDomains(it) }
            fpid?.let { viewModel.loadNumberList(it, clientId) }
            viewModel.getCartItems()
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    private fun initMvvm() {

        if (bundleData != null) {
            prefs.storeAddedPackageDesc(bundleData!!.desc ?: "")

            val itemIds = arrayListOf<String>()
            for (i in bundleData!!.included_features) {
                itemIds.add(i.feature_code)
            }
            CompositeDisposable().add(
                AppDatabase.getInstance(Application())!!
                    .featuresDao()
                    .getallFeaturesInList(itemIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            if (it.isNotEmpty()) {
                                for (singleItem in it){
                                    if (singleItem.feature_code == "DOMAINPURCHASE"){
                                        singleAddon= singleItem
                                    }
                                }
                            }
                        },
                        {
                            it.printStackTrace()
                        }
                    )
            )
        }

        viewModel.updateCustomDomainsResultResult().observe(this) {
            if (!it.domains.isNullOrEmpty()){
                mainll.visibility=View.VISIBLE
                shimmer_anim.visibility=View.GONE
                for (singleDomain in it.domains) {
                    if (singleDomain.isAvailable) {
                        domainName = singleDomain.name
                        tv_empty_select_website.text = singleDomain.name
                        tv_selected_domain.text = singleDomain.name
                        break
                    }
                }
            }else{
                mainll.visibility=View.GONE
                shimmer_anim.visibility=View.VISIBLE
            }
        }

//        viewModel.getLoaderStatus().observe(this)  {
//            if (it) {
//                mainll.visibility=View.GONE
//                shimmer_anim.visibility=View.VISIBLE
//            } else {
//                mainll.visibility=View.VISIBLE
//                shimmer_anim.visibility=View.GONE
//            }
//        }

        viewModel.getCallTrackingDetails().observe(this) {
            if (it != null) {
                System.out.println("numberList" + it)
                if (it.size>=1){
                    selectedNum = it[0]
                    tv_call_expert_select_domain.text = selectedNum
                    tv_vmn_selected_txt.text = selectedNum
                    view?.selectVmnSubmit?.isClickable = true
                } else{
                    selectedNum = "No VMN available"
                }
            }else {
                view?.selectVmnSubmit?.isClickable = false
                context?.let { it1 ->
                    Toasty.error(it1, "Error in Loading Available Numbers!!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun exploreDomainOptions() {
        try {
            val intent = Intent(
                activity,
                Class.forName("com.boost.marketplace.ui.details.domain.CustomDomainActivity")
            )
            intent.putExtra("expCode", experienceCode)
            intent.putExtra("fpid", fpid)
            intent.putExtra("bundleData", Gson().toJson(singleAddon))
            intent.putExtra("domainSelectionForCart", true)
            startActivity(intent)
            dismiss()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun exploreVmnOptions() {
        try {
            val intent = Intent(
                activity,
                Class.forName("com.boost.marketplace.ui.details.call_track.CallTrackingActivity")
            )
            intent.putExtra("expCode", experienceCode)
            intent.putExtra("fpid", fpid)
            intent.putExtra("bundleData", Gson().toJson(singleAddon))
            intent.putExtra("vmnSelectionForCart", true)
            startActivity(intent)
            dismiss()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun exploreDomainOptionsDefault() {
        try {
            val intent = Intent(
                activity,
                Class.forName("com.boost.marketplace.ui.details.domain.CustomDomainActivity")
            )
            intent.putExtra("expCode", experienceCode)
            intent.putExtra("fpid", fpid)
            intent.putExtra("bundleData", Gson().toJson(singleAddon))
            intent.putExtra("domainSelectionForCart", true)
            startActivity(intent)
           // dismiss()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun exploreVmnOptionsDefault() {
        try {
            val intent = Intent(
                activity,
                Class.forName("com.boost.marketplace.ui.details.call_track.CallTrackingActivity")
            )
            intent.putExtra("expCode", experienceCode)
            intent.putExtra("fpid", fpid)
            intent.putExtra("bundleData", Gson().toJson(singleAddon))
            intent.putExtra("vmnSelectionForCart", true)
            startActivity(intent)
          //  dismiss()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    fun hideAllLayout() {
        select_number_layout.visibility = View.GONE
        review_selection_layout.visibility = View.GONE
        select_website_layout.visibility = View.GONE
        no_selection_layout.visibility = View.GONE
        selected_website_layout.visibility = View.GONE
        selected_number_layout.visibility = View.GONE
    }

    private fun selectedNames() {

        constrainView()
        if(!prefs.getSelectedDomainName().isNullOrEmpty()){
            view?.tv_title_domain?.text =prefs.getSelectedDomainName()
            view?.tv_edit?.visibility =View.VISIBLE
            view?.domain_select_red?.visibility =View.GONE
            view?.yourDomainLayout?.setBackgroundResource(R.drawable.round_corner_white)
            view?.tvSubTextYourDomainLayout?.text="Post successful payment towards your selected pack, above domain will be mapped to your website address."
        }else{
            view?.tv_title_domain?.text = "Domain not selected yet"
            view?.tv_edit?.visibility =View.GONE
            view?.domain_select_red?.visibility =View.VISIBLE
            view?.yourDomainLayout?.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
            view?.tvSubTextYourDomainLayout?.text= "In case you want to select a Domain post the payment you can do so by going to ‘My current plan’ section after successful payment."
        }
        if(!prefs.getSelectedVMNName().isNullOrEmpty()){
            view?.tv_title_number?.text =prefs.getSelectedVMNName()
            view?.tv_edit_number?.visibility =View.VISIBLE
            view?.vmn_select_red?.visibility =View.GONE
            view?.yourNumberLayout?.setBackgroundResource(R.drawable.round_corner_white)
            view?.tvSubTextYourNumberLayout?.text ="Post successful payment towards your selected pack, above VMN will be mapped to your website."
        }else {
            view?.tv_title_number?.text = "VMN not selected yet"
            view?.tv_edit_number?.visibility = View.GONE
            view?.vmn_select_red?.visibility = View.VISIBLE
            view?.yourNumberLayout?.setBackgroundResource(R.drawable.rounded_border_red_white_bg)
            view?.tvSubTextYourNumberLayout?.text =
                "In case you want to select a VMN post the payment you can do so by going to ‘My current plan’ section after successful payment."
        }
    }

    private fun constrainView() {
        if (!prefs.getSelectedDomainName().isNullOrEmpty() && !prefs.getSelectedVMNName().isNullOrEmpty()){
            view?.reviewSelectionDomainLayout?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = tv_review_selection.id
            }
            view?.reviewSelectionNumberLayout?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = reviewSelectionDomainLayout.id
            }
            view?.continueToCart?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = reviewSelectionNumberLayout.id
            }
        }else if(prefs.getSelectedDomainName().isNullOrEmpty() && prefs.getSelectedVMNName().isNullOrEmpty()){
            view?.reviewSelectionDomainLayout?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = tv_review_selection.id
            }
            view?.reviewSelectionNumberLayout?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = reviewSelectionDomainLayout.id
            }
            view?.continueToCart?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = reviewSelectionNumberLayout.id
            }
        } else if(prefs.getSelectedDomainName().isNullOrEmpty() && !prefs.getSelectedVMNName().isNullOrEmpty()){
            view?.reviewSelectionDomainLayout?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = tv_review_selection.id
            }
            view?.reviewSelectionNumberLayout?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = reviewSelectionDomainLayout.id
            }
            view?.continueToCart?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = reviewSelectionNumberLayout.id
            }
        } else if (!prefs.getSelectedDomainName().isNullOrEmpty() && prefs.getSelectedVMNName().isNullOrEmpty()){
            view?.reviewSelectionNumberLayout?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = tv_review_selection.id
            }
            view?.reviewSelectionDomainLayout?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = reviewSelectionNumberLayout.id
            }
            view?.continueToCart?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                topToBottom = reviewSelectionDomainLayout.id
            }
        }
    }


}