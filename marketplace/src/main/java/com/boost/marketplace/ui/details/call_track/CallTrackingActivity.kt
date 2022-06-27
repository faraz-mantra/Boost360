package com.boost.marketplace.ui.details.call_track

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
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
import kotlinx.android.synthetic.main.activity_call_tracking.*


class CallTrackingActivity :
    AppBaseActivity<ActivityCallTrackingBinding, FeatureDetailsViewModel>(),
    CallTrackListener {
     var numberList: ArrayList<String> = ArrayList()
    lateinit var numberListAdapter: NumberListAdapter
    lateinit var matchNumberListAdapter: MatchNumberListAdapter
//    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
//    var experienceCode: String? = null
    var blockedNumber: String? = null
//    var fpid: String? = null
//    var email: String? = null
//    var mobileNo: String? = null
//    var profileUrl: String? = null
//    var accountType: String? = null
//    var isDeepLink: Boolean = false
//    var isOpenCardFragment: Boolean = false
//    var deepLinkViewType: String = ""
//    var deepLinkDay: Int = 7
//    var userPurchsedWidgets = java.util.ArrayList<String>()
//    lateinit var singleAddon: FeaturesModel
    lateinit var progressDialog: ProgressDialog
    lateinit var prefs: SharedPrefs


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

        viewModel.setApplicationLifecycle(application, this)
        matchNumberListAdapter = MatchNumberListAdapter(this, ArrayList(), null, this)
        numberListAdapter = NumberListAdapter(this, ArrayList(),null,this)
        progressDialog = ProgressDialog(this)



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
            bundle.putString("number",blockedNumber)

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
                if (p0 != null && p0?.length!! > 0) {
                    binding?.ivCross?.visibility = View.GONE
                    binding?.btnSearch?.visibility = View.VISIBLE
                    binding?.btnSearch?.setOnClickListener {
                        updateAllItemBySearchValue(p0.toString())
                        tv_available_no.text = "Search results"
                        binding?.btnSearch?.visibility = View.GONE

                    }
                    binding?.ivCross?.visibility = View.VISIBLE

                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        binding?.ivCross?.setOnClickListener {
            binding?.etCallTrack?.setText("")
            binding?.etCallTrack?.hint = "Search for a sequence of digits ..."
            binding?.tvAvailableNo?.text = "Available numbers"
            initRV()
            binding?.tvSearchResultForRelatedCombination?.visibility = GONE
            binding?.tvOtherAvailableNo?.visibility = GONE
            binding?.tvSearchResult?.visibility = GONE
            binding?.cardListRelated?.visibility = GONE
        }
        loadNumberList()
        initMVVM()
        initRV()

    }

    fun initMVVM(){
        viewModel.getCallTrackingDetails().observe(this) {
            if (it != null) {
                System.out.println("numberList" + it)

                numberList.addAll(it)
                numberListAdapter.addupdates(it)
                numberListAdapter.notifyDataSetChanged()


            }
        }

        viewModel.addonsLoader().observe(this, Observer
        {
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

    private fun initRV() {
        val recyclerview = findViewById<RecyclerView>(R.id.rv_number_list)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = numberListAdapter
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

    private fun updateNumberList(list: ArrayList<String>, searchValue: String?) {
        numberListAdapter = NumberListAdapter(this, list, searchValue, this)
        binding?.rvNumberList?.adapter = numberListAdapter
        binding?.tvSearchResult?.visibility = VISIBLE
        numberListAdapter.notifyDataSetChanged()
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
            if (num.contains(searchValue)) {
                exactMatchList.add(number)
            }
        }
        if (exactMatchList.isNotEmpty() && everyMatchList.isNotEmpty()) {
            updateNumberList(exactMatchList, searchValue)
            updateEveryNumberList(everyMatchList, searchValue)
            binding?.rvNumberListRelated?.visibility = VISIBLE
            binding?.tvSearchResult?.text =
                exactMatchList.size.toString() + " numbers found with " + "‘" + searchValue + "’"
            binding?.tvSearchResultForRelatedCombination?.visibility = VISIBLE
            binding?.tvSearchResultForRelatedCombination?.text =
                everyMatchList.size.toString() + " numbers found with related combinations"
        } else if (exactMatchList.isEmpty() && everyMatchList.isNotEmpty()) {
            tv_available_no.text = "Oops! No exact matches found."
            binding?.cardListRelated?.visibility = VISIBLE
            binding?.tvSearchResult?.visibility = GONE
            binding?.tvSearchResultForRelatedCombination?.visibility = GONE
            binding?.tvOtherAvailableNo?.visibility = VISIBLE
            binding?.tvOtherAvailableNo?.text =
                everyMatchList.size.toString() + " numbers found with related combinations"
            updateEveryNumberList(everyMatchList, searchValue)
        } else if (exactMatchList.isNotEmpty() && everyMatchList.isEmpty()) {
            binding?.cardListRelated?.visibility = GONE
            binding?.tvSearchResultForRelatedCombination?.visibility = GONE
            binding?.tvOtherAvailableNo?.visibility = GONE
        } else {
            tv_available_no.text = "Oops! No search results found."
            tv_other_available_no.text = "Other available numbers"
            updateNumberList(numberList, null)
        }


    }

    fun getAccessToken(): String {
        return UserSessionManager(this).getAccessTokenAuth()?.barrierToken() ?: ""
    }

    override fun onClicked(number: String) {
        blockedNumber = number

        blockedNumber?.let {
            viewModel.blockNumberStatus(
                (this).getAccessToken() ?: "", intent.getStringExtra("fpid") ?: "",
                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21", it
            )
        }
        viewModel.updateStatus().observe(this, androidx.lifecycle.Observer {
            if (it.Result) {
                binding?.btnSelectNumber?.setBackgroundResource(R.color.btn_bg_color_disabled)
                binding?.btnSelectNumber?.isEnabled = false

            } else {
                binding?.btnSelectNumber?.setBackgroundResource(R.color.colorAccent1)
                binding?.btnSelectNumber?.isEnabled = true


            }
        })
    }

}