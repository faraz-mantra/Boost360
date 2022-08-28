package com.boost.marketplace.ui.My_Plan

import android.annotation.SuppressLint
import android.app.Application
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.appservice.utils.WebEngageController
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.Adapters.FreeAddonsAdapter
import com.boost.marketplace.Adapters.PaidAddonsAdapter
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMyCurrentPlanBinding
import com.boost.marketplace.ui.History_Orders.HistoryOrdersActivity
import com.boost.marketplace.ui.browse.BrowseFeaturesActivity
import com.boost.marketplace.ui.popup.myplan.MyPlanBottomSheet
import com.boost.marketplace.ui.videos.HelpVideosBottomSheet
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.framework.webengageconstant.ADDONS_MARKETPLACE_MY_ADDONS_LOADED
import com.framework.webengageconstant.MY_ADDONS
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_my_current_plan.*


class MyCurrentPlanActivity :
    AppBaseActivity<ActivityMyCurrentPlanBinding, MyCurrentPlanViewModel>(),
    com.boost.marketplace.interfaces.MyAddonsListener {

    lateinit var freeAddonsAdapter: FreeAddonsAdapter
    lateinit var paidAddonsAdapter: PaidAddonsAdapter
    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
    var experienceCode: String? = null
    var screenType: String? = null
    var fpName: String? = null
    var fpid: String? = null
    var fpTag: String? = null
    var email: String? = null
    var mobileNo: String? = null
    var profileUrl: String? = null
    var accountType: String? = null
    var isOpenCardFragment: Boolean = false
    var isBackCart: Boolean = false
    var isOpenHomeFragment: Boolean = false
    var isOpenAddOnsFragment: Boolean = false
    var deepLinkViewType: String = ""
    var deepLinkDay: Int = 7
    var isDeepLink: Boolean = false
    var userPurchsedWidgets = ArrayList<String>()
    var totalActiveFreeWidgetCount = 0
    var totalActivePremiumWidgetCount = 0
    var totalFreeItemList: List<FeaturesModel>? = null
    var totalPaidItemList: List<FeaturesModel>? = null

    lateinit var progressDialog: ProgressDialog
    var purchasedPackages = ArrayList<String>()

    companion object {
        fun newInstance() = MyCurrentPlanActivity()
    }

    override fun getLayout(): Int {
        return R.layout.activity_my_current_plan
    }

    override fun getViewModelClass(): Class<MyCurrentPlanViewModel> {
        return MyCurrentPlanViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        deepLinkViewType = intent.getStringExtra("deepLinkViewType") ?: ""
        deepLinkDay = intent.getIntExtra("deepLinkDay", 7)
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
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()
        viewModel.setApplicationLifecycle(application, this)
        viewModel = ViewModelProviders.of(this)
            .get(MyCurrentPlanViewModel::class.java)
        progressDialog = ProgressDialog(this)
        var purchasedPack = intent.extras?.getStringArrayList("userPurchsedWidgets")
        if (purchasedPack != null) {
            purchasedPackages = purchasedPack
        }
        freeAddonsAdapter = FreeAddonsAdapter(this, ArrayList(), this)
        paidAddonsAdapter = PaidAddonsAdapter(this, ArrayList(), this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(com.boost.cart.R.color.common_text_color))
        }
        loadData()
        initMVVM()
        initializeFreeAddonsRecyclerView()
        initializePaidAddonsRecyclerView()

        addons_back.setOnClickListener {
            super.onBackPressed()
        }

        binding?.history?.setOnClickListener {
            val intent = Intent(this, HistoryOrdersActivity::class.java)
            intent.putExtra("fpid", fpid)
            startActivity(intent)

        }

        binding?.renwhistry?.setOnClickListener {
            val intent = Intent(this, HistoryOrdersActivity::class.java)
            intent.putExtra("fpid", fpid)
            startActivity(intent)
        }

        WebEngageController.trackEvent(
            ADDONS_MARKETPLACE_MY_ADDONS_LOADED,
            MY_ADDONS,
            NO_EVENT_VALUE
        )

        search_icon.setOnClickListener {
            search_icon.visibility = View.GONE
            search_layout.visibility = View.VISIBLE
        }

        clear_text.setOnClickListener {
            search_value.setText("")
            search_icon.visibility = View.VISIBLE
            search_layout.visibility = View.GONE
        }

        search_value.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0?.length!! > 3) {
                    updateAllItemBySearchValue(p0.toString())
                } else {
                    updateFreeAddonsRecycler(totalFreeItemList!!)
                    updatePaidAddonsRecycler(totalPaidItemList!!)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        help.setOnClickListener {
            val videoshelp = HelpVideosBottomSheet()
            videoshelp.show(this.supportFragmentManager, HelpVideosBottomSheet::class.java.name)
        }

        binding?.btnBrowseFeature?.setOnClickListener {
            val intent = Intent(this, BrowseFeaturesActivity::class.java)
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

        binding?.arrowBtn?.setOnClickListener {
            cardViewVisibilty()
        }
        binding?.paidAddonsTitle?.setOnClickListener {
            cardViewVisibilty()
        }

        binding?.arrowBtn1?.setOnClickListener {
            cardView1Visibilty()
        }

        binding?.paidAddonsTitle1?.setOnClickListener {
            cardView1Visibilty()
        }

    }

    fun updateAllItemBySearchValue(searchValue: String) {
        var freeitemList: java.util.ArrayList<FeaturesModel> = arrayListOf()
        var paiditemList: java.util.ArrayList<FeaturesModel> = arrayListOf()

        for (singleFreeFeature in totalFreeItemList!!) {
            if (singleFreeFeature.name?.lowercase()?.indexOf(searchValue.lowercase()) != -1) {
                freeitemList.add(singleFreeFeature)
            }
        }
        updateFreeAddonsRecycler(freeitemList)

        for (singlePaidFeature in totalFreeItemList!!) {
            if (singlePaidFeature.name?.lowercase()?.indexOf(searchValue.lowercase()) != -1) {
                paiditemList.add(singlePaidFeature)
            }
        }
        updatePaidAddonsRecycler(paiditemList)
    }

    private fun cardView1Visibilty() {
        if (binding?.expandableView1?.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(binding?.cardView1, AutoTransition())
            binding!!.expandableView1.visibility = View.VISIBLE
            binding?.arrowBtn1?.animate()?.rotation(0f)?.start()
        } else {
            TransitionManager.beginDelayedTransition(binding?.cardView1, AutoTransition())
            binding?.expandableView1?.visibility = View.GONE
            binding?.arrowBtn1?.animate()?.rotation(180f)?.start()
        }
    }

    private fun cardViewVisibilty() {
        if (binding?.expandableView?.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(binding?.cardView, AutoTransition())
            binding?.expandableView!!.visibility = View.VISIBLE
            binding?.arrowBtn?.animate()?.rotation(0f)?.start()
        } else {
            TransitionManager.beginDelayedTransition(binding?.cardView, AutoTransition())
            binding?.expandableView?.visibility = View.GONE
            binding?.arrowBtn?.animate()?.rotation(180f)?.start()
        }
    }

    private fun loadData() {
        try {
//            viewModel.loadPurchasedItems(
//                intent.getStringExtra("fpid") ?: "",
//                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
//            )
            viewModel.myPlanV3Status(
                intent.getStringExtra("fpid") ?: "",
                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
            )
        } catch (e: Exception) {
            SentryController.captureException(e)
        }
    }

    fun getAccessToken(): String {
        return UserSessionManager(this).getAccessTokenAuth()?.barrierToken() ?: ""
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun initMVVM() {
        viewModel.myplanResultV3().observe(this, androidx.lifecycle.Observer {
            var inActiveList =
                ArrayList<String>()
            var activeList =
                ArrayList<String>()

            for (singleItem in it.Result) {
                if (singleItem.ActionNeeded != null && singleItem.FeatureDetails != null) {
                    if (singleItem.ActionNeeded.ActionNeeded != 0 && singleItem.FeatureDetails.FeatureState != 7) {
                        inActiveList.add(singleItem.FeatureDetails.FeatureKey)
                    } else if (singleItem.ActionNeeded.ActionNeeded == 0 && singleItem.FeatureDetails.FeatureState == 1) {
                        activeList.add(singleItem.FeatureDetails.FeatureKey)
                    }
                }
            }
            //Inactive features
            compositeDisposable.add(
                AppDatabase.getInstance(Application())!!
                    .featuresDao()
                    .getallActiveFeatures1(inActiveList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess {
                        if (it != null) {
                            if (it.size > 0) {
                                if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
                                    binding?.shimmerViewHistory?.stopShimmer()
                                    binding?.shimmerViewHistory?.visibility = View.GONE
                                    binding?.nestedscroll?.visibility = View.VISIBLE
                                }
                                binding?.expandableView?.visibility = View.VISIBLE
                                updateFreeAddonsRecycler(it)
                                binding?.paidTitle?.text = " ${it.size} Inactive features"
                            } else {
                                if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
                                    binding?.shimmerViewHistory?.stopShimmer()
                                    binding?.shimmerViewHistory?.visibility = View.GONE
                                }
                                binding?.expandableView?.visibility = View.INVISIBLE
                            }
                        }
                    }
                    .doOnError {
                        Log.i("insertAllFeatures", "Failed")
                    }
                    .subscribe())

            //Active features
            compositeDisposable.add(
                AppDatabase.getInstance(Application())!!
                    .featuresDao()
                    .getallActiveFeatures1(activeList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess {
                        if (it != null) {
                            if (it.size > 0) {
                                if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
                                    binding?.shimmerViewHistory?.stopShimmer()
                                    binding?.shimmerViewHistory?.visibility = View.GONE
                                    binding?.nestedscroll?.visibility = View.VISIBLE
                                }
                                binding?.expandableView?.visibility = View.VISIBLE
                                updatePaidAddonsRecycler(it)
                                binding?.paidTitle1?.text = " ${it.size} Active features"
                            } else {
                                if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
                                    binding?.shimmerViewHistory?.stopShimmer()
                                    binding?.shimmerViewHistory?.visibility = View.GONE
                                }
                                binding?.emptyFeatures?.visibility = View.VISIBLE
                                binding?.paidTitle1?.text = "No add-ons active."
                            }
                        }
                    }
                    .doOnError {
                        Log.i("insertAllFeatures", "Failed")
                    }
                    .subscribe())

        })

//        viewModel.activeWidgetCount().observe(this, androidx.lifecycle.Observer {
//            totalPaidItemList = it
//
////            for (singleItem in it) {
////                val ans = singleItem.feature_code.toString()
////                viewModel.edgecases(
////                    intent.getStringExtra("fpid") ?: "",
////                    "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21", ans
////                )
////            }
//
//            totalActivePremiumWidgetCount = totalPaidItemList!!.size
//            val paidItemsCount = totalPaidItemList!!.size
//            if (paidItemsCount != null && paidItemsCount > 0) {
//                if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
//                    binding?.shimmerViewHistory?.stopShimmer()
//                    binding?.shimmerViewHistory?.visibility = View.GONE
//                    binding?.nestedscroll?.visibility = View.VISIBLE
//                }
//                binding?.paidTitle1?.text = totalPaidItemList!!.size.toString() + " Active features"
//            } else {
//                if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
//                    binding?.shimmerViewHistory?.stopShimmer()
//                    binding?.shimmerViewHistory?.visibility = View.GONE
//                }
//                binding?.paidTitle1?.text = "No add-ons active."
//                binding?.emptyFeatures?.visibility = View.VISIBLE
//            }
//
//            if (totalPaidItemList != null) {
//                if (totalPaidItemList!!.size > 1) {
//                    updatePaidAddonsRecycler(totalPaidItemList!!)
//                } else {
//                    updatePaidAddonsRecycler(totalPaidItemList!!)
//                }
//            }
//            updatePaidAddonsRecycler(it)
//        })
//
//
//        viewModel.inActiveWidgetCount().observe(this, androidx.lifecycle.Observer {
//
//            totalFreeItemList = it
//            totalActiveFreeWidgetCount = totalFreeItemList!!.size
//            binding?.paidTitle?.text = totalActiveFreeWidgetCount.toString() + " Inactive features"
//
//            if (totalFreeItemList != null) {
//                if (totalFreeItemList!!.size > 0) {
//                    if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
//                        binding?.shimmerViewHistory?.stopShimmer()
//                        binding?.shimmerViewHistory?.visibility = View.GONE
//                        binding?.nestedscroll?.visibility = View.VISIBLE
//                    }
//                    updateFreeAddonsRecycler(totalFreeItemList!!)
//                    binding?.expandableView?.visibility = View.VISIBLE
//                } else {
//                    if (binding?.shimmerViewHistory?.isShimmerStarted == true) {
//                        binding?.shimmerViewHistory?.stopShimmer()
//                        binding?.shimmerViewHistory?.visibility = View.GONE
//                    }
//                    binding?.expandableView?.visibility = View.INVISIBLE
//                    updateFreeAddonsRecycler(totalFreeItemList!!)
//                }
//            }
//            updateFreeAddonsRecycler(totalFreeItemList!!)
//        })
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

    private fun updateFreeAddonsRecycler(list: List<FeaturesModel>) {
        freeAddonsAdapter.addupdates(list)
        binding?.recycler?.adapter = freeAddonsAdapter
        freeAddonsAdapter.notifyDataSetChanged()
    }

    private fun updatePaidAddonsRecycler(list: List<FeaturesModel>) {
        binding?.premiumRecycler?.visibility = View.VISIBLE
        paidAddonsAdapter.addupdates(list)
        paidAddonsAdapter.notifyDataSetChanged()
    }

    private fun initializeFreeAddonsRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.recycler?.apply {
            layoutManager = linearLayoutManager
            binding?.recycler?.adapter = freeAddonsAdapter
        }
    }

    private fun initializePaidAddonsRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.premiumRecycler?.apply {
            layoutManager = linearLayoutManager
            binding?.premiumRecycler?.adapter = paidAddonsAdapter
        }
    }

    override fun onFreeAddonsClicked(item: FeaturesModel) {
        val dialogCard = MyPlanBottomSheet()
        val args = Bundle()
        args.putString("fpid", fpid)
        args.putString("bundleData", Gson().toJson(item))
        dialogCard.arguments = args
        dialogCard.show(
            this@MyCurrentPlanActivity.supportFragmentManager,
            MyPlanBottomSheet::class.java.name
        )
    }

    override fun onPaidAddonsClicked(item: FeaturesModel) {
        val dialogCard = MyPlanBottomSheet()
        val args = Bundle()
        args.putString("fpid", fpid)
        args.putString("bundleData", Gson().toJson(item))
        dialogCard.arguments = args
        dialogCard.show(
            this@MyCurrentPlanActivity.supportFragmentManager,
            MyPlanBottomSheet::class.java.name
        )
    }
}