package com.appservice.ui.calltracking

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.databinding.ActivityVmnCallCardsV2Binding
import com.appservice.model.VmnCallModel
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.VmnCallsViewModel
import com.framework.constants.PremiumCode
import com.framework.constants.SupportVideoType
import com.framework.pref.Key_Preferences
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_CATEGORY
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.InAppReviewUtils
import com.framework.utils.showSnackBarNegative
import com.framework.utils.toArrayList
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases
import com.framework.webengageconstant.BUSINESS_CALLS
import com.framework.webengageconstant.EVENT_LABEL_BUSINESS_CALLS
import com.google.android.material.tabs.TabLayout
import com.google.gson.JsonObject
import com.onboarding.nowfloats.constant.IntentConstant
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by Admin on 27-04-2017.
 */
class VmnCallCardsActivityV2 : AppBaseActivity<ActivityVmnCallCardsV2Binding, VmnCallsViewModel>(),
    AppOnZeroCaseClicked {
    var seeMoreLessStatus = false
    var totalCallCount = 0
    var totalPotentialCallCount = 0
    var stopApiCall = false
    var allowCallPlayFlag // This flag allows only one audio to play at a time. True means an audio can be played.
            = false
    var headerList: ArrayList<VmnCallModel> = ArrayList<VmnCallModel>()
    var vmnCallAdapter: AppBaseRecyclerViewAdapter<VmnCallModel>? = null
    var selectedViewType = "ALL"
    private var offset = 0
    private var appFragmentZeroCase: AppFragmentZeroCase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appFragmentZeroCase = AppRequestZeroCaseBuilder(
            AppZeroCases.BUSINESS_CALLS,
            this,
            this,
            isPremium
        ).getRequest().build()
        addFragment(binding?.childContainer?.getId(), appFragmentZeroCase,false)
       // MixPanelController.track(MixPanelController.VMN_CALL_TRACKER, null)
        setSupportActionBar(binding?.toolbar)
        if (supportActionBar != null) {
            title = "Business Calls"
            supportActionBar!!.setDisplayShowHomeEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        WebEngageController.trackEvent(BUSINESS_CALLS, EVENT_LABEL_BUSINESS_CALLS, null)

        binding?.tvTrackedCall?.setText(
            getString(R.string.tracked_calls) + " " + session?.getFPDetails(
                Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER
            )
        )

        allowCallPlayFlag = true

        //tracking calls
        showTrackedCalls()
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.callRecycler?.layoutManager = linearLayoutManager
        binding?.callRecycler?.setHasFixedSize(true)
        vmnCallAdapter = AppBaseRecyclerViewAdapter(this, headerList)
        binding?.callRecycler?.adapter = vmnCallAdapter
        binding?.callRecycler?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val totalItemCount = linearLayoutManager.itemCount
                val lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition()
                if (lastVisibleItem >= totalItemCount - 2 && !stopApiCall) {
                    calls()
                }
            }
        })
        setOnClickListener(binding?.seeMoreLess,binding?.websiteHelper,
        binding?.phoneHelper,binding?.parentLayout,binding?.cardViewViewCalllog)

        websiteCallCount()
        if (isPremium) {
            nonEmptyView()
            calls()
        } else {
            emptyView()
        }
        /*vmnCallAdapter.setAllowAudioPlay(object : AllowAudioPlay() {
            fun allowAudioPlay(): Boolean {
                return allowCallPlayFlag
            }

            fun toggleAllowAudioPlayFlag(setValue: Boolean) {
                allowCallPlayFlag = setValue
            }
        })*/
    }

    private val isPremium: Boolean
        get() {
            val keys = session?.getStoreWidgets()
            return keys != null && keys.contains(PremiumCode.CALLTRACKER.value)
        }

    private fun showTrackedCalls() {
        binding?.tableLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    if (selectedViewType != "ALL") {
                        selectedViewType = "ALL"
                        updateRecyclerData(null)
                    }
                } else if (tab.position == 1) {
                    if (selectedViewType != "MISSED") {
                        selectedViewType = "MISSED"
                        updateRecyclerData(null)
                    }
                } else if (tab.position == 2) {
                    if (selectedViewType != "CONNECTED") {
                        selectedViewType = "CONNECTED"
                        updateRecyclerData(null)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    fun calls() {
            Log.i(TAG, "getCalls: function called")
            stopApiCall = true
            showProgress()
            val startOffset = offset.toString()
            val hashMap = HashMap<String,String?>()
            hashMap["clientId"] = clientId
            hashMap["fpid"] = if (session?.iSEnterprise.equals("true")) session?.fPParentId else session?.fPID

            hashMap["offset"] = startOffset
            hashMap["limit"] = 100.toString()
            hashMap["identifierType"] = if (session?.iSEnterprise?.equals("true") == true) "MULTI" else "SINGLE"
            viewModel.trackerCalls(hashMap).observe(this) {
                    Log.i(TAG, "getCalls success: ")
                val vmnCallModels = (it.anyResponse as? ArrayList<VmnCallModel>)
                    hideProgress()
                    if (vmnCallModels == null ||it.isSuccess().not()) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@observe
                    }else{
                        hideProgress()
                        stopApiCall = false
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val size = vmnCallModels.size
                    Log.v("getCalls", " $size")
                    stopApiCall = size < 100
                    updateRecyclerData(vmnCallModels)
                    if (size != 0) {
                        offset += 100
                    }
                    if (size < 1) {
                        emptyView()
                    } else {
                        nonEmptyView()
                    }
                }
        }

    private fun updateRecyclerData(newItems: ArrayList<VmnCallModel>?) {
        if (newItems != null) {
            val sizeOfList = headerList.size
            val listSize = newItems.size
            for (i in 0 until listSize) {
                val model: VmnCallModel = newItems[i]
                headerList.add(model)
                vmnCallAdapter?.notifyItemInserted(sizeOfList + i);
            }
        }
        Log.i(TAG, "updateRecyclerData: header list size " + getSelectedTypeList(headerList).size)
        vmnCallAdapter?.updateList(getSelectedTypeList(headerList))
    }

    private fun getSelectedTypeList(list: ArrayList<VmnCallModel>): ArrayList<VmnCallModel> {
        var selectedItems: ArrayList<VmnCallModel> = ArrayList<VmnCallModel>()
        when (selectedViewType) {
            "ALL" -> {
                selectedItems = list
            }
            "MISSED" -> {
                var i = 0
                while (i < list.size) {
                    if (list[i].callStatus.equals("MISSED")) {
                        selectedItems.add(list[i])
                    }
                    i++
                }
            }
            "CONNECTED" -> {
                var i = 0
                while (i < list.size) {
                    if (list[i].callStatus.equals("CONNECTED")) {
                        selectedItems.add(list[i])
                    }
                    i++
                }
            }
        }
        return selectedItems
    }

    private fun showEmptyScreen() {
        if (totalCallCount == 0) {
            binding?.emptyLayout?.root?.visibility = View.VISIBLE
            val imageView = binding?.emptyLayout?.imageItem
            val mMainText = binding?.emptyLayout?.mainText
            val mDescriptionText = binding?.emptyLayout?.descriptionText
            imageView?.setImageResource(R.drawable.icon_no_calls)
            mMainText?.text = "You have no call records yet."
            mDescriptionText?.text =
                "Your customers haven't contacted\nyou on your call tracking number yet."
        } else {
            findViewById<View>(R.id.calls_details_layout).visibility = View.VISIBLE
            binding?.cardViewViewCalllog?.setVisibility(View.VISIBLE)
            binding?.cardViewViewCalllog?.setOnClickListener(this)
        }
    }

    //oldcode

    //        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
    //    private void setVmnTotalCallCount() {
    //        showProgress();
    //        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
    //        String type = sessionManager.getISEnterprise().equals("true") ? "MULTI" : "SINGLE";
    //
    //        trackerApis.getVmnSummary(Constants.clientId, sessionManager.getFPID(), type, new Callback<JsonObject>() {
    //            @Override
    //            public void success(JsonObject jsonObject, Response response) {
    //                hideProgress();
    //
    //                if (jsonObject == null || response.getStatus() != 200) {
    //                    Methods.showSnackBarNegative(VmnCallCardsActivity.this, getString(R.string.something_went_wrong));
    //
    //                } else {
    //                    if (jsonObject.has("TotalCalls")) {
    //                        String vmnTotalCalls = jsonObject.get("TotalCalls").getAsString();
    //                        // oldcode
    ////                        totalCount.setText(vmnTotalCalls != null && !"null".equalsIgnoreCase(vmnTotalCalls) ? vmnTotalCalls : "0");
    //                        if(vmnTotalCalls != null && !"null".equalsIgnoreCase(vmnTotalCalls)){
    //                            totalCallCount = Integer.parseInt(vmnTotalCalls);
    //                            allCountText.setText(vmnTotalCalls);
    //                            potentialCallsText.setText("View potential calls ("+totalCallCount+")");
    //                        }else{
    //                            allCountText.setText("0");
    //                        }
    //                    }
    //                    if (jsonObject.has("MissedCalls")) {
    //                        String vmnMissedCalls = jsonObject.get("MissedCalls").getAsString();
    //                        missedCountText.setText(vmnMissedCalls != null && !"null".equalsIgnoreCase(vmnMissedCalls) ? vmnMissedCalls : "0");
    //                    }
    //                    if (jsonObject.has("ReceivedCalls")) {
    //                        String vmnReceivedCalls = jsonObject.get("ReceivedCalls").getAsString();
    //                        receivedCountText.setText(vmnReceivedCalls != null && !"null".equalsIgnoreCase(vmnReceivedCalls) ? vmnReceivedCalls : "0");
    //                    }
    //                    getWebsiteCallCount();
//                }
//                showEmptyScreen();
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                hideProgress();
//                showEmptyScreen();
//                Methods.showSnackBarNegative(VmnCallCardsActivity.this, getString(R.string.something_went_wrong));
//            }
//        });
//    }
    fun websiteCallCount() {
            showProgress()

            //oldcode
//        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
            viewModel.getCallCountByType(
                session.fpTag,
                "POTENTIAL_CALLS",
                "WEB",).observe(this){

                hideProgress()
                val jsonObject = it.anyResponse as JsonObject
                if (it.isSuccess().not()) {
                    showEmptyScreen()
                    showSnackBarNegative(
                        this,
                        getString(R.string.something_went_wrong)
                    )
                } else {
                    val callCount: String = jsonObject.get("POTENTIAL_CALLS").getAsString()
                    binding?.webCallCount!!.text = callCount
                    totalPotentialCallCount += callCount.toInt()
                    binding?.totalNumberOfCalls?.text =
                        "View potential calls ($totalPotentialCallCount)"
                    getPhoneCallCount()
                }
            }

        }

    private fun getPhoneCallCount() {
        showProgress()

        //old code
//        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
       viewModel.getCallCountByType(
            session.fpTag,
            "POTENTIAL_CALLS",
            "MOBILE").observe(this){
           hideProgress()
           if (it.isSuccess().not()) {
               showEmptyScreen()
               showSnackBarNegative(
                   this,
                   getString(R.string.something_went_wrong)
               )
           } else {
               val jsonObject = it.anyResponse as JsonObject

               val callCount: String = jsonObject.get("POTENTIAL_CALLS").getAsString()
               //                    webCallCount.setText(callCount);
               binding?.phoneCallCount?.text = callCount
               totalPotentialCallCount += callCount.toInt()
               binding?.totalNumberOfCalls!!.text =
                   "View potential calls ($totalPotentialCallCount)"
           }

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.cardViewViewCalllog -> {
              /*  val i = Intent(this@VmnCallCardsActivity, ShowVmnCallActivity::class.java)

                if (totalCallCount == 0) {
                    showSnackBarNegative(
                        this@VmnCallCardsActivity,
                        "You do not have call logs."
                    )
                    return
                }
                startActivity(i)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)*/
            }
            binding?.seeMoreLess->{
                if (!seeMoreLessStatus) {
                    seeMoreLessStatus = true
                    binding?.seeMoreLessImage?.setImageResource(R.drawable.vmn_up_arrow)
                    binding?.helpWebPhoneLayout?.setVisibility(View.VISIBLE)
                } else {
                    seeMoreLessStatus = false
                    binding?.seeMoreLessImage?.setImageResource(R.drawable.vmn_down_arrow)
                    binding?.helpWebPhoneLayout?.setVisibility(View.GONE)

                    //hide info
                    binding?.helpWebsiteInfo?.setVisibility(View.GONE)
                    binding?.helpPhoneInfo?.setVisibility(View.GONE)
                }
            }
            binding?.websiteHelper->{
                binding?.helpPhoneInfo?.setVisibility(View.GONE)
                if (binding?.helpWebsiteInfo?.getVisibility() == View.VISIBLE) {
                    binding?.helpWebsiteInfo?.setVisibility(View.GONE)
                } else {
                    binding?.helpWebsiteInfo?.setVisibility(View.VISIBLE)
                }
            }
            binding?.phoneHelper->{
                binding?.helpWebsiteInfo?.setVisibility(View.GONE)
                if (binding?.helpPhoneInfo?.getVisibility() == View.VISIBLE) {
                    binding?.helpPhoneInfo?.setVisibility(View.GONE)
                } else {
                    binding?.helpPhoneInfo?.setVisibility(View.VISIBLE)
                }
            }
            binding?.parentLayout->{
                if (seeMoreLessStatus) {
                    seeMoreLessStatus = false
                    binding?.seeMoreLessImage?.setImageResource(R.drawable.vmn_down_arrow)
                    binding?.helpWebPhoneLayout?.setVisibility(View.GONE)

                    //hide info
                    binding?.helpWebsiteInfo?.setVisibility(View.GONE)
                    binding?.helpPhoneInfo?.setVisibility(View.GONE)
                }
            }
        }
    }


    private fun initiateBuyFromMarketplace() {
        showProgress()
        val intent = Intent(this, Class.forName("com.boost.upgrades.UpgradeActivity"))
        intent.putExtra("expCode", session?.fP_AppExperienceCode)
        intent.putExtra("fpName", session?.fPName)
        intent.putExtra("fpid", session?.fPID)
        intent.putExtra("fpTag", session?.fpTag)
        intent.putExtra("accountType", session?.getFPDetails(GET_FP_DETAILS_CATEGORY))
        intent.putStringArrayListExtra(
            "userPurchsedWidgets",
            session.getStoreWidgets()?.toArrayList())
        if (session.userPrimaryMobile != null) {
            intent.putExtra("email", session.userPrimaryMobile)
        } else {
            intent.putExtra("email", getString(R.string.ria_customer_mail))
        }
        if (session.userPrimaryMobile != null) {
            intent.putExtra("mobileNo", session?.userPrimaryMobile)
        } else {
            intent.putExtra("mobileNo", getString(R.string.ria_customer_number))
        }
        intent.putExtra("profileUrl", session?.fPLogo)
        intent.putExtra("buyItemKey", "CALLTRACKER")
        startActivity(intent)
        Handler().postDelayed({hideProgress() }, 1000)
    }

    private fun nonEmptyView() {
        binding?.primaryLayout?.setVisibility(View.VISIBLE)
        binding?.childContainer?.setVisibility(View.GONE)
    }

    private fun emptyView() {
        binding?.primaryLayout?.setVisibility(View.GONE)
        binding?.childContainer?.setVisibility(View.VISIBLE)
    }

    override fun primaryButtonClicked() {
        initiateBuyFromMarketplace()
    }

    override fun secondaryButtonClicked() {
        try {
            startActivity(
                Intent(
                    this,
                    Class.forName("com.onboarding.nowfloats.ui.supportVideo.SupportVideoPlayerActivity")
                )
                    .putExtra(IntentConstant.SUPPORT_VIDEO_TYPE.name, SupportVideoType.TOB.value)
            )
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun ternaryButtonClicked() {}
    override fun appOnBackPressed() {}
    override fun onStop() {
        super.onStop()
        if (vmnCallAdapter?.getItemCount()?:0 > 1) {
            InAppReviewUtils.showInAppReview(
                this,
                InAppReviewUtils.Events.in_app_review_out_of_customer_calls
            )
        }
    }

    companion object {
        private const val TAG = "VmnCallCardsActivity"
    }

    override fun getLayout(): Int {
        return R.layout.activity_vmn_call_cards_v2
    }

    override fun getViewModelClass(): Class<VmnCallsViewModel> {
        return VmnCallsViewModel::class.java
    }
}