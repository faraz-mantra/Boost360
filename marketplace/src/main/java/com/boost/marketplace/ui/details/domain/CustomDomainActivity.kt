package com.boost.marketplace.ui.details.domain

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.CartActivity
import com.boost.cart.utils.Utils
import com.boost.dbcenterapi.data.api_model.CustomDomain.Domain
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.R
import com.boost.marketplace.adapter.CustomDomainListAdapter
import com.boost.marketplace.adapter.CustomDomainListAdapter1
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityCustomDomainBinding
import com.boost.marketplace.interfaces.DomainListener
import com.boost.marketplace.ui.popup.customdomains.ConfirmedCustomDomainBottomSheet
import com.boost.marketplace.ui.popup.customdomains.CustomDomainHelpBottomSheet
import com.boost.marketplace.ui.popup.customdomains.CustomDomainLearnDomainBottomSheet
import com.boost.marketplace.ui.popup.customdomains.SSLCertificateBottomSheet
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.framework.utils.hideKeyBoard
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import java.text.NumberFormat
import java.util.*


class CustomDomainActivity : AppBaseActivity<ActivityCustomDomainBinding, CustomDomainViewModel>(),DomainListener {

    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
    var experienceCode: String? = null
    var blockedItem: String?=null
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
    var userPurchsedWidgets = java.util.ArrayList<String>()
    var allDomainsList: List<Domain>? = null
    var itemInCartStatus = false
     var  result:Boolean? = null
    lateinit var customDomainListAdapter1: CustomDomainListAdapter1
    lateinit var customDomainListAdapter: CustomDomainListAdapter
    lateinit var singleAddon: FeaturesModel
    lateinit var progressDialog: ProgressDialog
    lateinit var prefs: SharedPrefs
    var domainPrice: String? = null
    var pricing: String? = null

    override fun getLayout(): Int {
        return R.layout.activity_custom_domain
    }

    companion object {
        fun newInstance() = CustomDomainActivity()
    }

    override fun getViewModelClass(): Class<CustomDomainViewModel> {
        return CustomDomainViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()

        experienceCode = intent.getStringExtra("expCode")
        fpid = intent.getStringExtra("fpid")
        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        deepLinkViewType = intent.getStringExtra("deepLinkViewType") ?: ""
        deepLinkDay = intent.getStringExtra("deepLinkDay")?.toIntOrNull() ?: 7
        email = intent.getStringExtra("email")
        mobileNo = intent.getStringExtra("mobileNo")
        profileUrl = intent.getStringExtra("profileUrl")
        accountType = intent.getStringExtra("accountType")
        isOpenCardFragment = intent.getBooleanExtra("isOpenCardFragment", false)
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: java.util.ArrayList()
        val jsonString = intent.extras?.getString("bundleData")
        singleAddon = Gson().fromJson<FeaturesModel>(jsonString, object : TypeToken<FeaturesModel>() {}.type)
        viewModel.setApplicationLifecycle(application, this)
        viewModel = ViewModelProviders.of(this).get(CustomDomainViewModel::class.java)
        customDomainListAdapter1= CustomDomainListAdapter1(this,ArrayList(),this)
        customDomainListAdapter = CustomDomainListAdapter(this, ArrayList(),this)
        progressDialog = ProgressDialog(this)
        prefs = SharedPrefs(this)

        binding?.help?.setOnClickListener {
            val dialogCard = CustomDomainHelpBottomSheet()
            dialogCard.show(
                this.supportFragmentManager,
                CustomDomainHelpBottomSheet::class.java.name
            )
        }
        binding?.btnSelectDomain?.setOnClickListener {
            if(blockedItem!=null //&& result ==false
            ) {
                val dialogCard = ConfirmedCustomDomainBottomSheet()
                val bundle = Bundle()
                bundle.putString("blockedItem", blockedItem)
                bundle.putString("fpid", fpid)
                bundle.putString("fpTag", fpTag)
                bundle.putString("price",pricing)
                bundle.putString("expCode", experienceCode)
                bundle.putString("bundleData", Gson().toJson(singleAddon))
                bundle.putString("isDeepLink", isDeepLink.toString())
                bundle.putString("deepLinkViewType", deepLinkViewType)
                bundle.putString("deepLinkDay", deepLinkDay.toString())
                bundle.putString("isOpenCardFragment", isOpenCardFragment.toString())
                bundle.putString("accountType", accountType)
                bundle.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
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
                dialogCard.arguments = bundle
                dialogCard.show(
                    this.supportFragmentManager,
                    ConfirmedCustomDomainBottomSheet::class.java.name
                )
            }
//            else if (blockedItem!=null && result ==true){
//                Toasty.error(this, "Domain unavailable select other", Toast.LENGTH_SHORT).show()
//            }
            else{
                Toasty.error(this, "No domain selected ", Toast.LENGTH_SHORT).show()
            }
        }
        binding?.tvLearmore?.setOnClickListener {
            val dialogCard = SSLCertificateBottomSheet()
            dialogCard.show(this.supportFragmentManager, SSLCertificateBottomSheet::class.java.name)
        }
        binding?.addonsBack?.setOnClickListener {
            finish()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }

        binding?.learnDomain?.setOnClickListener {
            val dialogCard = CustomDomainLearnDomainBottomSheet()
            dialogCard.show(this.supportFragmentManager, CustomDomainLearnDomainBottomSheet::class.java.name)
        }

        binding?.etDomain?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                binding?.btnSelectDomain?.visibility=View.GONE
                binding?.learnDomain?.visibility=View.VISIBLE

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.length > 1) {
                    binding?.searchBtn?.visibility = View.VISIBLE
                    binding?.searchBtn?.setOnClickListener {
                        hideKeyBoard()
                        binding?.learnDomain?.visibility=View.GONE
                        binding?.btnSelectDomain?.visibility=View.VISIBLE
                        binding?.searchBtn?.visibility = View.GONE
                        binding?.tvSuggestedDomains?.text = "Search results"
                        updateItemBySearchValue(p0.toString())
                    }
                    binding?.ivCross?.visibility = View.VISIBLE
                } else {
                    binding?.learnDomain?.visibility=View.GONE
                    binding?.btnSelectDomain?.visibility=View.VISIBLE
                    binding?.tvSuggestedDomains?.text = "Suggested domains for you"
                    binding?.searchBtn?.visibility = View.GONE
                    binding?.searchResults?.visibility = View.GONE
                    binding?.ivCross?.visibility = View.GONE
                    allDomainsList?.let { updateDomainsRecycler(it) }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding?.ivCross?.setOnClickListener {
            blockedItem=null
            binding?.btnSelectDomain?.setBackgroundResource(R.color.btn_bg_color_disabled);
            binding?.ivCross?.visibility = View.GONE
            binding?.searchResults?.visibility = View.GONE
            binding?.rvCustomDomain1?.visibility=View.GONE
            binding?.tvSuggestedDomains?.text = "Suggested domains for you"
            binding?.learnDomain?.visibility=View.GONE
            binding?.btnSelectDomain?.visibility=View.VISIBLE
            allDomainsList?.let { updateDomainsRecycler(it) }
            binding?.etDomain?.text = null
        }

        binding?.tvSkipTocart?.setOnClickListener {
            if (!itemInCartStatus) {
                if (singleAddon != null) {
                    prefs.storeCartOrderInfo(null)
                    viewModel.addItemToCart1(singleAddon, singleAddon.name?:"")
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
            val intent = Intent(applicationContext, CartActivity::class.java)
            intent.putExtra("fpid", fpid)
            intent.putExtra("fpTag",fpTag)
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

        loadData()
        initMVVM()
        initRecyclerView()
        initRecyclerView1()
    }

    private fun loadData() {
        val pref = getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
        val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
        fpTag?.let { DomainRequest(clientid, it) }
            ?.let { viewModel.GetSuggestedDomains(it) }
    }

    private fun initMVVM() {
        viewModel.updateResult().observe(this, androidx.lifecycle.Observer {
            allDomainsList = it.domains
            updateDomainsRecycler(it.domains)
            updateFreeAddonsRecycler1(it.domains)
            val discount = 100 - singleAddon.discount_percent
            val paymentPrice = Utils.priceCalculatorForYear(
                (discount*singleAddon.price) / 100.0,
                singleAddon.widget_type ?: "",
                this
            )
            pricing =  "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                .format(paymentPrice) + Utils.yearlyOrMonthlyOrEmptyValidity(
                singleAddon.widget_type ?: "",  this
            )
            domainPrice=pricing

        })

        viewModel.updatesLoader().observe(this, androidx.lifecycle.Observer {
            if (it) {
                binding?.scrollView?.visibility = View.GONE
                binding?.shimmerViewDomain?.visibility = View.VISIBLE
            } else {
                binding?.scrollView?.visibility = View.VISIBLE
                binding?.shimmerViewDomain?.visibility = View.GONE
            }
        })

//        viewModel.updateStatus().observe(this, androidx.lifecycle.Observer{
//            result=it.Result
////            if (it.Result.equals(false)){
////                binding?.btnSelectDomain?.setBackgroundResource(R.color.colorAccent1);
////            }
////            else{
////                binding?.btnSelectDomain?.setBackgroundResource(R.color.btn_bg_color_disabled)
////            }
//        })
    }

    fun initRecyclerView() {
        val gridLayoutManager = LinearLayoutManager(this)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvCustomDomain?.apply {
            layoutManager = gridLayoutManager
            binding?.rvCustomDomain?.adapter = customDomainListAdapter
        }
    }
    fun initRecyclerView1() {
        val gridLayoutManager = LinearLayoutManager(this )
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvCustomDomain1?.apply {
            layoutManager = gridLayoutManager
            binding?.rvCustomDomain1?.adapter = customDomainListAdapter1
        }
    }

    private fun updateDomainsRecycler(list: List<Domain>) {
        customDomainListAdapter.addupdates(list)
        customDomainListAdapter.notifyDataSetChanged()
        binding?.rvCustomDomain?.setFocusable(false)
    }

    private fun updateFreeAddonsRecycler1(list: List<Domain>) {
        customDomainListAdapter1.addupdates(list)
        customDomainListAdapter1.notifyDataSetChanged()
        binding?.rvCustomDomain1?.setFocusable(false)
    }

    fun updateItemBySearchValue(searchValue: String) {
        val freeitemList: java.util.ArrayList<Domain> = arrayListOf()
        val paiditemList: java.util.ArrayList<Domain> = arrayListOf()
        for (singleDomain in allDomainsList!!) {
            if (singleDomain.name.lowercase().indexOf(searchValue.lowercase()) != -1) {
                binding?.tvSuggestedDomains?.text = "Search results"
                freeitemList.add(singleDomain)
                updateDomainsRecycler(freeitemList)
            }
               if (singleDomain.name.lowercase().equals(searchValue.lowercase()) && searchValue.length > 7) {
                    binding?.rvCustomDomain1Layout?.visibility=View.VISIBLE
                    binding?.rvCustomDomain1?.visibility=View.VISIBLE
                    paiditemList.add(singleDomain)
                    updateFreeAddonsRecycler1(paiditemList)
                    updateDomainsRecycler(freeitemList)
            }
        }
    }

    private fun getDiscountedPrice(price: Double, discountPercent: Int): Double {
        return price - ((discountPercent / 100) * price)
    }

    fun getAccessToken(): String {
        return UserSessionManager(this).getAccessTokenAuth()?.barrierToken() ?: ""
    }

    override fun onSelectedDomain(itemList: Domain?) {
        blockedItem=itemList?.name
        binding?.btnSelectDomain?.setBackgroundResource(R.color.colorAccent1);

    }

    override fun onSearchedDomain(itemList: Domain?) {
        blockedItem=itemList?.name
        if(blockedItem!=null //&& result ==false
        ) {
            val dialogCard = ConfirmedCustomDomainBottomSheet()
            val bundle = Bundle()
            bundle.putString("blockedItem", blockedItem)
            bundle.putString("fpid", fpid)
            bundle.putString("fpTag", fpTag)
            bundle.putString("expCode", experienceCode)
            bundle.putString("bundleData", Gson().toJson(singleAddon))
            bundle.putString("isDeepLink", isDeepLink.toString())
            bundle.putString("deepLinkViewType", deepLinkViewType)
            bundle.putString("deepLinkDay", deepLinkDay.toString())
            bundle.putString("isOpenCardFragment", isOpenCardFragment.toString())
            bundle.putString("accountType", accountType)
            bundle.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
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
            dialogCard.arguments = bundle
            dialogCard.show(
                this.supportFragmentManager,
                ConfirmedCustomDomainBottomSheet::class.java.name
            )
        }
//            else if (blockedItem!=null && result ==true){
//                Toasty.error(this, "Domain unavailable select other", Toast.LENGTH_SHORT).show()
//            }
        else{
            Toasty.error(this, "No domain selected ", Toast.LENGTH_SHORT).show()
        }
    }
}