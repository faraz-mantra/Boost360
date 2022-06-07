package com.boost.marketplace.ui.details.domain

import android.app.ProgressDialog
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.CustomDomain.Domain
import com.boost.dbcenterapi.data.api_model.CustomDomain.DomainRequest
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.adapter.CustomDomainListAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityCustomDomainBinding
import com.framework.utils.hideKeyBoard


class CustomDomainActivity : AppBaseActivity<ActivityCustomDomainBinding, CustomDomainViewModel>() {

    lateinit var customDomainListAdapter: CustomDomainListAdapter
    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
    var experienceCode: String? = null
    var fpid: String? = null
    var totalFreeItemList: List<Domain>? = null
    var addonDetails: FeaturesModel? = null
    var itemInCartStatus = false

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
        addonDetails?.name = intent.getStringExtra("Addon Name")
        addonDetails?.price = intent.getStringExtra("Addon Price")?.toDouble()!!
        addonDetails?.discount_percent = intent.getStringExtra("Addon Discounted Price")!!.toInt()
       // intent.getStringExtra("Addon Discount %")
       // addonDetails?.va =  intent.getStringExtra("Addon Validity")
       addonDetails?.boost_widget_key = intent.getStringExtra("Addon Feature Key")!!







        viewModel.setApplicationLifecycle(application, this)
        viewModel = ViewModelProviders.of(this).get(CustomDomainViewModel::class.java)
        customDomainListAdapter= CustomDomainListAdapter(this,ArrayList())
        progressDialog = ProgressDialog(this)
        prefs = SharedPrefs(this)

        binding?.help?.setOnClickListener {
            val dialogCard = CustomDomainHelpBottomSheet()
            dialogCard.show(this.supportFragmentManager, CustomDomainHelpBottomSheet::class.java.name)
        }
        binding?.btnSelect?.setOnClickListener {
            val dialogCard = ConfirmedCustomDomainBottomSheet()
            dialogCard.show(this.supportFragmentManager, ConfirmedCustomDomainBottomSheet::class.java.name)
        }
        binding?.btnSelectDomain?.setOnClickListener {
            val dialogCard = ConfirmedCustomDomainBottomSheet()
            dialogCard.show(this.supportFragmentManager, ConfirmedCustomDomainBottomSheet::class.java.name)
        }
        binding?.tv3?.setOnClickListener {
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

        binding?.etDomain?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!=null && p0?.length!! >1){
                    binding?.searchBtn?.visibility=View.VISIBLE
                    binding?.searchBtn?.setOnClickListener {
                        hideKeyBoard()
                        binding?.searchBtn?.visibility=View.GONE
                        binding?.tvTitle?.text="Search results"
                        updateAllItemBySearchValue(p0.toString())
                    }
                    binding?.ivCross?.visibility=View.VISIBLE
                } else{
                    binding?.tvTitle?.text="Suggested domains for you"
                    binding?.searchBtn?.visibility=View.GONE
                    binding?.searchResults?.visibility=View.GONE
                    binding?.ivCross?.visibility=View.GONE
                    totalFreeItemList?.let { updateFreeAddonsRecycler(it) }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding?.ivCross?.setOnClickListener {
            binding?.ivCross?.visibility=View.GONE
            binding?.searchResults?.visibility=View.GONE
            binding?.tvTitle?.text="Suggested domains for you"
            totalFreeItemList?.let { updateFreeAddonsRecycler(it) }
            binding?.etDomain?.text= null
            binding?.layoutAvailable?.visibility=View.GONE
            binding?.layoutNotAvailable?.visibility=View.GONE
        }

        binding?.tvSkip?.setOnClickListener {
            if (!itemInCartStatus) {
                if (addonDetails == null) {
                            prefs.storeCartOrderInfo(null)
                            viewModel.addItemToCart1(addonDetails!!, this)
                            val event_attributes: HashMap<String, Any> = HashMap()
                            addonDetails!!.name?.let { it1 -> event_attributes.put("Addon Name", it1) }
                            event_attributes.put("Addon Price", addonDetails!!.price)
//                            event_attributes.put(
//                                "Addon Discounted Price",
//                                getDiscountedPrice(addonDetails!!.price, addonDetails!!.discount_percent)
//                            )
                           // event_attributes.put("Addon Discount %", addonDetails!!.discount_percent)
                          //  event_attributes.put("Addon Validity", 1)
                            event_attributes.put("Addon Feature Key", addonDetails!!.boost_widget_key)
//                            addonDetails!!.target_business_usecase?.let { it1 ->
//                                event_attributes.put(
//                                    "Addon Tag",
//                                    it1
//                                )
//                            }
//                            WebEngageController.trackEvent(
//                                ADDONS_MARKETPLACE_FEATURE_ADDED_TO_CART,
//                                ADDONS_MARKETPLACE,
//                                event_attributes
//                            )
//                            if (addonDetails!!.feature_code == "CUSTOM_PAYMENTGATEWAY")
//                                WebEngageController.trackEvent(
//                                    SELF_BRANDED_PAYMENT_GATEWAY_REQUESTED,
//                                    SELF_BRANDED_PAYMENT_GATEWAY,
//                                    NO_EVENT_VALUE
//                                )
//                            badgeNumber = badgeNumber + 1
//
//                            Constants.CART_VALUE = badgeNumber
//
//
//                            add_item_to_cart.background = ContextCompat.getDrawable(
//                                applicationContext,
//                                R.drawable.grey_button_click_effect
//                            )
//                            add_item_to_cart.setTextColor(getResources().getColor(R.color.tv_color_BB))
//                            add_item_to_cart.text = getString(R.string.added_to_cart)
                            itemInCartStatus = true




                }
            }
        }
    }

    private fun loadData() {
        experienceCode?.let { DomainRequest(clientid, it) }?.let { viewModel.GetSuggestedDomains(it) }
    }

    private fun initMVVM() {
        viewModel.updateResult().observe(this, androidx.lifecycle.Observer {
            totalFreeItemList=it.domains
            updateFreeAddonsRecycler(it.domains)
        })

        viewModel.updatesLoader().observe(this, androidx.lifecycle.Observer {
            if (it) {
                binding?.scrollView?.visibility=View.GONE
                binding?.shimmerViewDomain?.visibility=View.VISIBLE
            } else {
                binding?.scrollView?.visibility=View.VISIBLE
                binding?.shimmerViewDomain?.visibility=View.GONE
            }
        })
    }

    fun initRecyclerView() {
        val gridLayoutManager = LinearLayoutManager(this )
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.rvCustomDomain?.apply {
            layoutManager = gridLayoutManager
            binding?.rvCustomDomain?.adapter = customDomainListAdapter
        }
    }

    private fun updateFreeAddonsRecycler(list: List<Domain>) {
        customDomainListAdapter.addupdates(list)
        customDomainListAdapter.notifyDataSetChanged()
        binding?.rvCustomDomain?.setFocusable(false)
    }

    fun updateAllItemBySearchValue(searchValue: String){
        val freeitemList: java.util.ArrayList<Domain> = arrayListOf()
        for(singleFreeFeature in totalFreeItemList!!){
            if(singleFreeFeature.name.lowercase().indexOf(searchValue.lowercase()) != -1) {
                binding?.tvTitle?.text="Search results"
                freeitemList.add(singleFreeFeature)

              if ( singleFreeFeature.name.lowercase().equals(searchValue.lowercase()) && searchValue.length >7 ) {
                  if (singleFreeFeature.isAvailable){
                      binding?.layoutAvailable?.visibility=View.VISIBLE
                      binding?.tv1?.text=singleFreeFeature.name
                  }
                  else {
                      binding?.layoutAvailable?.visibility=View.GONE
                      binding?.layoutNotAvailable?.visibility=View.VISIBLE
                      binding?.tv?.text=singleFreeFeature.name
                  }
              }
                else{
                  binding?.layoutAvailable?.visibility=View.GONE
                  binding?.layoutNotAvailable?.visibility=View.GONE
              }
                updateFreeAddonsRecycler(freeitemList)
            }
            else{
                binding?.tvTitle?.visibility=View.VISIBLE
                updateFreeAddonsRecycler(totalFreeItemList!!)
            }
        }
        updateFreeAddonsRecycler(freeitemList)
    }

    private fun getDiscountedPrice(price: Double, discountPercent: Int): Double {
        return price - ((discountPercent / 100) * price)
    }
}