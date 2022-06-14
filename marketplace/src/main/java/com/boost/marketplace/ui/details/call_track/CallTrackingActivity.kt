package com.boost.marketplace.ui.details.call_track

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R
import com.boost.marketplace.adapter.MatchNumberListAdapter
import com.boost.marketplace.adapter.NumberListAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityCallTrackingBinding
import com.boost.marketplace.interfaces.CallTrackListener
import com.boost.marketplace.ui.details.FeatureDetailsViewModel
import com.boost.marketplace.ui.popup.call_track.CallTrackingHelpBottomSheet
import com.boost.marketplace.ui.popup.call_track.SelectedNumberBottomSheet
import kotlinx.android.synthetic.main.activity_call_tracking.*


class CallTrackingActivity :
    AppBaseActivity<ActivityCallTrackingBinding, FeatureDetailsViewModel>(),
    CallTrackListener {
    lateinit var numberList: ArrayList<String>
    lateinit var numberListAdapter: NumberListAdapter
    lateinit var matchNumberListAdapter: MatchNumberListAdapter


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
        numberList = intent.getStringArrayListExtra("list")!!
        matchNumberListAdapter = MatchNumberListAdapter(this, ArrayList(), null, this)
        initRV()
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
            val dialogCard = SelectedNumberBottomSheet()
            dialogCard.show(this.supportFragmentManager, SelectedNumberBottomSheet::class.java.name)
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
            binding?.tvAvailableNo?.text ="Available numbers"
            initRV()
            binding?.tvSearchResultForRelatedCombination?.visibility = GONE
            binding?.tvOtherAvailableNo?.visibility = GONE
            binding?.tvSearchResult?.visibility = GONE
            binding?.cardListRelated?.visibility = GONE
        }


    }

    private fun initRV() {
        val recyclerview = findViewById<RecyclerView>(R.id.rv_number_list)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = NumberListAdapter(this, numberList, null, this)
        recyclerview.adapter = adapter
    }

    private fun updateNumberList(list: ArrayList<String>, searchValue: String?) {
        numberListAdapter = NumberListAdapter(this, list, searchValue, this)
        binding?.rvNumberList?.adapter = numberListAdapter
        binding?.tvSearchResult?.visibility = VISIBLE
        numberListAdapter.notifyDataSetChanged()
    }

    private fun updateEveryNumberList(list: MutableList<String>, searchValue: String?) {
        matchNumberListAdapter = MatchNumberListAdapter(this,list,searchValue,this)
        binding?.cardListRelated?.visibility = VISIBLE
        val recyclerview = findViewById<RecyclerView>(R.id.rv_number_list_related)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = matchNumberListAdapter
        matchNumberListAdapter.notifyDataSetChanged()
    }

    fun updateAllItemBySearchValue(searchValue: String) {
        var exactMatchList: ArrayList<String> = arrayListOf()
        var everyMatchList: ArrayList<String> = arrayListOf()
        var isMatching: Boolean = false

        for (number in  numberList) {
            val num = number.replace("+91 ", "").replace("-", "")
            for (i in searchValue.indices) {
                if (num.contains(searchValue[i])) {
                    isMatching = true
                    if (num.contains(searchValue)) {
                        isMatching = false
                    }
                    if (isMatching) {
                        everyMatchList.add(number)
                    }
                    break
                }
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
        }else if (exactMatchList.isEmpty() && everyMatchList.isNotEmpty()) {
            tv_available_no.text = "Oops! No exact matches found."
            binding?.cardListRelated?.visibility = VISIBLE
           binding?.tvSearchResult?.visibility = GONE
            binding?.tvSearchResultForRelatedCombination?.visibility = GONE
            binding?.tvOtherAvailableNo?.visibility = VISIBLE
            binding?.tvOtherAvailableNo?.text =
                everyMatchList.size.toString() + " numbers found with related combinations"
            updateEveryNumberList(everyMatchList, searchValue)
        }else if(exactMatchList.isNotEmpty() && everyMatchList.isEmpty()){
           binding?.cardListRelated?.visibility = GONE
           binding?.tvSearchResultForRelatedCombination?.visibility = GONE
           binding?.tvOtherAvailableNo?.visibility = GONE
       }else{
            tv_available_no.text = "Oops! No search results found."
            tv_other_available_no.text = "Other available numbers"
           updateNumberList(numberList,null)
        }


    }

    override fun onClicked(position: Int, view: View) {
        TODO("Not yet implemented")
    }
}