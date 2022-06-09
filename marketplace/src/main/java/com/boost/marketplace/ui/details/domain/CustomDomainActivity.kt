package com.boost.marketplace.ui.details.domain

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.CartActivity
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
import com.framework.utils.hideKeyBoard
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class CustomDomainActivity : AppBaseActivity<ActivityCustomDomainBinding, CustomDomainViewModel>() {

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
    var allDomainsList: List<Domain>? = null
    var itemInCartStatus = false
    lateinit var customDomainListAdapter1: CustomDomainListAdapter1
    lateinit var customDomainListAdapter: CustomDomainListAdapter
    lateinit var singleAddon: FeaturesModel
    lateinit var progressDialog: ProgressDialog
    lateinit var prefs: SharedPrefs

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
        customDomainListAdapter1= CustomDomainListAdapter1(this,ArrayList())
        customDomainListAdapter = CustomDomainListAdapter(this, ArrayList())
        progressDialog = ProgressDialog(this)
        prefs = SharedPrefs(this)

        binding?.help?.setOnClickListener {
            val dialogCard = CustomDomainHelpBottomSheet()
            dialogCard.show(
                this.supportFragmentManager,
                CustomDomainHelpBottomSheet::class.java.name
            )
        }
//        binding?.btnSelect?.setOnClickListener {
//            val dialogCard = ConfirmedCustomDomainBottomSheet()
//            dialogCard.show(
//                this.supportFragmentManager,
//                ConfirmedCustomDomainBottomSheet::class.java.name
//            )
//        }
        binding?.btnSelectDomain?.setOnClickListener {
            val dialogCard = ConfirmedCustomDomainBottomSheet()
            dialogCard.show(
                this.supportFragmentManager,
                ConfirmedCustomDomainBottomSheet::class.java.name
            )
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

        loadData()
        initMVVM()
        initRecyclerView()
        initRecyclerView1()

        binding?.etDomain?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.length > 1) {
                    binding?.searchBtn?.visibility = View.VISIBLE
                    binding?.searchBtn?.setOnClickListener {
                        hideKeyBoard()
                        binding?.searchBtn?.visibility = View.GONE
                        binding?.tvSuggestedDomains?.text = "Search results"
                        updateItemBySearchValue(p0.toString())
                    }
                    binding?.ivCross?.visibility = View.VISIBLE
                } else {
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
            binding?.ivCross?.visibility = View.GONE
            binding?.searchResults?.visibility = View.GONE
            binding?.tvSuggestedDomains?.text = "Suggested domains for you"
            allDomainsList?.let { updateDomainsRecycler(it) }
            binding?.etDomain?.text = null
//            binding?.layoutAvailable?.visibility = View.GONE
//            binding?.layoutNotAvailable?.visibility = View.GONE
        }

        binding?.tvSkipTocart?.setOnClickListener {
            if (!itemInCartStatus) {
                if (singleAddon != null) {
                    prefs.storeCartOrderInfo(null)
                    viewModel.addItemToCart1(singleAddon, this)
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

    private fun loadData() {
        experienceCode?.let { DomainRequest(clientid, it) }
            ?.let { viewModel.GetSuggestedDomains(it) }
    }

    private fun initMVVM() {
        viewModel.updateResult().observe(this, androidx.lifecycle.Observer {
            allDomainsList = it.domains
            updateDomainsRecycler(it.domains)
            updateFreeAddonsRecycler1(it.domains)
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
//                    if (singleDomain.isAvailable) {
//                        binding?.layoutAvailable?.visibility = View.VISIBLE
//                        binding?.tv1?.text = singleDomain.name
//                    } else {
//                        binding?.layoutAvailable?.visibility = View.GONE
//                        binding?.layoutNotAvailable?.visibility = View.VISIBLE
//                        binding?.tv?.text = singleDomain.name
//                    }
                    binding?.rvCustomDomain1?.visibility=View.VISIBLE
                    paiditemList.add(singleDomain)
                    updateFreeAddonsRecycler1(paiditemList)
                    updateDomainsRecycler(freeitemList!!)
//                } else {
//                    binding?.layoutAvailable?.visibility = View.GONE
//                    binding?.layoutNotAvailable?.visibility = View.GONE
//                }
//                updateDomainsRecycler(freeitemList)
//                    updateFreeAddonsRecycler1(paiditemList)
            }
                //            else {
//                binding?.tvSuggestedDomains?.visibility = View.VISIBLE
//                updateDomainsRecycler(allDomainsList!!)
//            }
        }
//        updateDomainsRecycler(freeitemList)
//            updateFreeAddonsRecycler1(paiditemList)

//        var exactMatchList: ArrayList<String> = arrayListOf()
//        var everyMatchList: ArrayList<String> = arrayListOf()
//        var isMatching: Boolean = false
//        var searchChar: Char? = null
//        for (item in allDomainsList!!) {
//            for (i in searchValue) {
//                if (item.name.lowercase().indexOf(searchValue.lowercase())!=-1) {
//                    isMatching = true
//                    if (item.name.lowercase() != (searchValue.lowercase())) {
//                        isMatching = false
//                    }
//                    if (isMatching) {
//                        freeitemList.add(item)
//                    }
//                    break
//                }
//            }
//            if (item.equals(searchValue)) {
//                freeitemList.add(item)
//            }
//        }
//
//        var exactMatchList: ArrayList<String> = arrayListOf()
//        var everyMatchList: ArrayList<String> = arrayListOf()
//        var isMatching: Boolean = false
//        var searchChar: Char? = null
//        for (number in list) {
//            for (i in searchValue.indices) {
//                if (number.contains(searchValue[i])) {
//                    isMatching = true
//                    if (number.contains(searchValue)) {
//                        isMatching = false
//                    }
//                    if (isMatching) {
//                        everyMatchList.add(number)
//                    }
//                    break
//                }
//            }
//            if (number.contains(searchValue)) {
//                exactMatchList.add(number)
//            }
//
//        updateDomainsRecycler(freeitemList)
//        if (isMatching) {
//            binding?.rvCustomDomain1?.visibility = View.VISIBLE
//            updateFreeAddonsRecycler1(freeitemList)
//        }

    }


    private fun getDiscountedPrice(price: Double, discountPercent: Int): Double {
        return price - ((discountPercent / 100) * price)
    }
}