package com.boost.upgrades.ui.historydetails

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.HistoryDetailsAdapter
import kotlinx.android.synthetic.main.history_details_fragment.*

class HistoryDetailsFragment : BaseFragment() {

    lateinit var root: View

    lateinit var historyDetailsAdapter: HistoryDetailsAdapter

    companion object {
        fun newInstance() = HistoryDetailsFragment()
    }

    private lateinit var viewModel: HistoryDetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.history_details_fragment, container, false)

        historyDetailsAdapter = HistoryDetailsAdapter(ArrayList())
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(HistoryDetailsViewModel::class.java)

        initRecyclerView()

        history_details_back.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

    }

    private fun initRecyclerView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        history_single_item_recycler.apply {
            layoutManager = gridLayoutManager
            history_single_item_recycler.adapter = historyDetailsAdapter

        }
    }

}
