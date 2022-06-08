package com.boost.marketplace.ui.details.call_track

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R
import com.boost.marketplace.adapter.MatchNumberListAdapter
import com.boost.marketplace.adapter.NumberListAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityCallTrackingBinding
import com.boost.marketplace.interfaces.CallTrackListener
import com.boost.marketplace.ui.details.FeatureDetailsViewModel
import com.boost.marketplace.ui.details.domain.*
import kotlinx.android.synthetic.main.activity_call_tracking.*


class CallTrackingActivity :
    AppBaseActivity<ActivityCallTrackingBinding, FeatureDetailsViewModel>(),
    CallTrackListener {
    lateinit var numberList: ArrayList<String>
    lateinit var numberListAdapter: NumberListAdapter
    val list: ArrayList<String> = ArrayList()


    override fun getLayout(): Int {
        return R.layout.activity_call_tracking
    }

    companion object {
        fun newInstance() = CallTrackingActivity()
    }


    override fun getViewModelClass(): Class<FeatureDetailsViewModel> {
        return FeatureDetailsViewModel::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FeatureDetailsViewModel::class.java]
        viewModel.setApplicationLifecycle(application, this)
        numberList = intent.getStringArrayListExtra("list")!!
        list.add("+91 8071-280-316")
        list.add("+91 8071-280-001")
        list.add("+91 8071-200-002")
        list.add("+91 8071-000-256")
        list.add("+91 8071-380-000")
        list.add("+91 8071-238-981")
        list.add("+91 8071-568-001")
        list.add("+91 8071-674-000")

        initRV()
    }

    override fun onCreateView() {
        super.onCreateView()

        numberListAdapter = NumberListAdapter(this, ArrayList(), null, this)
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
                if (p0 != null && p0?.length!! > 2) {
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
            initRV()
//            updateEveryNumberList(ArrayList(),null)
            binding?.tvSearchResultForRelatedCombination?.visibility = GONE
            binding?.tvSearchResult?.visibility = GONE
            binding?.rvNumberListRelated?.visibility = GONE
        }


    }

    private fun initRV() {
        val recyclerview = findViewById<RecyclerView>(R.id.rv_number_list)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = NumberListAdapter(this, list, null, this)
        recyclerview.adapter = adapter
    }

    private fun updateNumberList(list: ArrayList<String>, searchValue: String?) {
        numberListAdapter.addupdates(list)
        numberListAdapter = NumberListAdapter(this, list, searchValue, this)
        binding?.rvNumberList?.adapter = numberListAdapter
        binding?.tvSearchResult?.visibility = VISIBLE
        binding?.tvSearchResult?.text =
            list.size.toString() + " numbers found with " + "‘" + searchValue + "’"
        numberListAdapter.notifyDataSetChanged()
    }

    private fun updateEveryNumberList(list: MutableList<String>, searchValue: String?) {
        binding?.cardListRelated?.visibility = VISIBLE
        val recyclerview = findViewById<RecyclerView>(R.id.rv_number_list_related)
        recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = MatchNumberListAdapter(this, list, searchValue, this)
        recyclerview.adapter = adapter
        binding?.tvSearchResultForRelatedCombination?.visibility = VISIBLE
        binding?.tvSearchResultForRelatedCombination?.text =
            list.size.toString() + " numbers found with related combinations"
        adapter.notifyDataSetChanged()
    }

    fun updateAllItemBySearchValue(searchValue: String) {
        var exactMatchList: ArrayList<String> = arrayListOf()
        var everyMatchList: ArrayList<String> = arrayListOf()
        var isMatching: Boolean = false
        var searchChar: Char? = null
        for (number in list) {
            for (i in searchValue.indices) {
                if (number.contains(searchValue[i])) {
                    isMatching = true
                    if (number.contains(searchValue)) {
                        isMatching = false
                    }
                    if (isMatching) {
                        everyMatchList.add(number)
                    }
                    break
                }
            }
            if (number.contains(searchValue)) {
                exactMatchList.add(number)
            }
        }
        updateNumberList(exactMatchList, searchValue)
        if (isMatching) {
            binding?.rvNumberListRelated?.visibility = VISIBLE
            updateEveryNumberList(everyMatchList, searchValue)
        }
    }

    override fun onClicked(position: Int, view: View) {
        TODO("Not yet implemented")
    }
}