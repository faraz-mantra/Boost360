package com.boost.marketplace.ui.My_Plan

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.interfaces.MyAddonsListener
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.Adapters.FreeAddonsAdapter
import com.boost.marketplace.Adapters.PaidAddonsAdapter
import com.boost.marketplace.R
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityMyCurrentPlanBinding
import com.boost.marketplace.interfaces.CompareBackListener
import com.boost.marketplace.ui.History_Orders.HistoryOrdersActivity
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import kotlinx.android.synthetic.main.activity_my_current_plan.*

//import kotlinx.android.synthetic.main.activity_my_current_plan.*
//import kotlinx.android.synthetic.main.activity_my_current_plan.all_addons_view_layout


class MyCurrentPlanActivity :
    AppBaseActivity<ActivityMyCurrentPlanBinding, MyCurrentPlanViewModel>(),
    CompareBackListener, MyAddonsListener, com.boost.marketplace.interfaces.MyAddonsListener {


    lateinit var freeAddonsAdapter: FreeAddonsAdapter
    lateinit var paidAddonsAdapter: PaidAddonsAdapter

    //  lateinit var myAddonsViewModelFactory: MyAddonsViewModelFactory

   var fpid: String? = null

//    var totalActiveWidgetCount = 0
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

//        myAddonsViewModelFactory =
//            MyAddonsViewModelFactory(requireNotNull(this.application))

        fpid = intent.getStringExtra("fpid")
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

        loadData()
        initMVVM()
        initializeFreeAddonsRecyclerView()
        initializePaidAddonsRecyclerView()
        // shimmer_view_paidaddon.startShimmer()
        //   shimmer_view_freeaddon.startShimmer()
//        val profileURL = (activity as UpgradeActivity).profileUrl
//
//        if (profileURL.isNullOrEmpty() || profileURL.length < 2) {
//            Glide.with(this)
//                .load(R.drawable.group)
//                .into(merchant_logo)
//        } else {
//            Glide.with(this)
//                .load(profileURL)
//                .into(merchant_logo)
//        }

        //   top_line_view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)


//        Glide.with(this).load(R.drawable.back_beau)
//                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
//                .into(back_image)


//        verifybtn1.setOnClickListener {
//            if (add_remove_layout.visibility == View.VISIBLE) {
//                add_remove_layout.visibility = View.GONE
//            } else {
//                val args = Bundle()
//                args.putStringArrayList(
//                    "userPurchsedWidgets",
//                    arguments?.getStringArrayList("userPurchsedWidgets")
//                )
//                (activity as UpgradeActivity).addFragmentHome(
//                    ViewAllFeaturesFragment.newInstance(),
//                    Constants.VIEW_ALL_FEATURE, args
//                )
//            }
//        }

//        view_orders_history.setOnClickListener {
//            //            Toasty.info(requireContext(), R.string.feature_coming_soon).show()
//            (activity as UpgradeActivity).addFragment(HistoryFragment.newInstance(), HISTORY_FRAGMENT)
//        }
//
        addons_back.setOnClickListener {
            super.onBackPressed()
        }

//        paid_menu_layout.setOnClickListener {
//            if (add_remove_layout.visibility == View.VISIBLE) {
//                add_remove_layout.visibility = View.GONE
//            } else {
//                add_remove_layout.visibility = View.VISIBLE
//            }
//        }

        binding?.allAddonsViewLayout?.setOnClickListener {
//            if (add_remove_layout.visibility == View.VISIBLE) {
//                add_remove_layout.visibility = View.GONE
//            }
        }

        binding?.history?.setOnClickListener {
            val intent= Intent(this, HistoryOrdersActivity::class.java)
            intent.putExtra("fpid",fpid)
            startActivity(intent)

        }

//        add_paid_addons.setOnClickListener {
//            add_remove_layout.visibility = View.GONE
//            val args = Bundle()
//            args.putStringArrayList(
//                "userPurchsedWidgets",
//                arguments?.getStringArrayList("userPurchsedWidgets")
//            )
//            (activity as UpgradeActivity).addFragmentHome(
//                ViewAllFeaturesFragment.newInstance(),
//                VIEW_ALL_FEATURE,
//                args
//            )
//        }

//        remove_paid_addons.setOnClickListener {
//            add_remove_layout.visibility = View.GONE
////            (activity as UpgradeActivity).addFragment(RemoveAddonsFragment.newInstance(), REMOVE_ADDONS_FRAGMENT)
//            Toasty.warning(requireContext(), R.string.feature_coming_soon).show()
//        }

//        read_more_less_free_addons.setOnClickListener {
//            if (add_remove_layout.visibility == View.VISIBLE) {
//                add_remove_layout.visibility = View.GONE
//            } else {
//                if (totalFreeItemList != null) {
//                    if (freeaddonsSeeMoreStatus && totalFreeItemList!!.size > 6) {
//                        val lessList = totalFreeItemList!!.subList(0, 6)
//                        updateFreeAddonsRecycler(lessList)
//                        freeaddonsSeeMoreStatus = false
//                        read_more_less_text_free_addons.setText("See more")
//                        read_more_less_text_free_addons.setCompoundDrawablesWithIntrinsicBounds(
//                            null,
//                            null,
//                            ContextCompat.getDrawable(requireContext(), R.drawable.addons_arrow_down),
//                            null
//                        )
//                    } else {
//                        updateFreeAddonsRecycler(totalFreeItemList!!)
//                        freeaddonsSeeMoreStatus = true
//                        read_more_less_text_free_addons.setText("See less")
//                        read_more_less_text_free_addons.setCompoundDrawablesWithIntrinsicBounds(
//                            null,
//                            null,
//                            ContextCompat.getDrawable(requireContext(), R.drawable.addons_arrow_up),
//                            null
//                        )
//
//
//                        WebEngageController.trackEvent(
//                            ADDONS_MARKETPLACE_FREE_ADDONS_SEE_MORE,
//                            FREE_ADDONS,
//                            NO_EVENT_VALUE
//                        )
//                    }
//                }
//            }
//        }


//        read_more_less_paid_addons.setOnClickListener {
//            if (add_remove_layout.visibility == View.VISIBLE) {
//                add_remove_layout.visibility = View.GONE
//            } else {
//                if (totalPaidItemList != null) {
//                    if (paidaddonsSeeMoreStatus && totalPaidItemList!!.size > 4) {
//                        val lessList = totalPaidItemList!!.subList(0, 4)
//                        updatePaidAddonsRecycler(lessList)
//                        paidaddonsSeeMoreStatus = false
//                        read_more_less_text_paid_addons.setText("See more")
//                        read_more_less_text_paid_addons.setCompoundDrawablesWithIntrinsicBounds(
//                            null,
//                            null,
//                            ContextCompat.getDrawable(requireContext(), R.drawable.addons_arrow_down),
//                            null
//                        )
//                    } else {
//                        updatePaidAddonsRecycler(totalPaidItemList!!)
//                        paidaddonsSeeMoreStatus = true
//                        read_more_less_text_paid_addons.setText("See less")
//                        read_more_less_text_paid_addons.setCompoundDrawablesWithIntrinsicBounds(
//                            null,
//                            null,
//                            ContextCompat.getDrawable(requireContext(), R.drawable.addons_arrow_up),
//                            null
//                        )
//                    }
//                }
//            }
//        }

        //   WebEngageController.trackEvent(ADDONS_MARKETPLACE_MY_ADDONS_LOADED, MY_ADDONS, NO_EVENT_VALUE)

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
                if(p0!=null && p0?.length>3){
                    updateAllItemBySearchValue(p0.toString())
                } else{
                    updateFreeAddonsRecycler(totalFreeItemList!!)
                    updatePaidAddonsRecycler(totalPaidItemList!!)
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding?.arrowBtn?.setOnClickListener {
            cardViewVisibilty()
        }
        binding?.cardView?.setOnClickListener {
            cardViewVisibilty()
        }

        binding?.arrowBtn1?.setOnClickListener {
            cardView1Visibilty()
        }

        binding?.cardView1?.setOnClickListener {
            cardView1Visibilty()
        }

    }

    fun updateAllItemBySearchValue(searchValue: String){
        var freeitemList: java.util.ArrayList<FeaturesModel> = arrayListOf()
        var paiditemList: java.util.ArrayList<FeaturesModel> = arrayListOf()

        for(singleFreeFeature in totalFreeItemList!!){
            if(singleFreeFeature.name?.lowercase()?.indexOf(searchValue.lowercase()) != -1 ){
                freeitemList.add(singleFreeFeature)
            }
        }
        updateFreeAddonsRecycler(freeitemList)

        for(singlePaidFeature in totalFreeItemList!!){
            if(singlePaidFeature.name?.lowercase()?.indexOf(searchValue.lowercase()) != -1 ){
                paiditemList.add(singlePaidFeature)
            }
        }
        updatePaidAddonsRecycler(paiditemList)
    }

    private fun cardView1Visibilty() {
        if (binding?.expandableView1?.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(binding?.cardView1, AutoTransition())
            binding!!.expandableView1.visibility = View.VISIBLE
            binding?.arrowBtn1?.animate()?.rotation(180f)?.start()
        } else {
            TransitionManager.beginDelayedTransition(binding?.cardView1, AutoTransition())
            binding?.expandableView1?.visibility = View.GONE
            binding?.arrowBtn1?.animate()?.rotation(0f)?.start()
        }
    }


    private fun cardViewVisibilty() {
        if (binding?.expandableView?.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(binding?.cardView, AutoTransition())
            binding?.expandableView!!.visibility = View.VISIBLE
            binding?.arrowBtn?.animate()?.rotation(180f)?.start()
        } else {
            TransitionManager.beginDelayedTransition(binding?.cardView, AutoTransition())
            binding?.expandableView?.visibility = View.GONE
            binding?.arrowBtn?.animate()?.rotation(0f)?.start()
        }
    }

    private fun loadData() {
        try {
            viewModel.loadUpdates(
                getAccessToken(),
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
        viewModel.getActiveFreeWidgets().observe(this, androidx.lifecycle.Observer {
            totalFreeItemList = it
            totalActiveFreeWidgetCount = totalFreeItemList!!.size
//            totalActiveWidgetCount = totalActiveFreeWidgetCount + totalActivePremiumWidgetCount

            binding?.paidTitle?.text = totalActiveFreeWidgetCount.toString() + " Free Add-ons"

            initializeFreeAddonsRecyclerView()
            initializePaidAddonsRecyclerView()

            if (totalFreeItemList != null) {
                if (totalFreeItemList!!.size > 0) {
//                    if (shimmer_view_freeaddon.isShimmerStarted) {
//                        shimmer_view_freeaddon.stopShimmer()
//                        shimmer_view_freeaddon.visibility = View.GONE
//                    }
                    updateFreeAddonsRecycler(totalFreeItemList!!)
                    binding?.expandableView?.visibility = View.VISIBLE
//                    read_more_less_free_addons.visibility = View.VISIBLE
                } else {
//                    if (shimmer_view_freeaddon.isShimmerStarted) {
//                        shimmer_view_freeaddon.stopShimmer()
//                        shimmer_view_freeaddon.visibility = View.GONE
//                    }
                    binding?.expandableView?.visibility = View.INVISIBLE
//                    read_more_less_free_addons.visibility = View.GONE
                    updateFreeAddonsRecycler(totalFreeItemList!!)
                }
            }
        })
        viewModel.getActivePremiumWidgets().observe(this, androidx.lifecycle.Observer {
            Log.i("getActiveWidgets", it.toString())
            totalPaidItemList = it
            totalActivePremiumWidgetCount = totalPaidItemList!!.size
//            totalActiveWidgetCount = totalActiveFreeWidgetCount + totalActivePremiumWidgetCount

            val paidItemsCount = totalPaidItemList!!.size

            if (paidItemsCount != null && paidItemsCount > 0) {
//                if (shimmer_view_paidaddon.isShimmerStarted) {
//                    shimmer_view_paidaddon.stopShimmer()
//                    shimmer_view_paidaddon.visibility = View.GONE
//                }
                binding?.paidTitle1?.text = totalPaidItemList!!.size.toString() + " Premium add-ons"
                binding?.paidSubtitle1?.text =
                    totalPaidItemList!!.size.toString() + " Activated, 0 Syncing and 0 needs Attention"
//                read_more_less_paid_addons.visibility = View.VISIBLE
//                premium_account_flag.visibility = View.VISIBLE
            } else {
//                if (shimmer_view_paidaddon.isShimmerStarted) {
//                    shimmer_view_paidaddon.stopShimmer()
//                    shimmer_view_paidaddon.visibility = View.GONE
//                }
                binding?.paidTitle1?.text = "No Premium add-ons active."
                binding?.paidSubtitle1?.text = "check out the recommended add-ons for your business"
//                read_more_less_paid_addons.visibility = View.GONE
            }

            if (totalPaidItemList != null) {
                if (totalPaidItemList!!.size > 1) {
                    updatePaidAddonsRecycler(totalPaidItemList!!)
//                    myaddons_view2.visibility = View.VISIBLE
//                    read_more_less_paid_addons.visibility = View.VISIBLE
                } else {
//                    myaddons_view2.visibility = View.INVISIBLE
//                    read_more_less_paid_addons.visibility = View.GONE
                    updatePaidAddonsRecycler(totalPaidItemList!!)
                }
            }
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
    }

//    private fun setHeadlineTexts() {
//        free_addons_name.setText("Currently using\n" + totalActiveWidgetCount + " add-ons")
//        bottom_free_addons.setText(totalActiveFreeWidgetCount.toString() + " free, " + totalActivePremiumWidgetCount.toString() + " premium")
//        binding?.paidTitle?.text = totalActiveFreeWidgetCount.toString() + " Free Add-ons"
//    }

    private fun updateFreeAddonsRecycler(list: List<FeaturesModel>) {
        freeAddonsAdapter.addupdates(list)
        binding?.recycler?.adapter = freeAddonsAdapter
        freeAddonsAdapter.notifyDataSetChanged()
    }

    private fun updatePaidAddonsRecycler(list: List<FeaturesModel>) {
        binding?.premiumRecycler?.visibility = View.VISIBLE
        paidAddonsAdapter.addupdates(list)
        binding?.premiumRecycler?.adapter = paidAddonsAdapter
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

    override fun onFreeAddonsClicked(v: View?) {
//        if (add_remove_layout.visibility == View.VISIBLE) {
//            add_remove_layout.visibility = View.GONE
//        } else {
//            val itemPosition = recycler_freeaddons.getChildAdapterPosition(v!!)
//
//        }
        val dialogCard = MyPlanBottomSheet()
        //  dialogCard.setData(getLocalSession(it), shareChannelText)
        dialogCard.show(this@MyCurrentPlanActivity.supportFragmentManager, MyPlanBottomSheet::class.java.name)
    }

    override fun onPaidAddonsClicked(v: View?) {

        val dialogCard = MyPlanBottomSheet()
        dialogCard.show(this@MyCurrentPlanActivity.supportFragmentManager, MyPlanBottomSheet::class.java.name)
    }

    override fun backComparePress() {

    }


}