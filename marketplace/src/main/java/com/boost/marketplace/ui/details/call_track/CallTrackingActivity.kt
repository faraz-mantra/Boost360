package com.boost.marketplace.ui.details.call_track

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.adapter.ExactMatchListAdapter
import com.boost.marketplace.adapter.MatchNumberListAdapter
import com.boost.marketplace.adapter.NumberListAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityCallTrackingBinding
import com.boost.marketplace.interfaces.CallTrackListener
import com.boost.marketplace.ui.details.FeatureDetailsViewModel
import com.boost.marketplace.ui.popup.call_track.CallTrackAddToCartBottomSheet
import com.boost.marketplace.ui.popup.call_track.CallTrackingHelpBottomSheet
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.framework.utils.hideKeyBoard
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class CallTrackingActivity :
    AppBaseActivity<ActivityCallTrackingBinding, FeatureDetailsViewModel>(),
    CallTrackListener {
    var numberList: ArrayList<String> = ArrayList()
    var loadMorenumberList: ArrayList<String> = ArrayList()
    var isLoading = false
    var handler: Handler = Handler()

    lateinit var numberListAdapter: NumberListAdapter
    lateinit var matchNumberListAdapter: MatchNumberListAdapter
    lateinit var exactMatchNumberListAdapter: ExactMatchListAdapter
    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
    var experienceCode: String? = null
    var blockedNumber: String? = null
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
    lateinit var singleAddon: FeaturesModel
    lateinit var progressDialog: ProgressDialog
    lateinit var prefs: SharedPrefs
    var numberprice: String? = null
    var pricing: String? = null

    override fun getLayout(): Int {
        return R.layout.activity_call_tracking
    }

    companion object {
        fun newInstance() = CallTrackingActivity()
    }


    override fun getViewModelClass(): Class<FeatureDetailsViewModel> {
        return FeatureDetailsViewModel::class.java
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
        userPurchsedWidgets =
            intent.getStringArrayListExtra("userPurchsedWidgets") ?: java.util.ArrayList()
        val jsonString = intent.extras?.getString("bundleData")
        singleAddon =
            Gson().fromJson<FeaturesModel>(jsonString, object : TypeToken<FeaturesModel>() {}.type)
        viewModel.setApplicationLifecycle(application, this)

        progressDialog = ProgressDialog(this)
        prefs = SharedPrefs(this)
        viewModel.setApplicationLifecycle(application, this)


        matchNumberListAdapter = MatchNumberListAdapter(this, ArrayList(), null, this)
        numberListAdapter = NumberListAdapter(this, ArrayList(), null, this)
        exactMatchNumberListAdapter = ExactMatchListAdapter(this, ArrayList(), null, this)
        progressDialog = ProgressDialog(this)

        numberprice = intent.getStringExtra("price")


        binding?.addonsBack?.setOnClickListener {
            onBackPressed()
        }
        binding?.help?.setOnClickListener {
            val dialogCard = CallTrackingHelpBottomSheet()
            dialogCard.show(
                this.supportFragmentManager,
                CallTrackingHelpBottomSheet::class.java.name
            )
        }
        binding?.btnSelectNumber?.setOnClickListener {

            val dialogCard = CallTrackAddToCartBottomSheet()
            val bundle = Bundle()

            bundle.putString("price",numberprice)

            bundle.putString("number", blockedNumber)
            bundle.putString("bundleData", Gson().toJson(singleAddon))
            bundle.putDouble(
                "AddonDiscountedPrice",
                getDiscountedPrice(singleAddon!!.price, singleAddon!!.discount_percent)
            )
            bundle.putString("fpid", fpid)
            bundle.putString("expCode", experienceCode)
            bundle.putBoolean("isDeepLink", isDeepLink)
            bundle.putString("deepLinkViewType", deepLinkViewType)
            bundle.putInt("deepLinkDay", deepLinkDay)
            bundle.putBoolean("isOpenCardFragment", isOpenCardFragment)
            bundle.putString(
                "accountType",
                accountType
            )
            bundle.putStringArrayList(
                "userPurchsedWidgets",
                userPurchsedWidgets
            )
            if (email != null) {
                bundle.putString("email", email)
            } else {
                bundle.putString("email", "ria@nowfloats.com")
            }
            if (mobileNo != null) {
                bundle.putString("mobileNo", mobileNo)
            } else {
                bundle.putString("mobileNo", "9160004303")
            }
            bundle.putString("profileUrl", profileUrl)
            dialogCard.arguments = bundle
            dialogCard.show(
                this.supportFragmentManager,
                CallTrackAddToCartBottomSheet::class.java.name
            )
        }
        binding?.etCallTrack?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.length!! > 0) {
                    binding?.ivCross?.visibility = GONE
                    binding?.btnSearch?.visibility = VISIBLE
                    binding?.btnSearch?.setOnClickListener {
                        hideKeyBoard()

                        updateAllItemBySearchValue(p0.toString())
                        binding?.tvAvailableNo?.text = "Search results"
                        binding?.btnSearch?.visibility = GONE
                    }

                    binding?.ivCross?.visibility = VISIBLE
                } else {
                    binding?.btnSearch?.visibility = GONE
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        binding?.ivCross?.setOnClickListener {
            binding?.etCallTrack?.setText("")
            binding?.etCallTrack?.hint = "Search for a sequence of digits ..."
            binding?.tvAvailableNo?.text = "Available numbers"
            binding?.cardListExactMatch?.visibility = GONE
            binding?.cardList?.visibility = VISIBLE
            val adapter2 = ExactMatchListAdapter(this, ArrayList(), null, this)
            binding?.rvNumberListExactMatch?.adapter = adapter2
            binding?.rvNumberListExactMatch?.setHasFixedSize(true)
            binding?.rvNumberListExactMatch?.isNestedScrollingEnabled = false
            val adapter1 = MatchNumberListAdapter(this, ArrayList(), null, this)
            binding?.rvNumberListRelated?.adapter = adapter1
            binding?.rvNumberListRelated?.isNestedScrollingEnabled = false
            binding?.rvNumberListRelated?.setHasFixedSize(true)
            binding?.tvSearchResult?.visibility = VISIBLE
            binding?.tvSearchResultForRelatedCombination?.visibility = GONE
            binding?.tvOtherAvailableNo?.visibility = GONE
            binding?.tvSearchResult?.visibility = GONE
            binding?.cardListRelated?.visibility = GONE
            binding?.ivCross?.visibility = GONE
        }
        loadNumberList()
        initMVVM()
    }

    private fun initNumberListAdapter(list: ArrayList<String>) {
        numberListAdapter = NumberListAdapter(this, loadMorenumberList, null, this)
        binding?.rvNumberList?.setHasFixedSize(true)
        binding?.rvNumberList?.isNestedScrollingEnabled = false
        binding?.rvNumberList?.adapter = numberListAdapter
    }

    private fun initScrollListener() {
        binding?.rvNumberList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val pos = linearLayoutManager?.findLastCompletelyVisibleItemPosition()
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == loadMorenumberList.size - 1) {
                    //bottom of list!
                    onScrolledToBottom()
                }


            }
        })
    }

    private fun onScrolledToBottom() {
        if (loadMorenumberList.size < numberList.size) {
            val x: Int
            val y: Int
            if (numberList.size - loadMorenumberList.size >= 20) {
                x = loadMorenumberList.size
                y = x + 20
            } else {
                x = loadMorenumberList.size
                y = x + numberList.size - loadMorenumberList.size
            }
            for (i in x until y) {
                loadMorenumberList.add(numberList[i])
            }
            numberListAdapter.notifyDataSetChanged()
        }
    }

    fun initMVVM() {
        viewModel.getCallTrackingDetails().observe(this) {
            if (it != null) {
                numberList.addAll(it)
                for (i in 0..19) {
                    loadMorenumberList.add(numberList[i])
                }
                initNumberListAdapter(loadMorenumberList)
                initScrollListener()

            }
        }
        viewModel.numberLoader().observe(this, Observer
        {
            if (it) {

                binding?.scrollView?.visibility = GONE
                binding?.shimmerViewVmn?.visibility = VISIBLE

            } else {
                binding?.scrollView?.visibility = VISIBLE
                binding?.shimmerViewVmn?.visibility = GONE
            }
        })

    }


    private fun loadNumberList() {
        try {
            viewModel.loadNumberList(
                intent.getStringExtra("fpid") ?: "",
                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"

            )
        } catch (e: Exception) {
            SentryController.captureException(e)
        }

    }

    private fun updateExactNumberList(list: ArrayList<String>, searchValue: String?) {
        exactMatchNumberListAdapter = ExactMatchListAdapter(this, list, searchValue, this)
        binding?.rvNumberListExactMatch?.adapter = exactMatchNumberListAdapter
        binding?.tvSearchResult?.visibility = VISIBLE
        exactMatchNumberListAdapter.notifyDataSetChanged()

    }

    private fun updateEveryNumberList(list: MutableList<String>, searchValue: String?) {
        matchNumberListAdapter = MatchNumberListAdapter(this, list, searchValue, this)
        binding?.cardListRelated?.visibility = VISIBLE
        val recyclerview = findViewById<RecyclerView>(R.id.rv_number_list_related)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = matchNumberListAdapter
        matchNumberListAdapter.notifyDataSetChanged()
    }

    fun updateAllItemBySearchValue(searchValue: String) {

        var exactMatchList: ArrayList<String> = arrayListOf()
        var everyMatchList: ArrayList<String> = arrayListOf()

        for (number in numberList) {
            val num = number.replace("+91 ", "").replace("-", "")
            var isMatching: Int = 0
            for (i in searchValue.indices) {
                if (num.contains(searchValue[i])) {
                    isMatching = isMatching + 1
                    if (num.contains(searchValue)) {
                        isMatching = 0
                        break
                    }
                }
            }
            if (isMatching > 2) {
                everyMatchList.add(number)
            }
        }
        for (number in numberList) {
            val num = number.replace("+91 ", "").replace("-", "")
            if (num.contains(searchValue)) {
                exactMatchList.add(number)

            }
        }
        if (exactMatchList.isNotEmpty() && everyMatchList.isNotEmpty()) {
            updateExactNumberList(exactMatchList, searchValue)
            updateEveryNumberList(everyMatchList, searchValue)
            binding?.cardListExactMatch?.visibility = VISIBLE
            binding?.cardList?.visibility = GONE
            binding?.rvNumberListRelated?.visibility = VISIBLE
            binding?.tvSearchResult?.visibility = VISIBLE
            binding?.tvSearchResult?.text =
                exactMatchList.size.toString() + " numbers found with " + "‘" + searchValue + "’"
            binding?.tvSearchResultForRelatedCombination?.visibility = VISIBLE
            binding?.tvSearchResultForRelatedCombination?.text =
                everyMatchList.size.toString() + " numbers found with related combinations"
        } else if (exactMatchList.isEmpty() && everyMatchList.isNotEmpty()) {
            updateEveryNumberList(everyMatchList, searchValue)
            binding?.tvAvailableNo?.text = "Oops! No exact matches found."
            binding?.cardListRelated?.visibility = VISIBLE
            binding?.tvSearchResult?.visibility = GONE
            binding?.cardListExactMatch?.visibility = GONE
            binding?.cardList?.visibility = GONE
            binding?.tvSearchResultForRelatedCombination?.visibility = GONE
            binding?.tvOtherAvailableNo?.visibility = VISIBLE
            binding?.tvOtherAvailableNo?.text =
                everyMatchList.size.toString() + " numbers found with related combinations"
        } else if (exactMatchList.isNotEmpty() && everyMatchList.isEmpty()) {
            updateExactNumberList(exactMatchList,searchValue)
            binding?.cardListRelated?.visibility = GONE
            binding?.cardListExactMatch?.visibility = VISIBLE
            binding?.cardList?.visibility = GONE
            binding?.tvSearchResultForRelatedCombination?.visibility = GONE
            binding?.tvOtherAvailableNo?.visibility = GONE
        } else {
            binding?.tvAvailableNo?.text = "Oops! No search results found."
            binding?.tvOtherAvailableNo?.text = "Other available numbers"
            val adapter2 = ExactMatchListAdapter(this, ArrayList(), null, this)
            binding?.rvNumberListExactMatch?.adapter = adapter2
            binding?.rvNumberListExactMatch?.setHasFixedSize(true)
            binding?.rvNumberListExactMatch?.isNestedScrollingEnabled = false
            binding?.cardList?.visibility = VISIBLE
        }


    }

    private fun getDiscountedPrice(price: Double, discountPercent: Int): Double {
        return price - ((discountPercent / 100) * price)
    }

    fun getAccessToken(): String {
        return UserSessionManager(this).getAccessTokenAuth()?.barrierToken() ?: ""
    }

    override fun onClicked(number: String) {
        blockedNumber = number
        binding?.btnSelectNumber?.setBackgroundResource(R.color.colorAccent1);

    }

}