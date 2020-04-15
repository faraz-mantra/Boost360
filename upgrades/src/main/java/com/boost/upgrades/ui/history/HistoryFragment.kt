package com.boost.upgrades.ui.history

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
import com.boost.upgrades.adapter.HistoryAdapter
import com.boost.upgrades.interfaces.HistoryFragmentListener
import com.boost.upgrades.ui.historydetails.HistoryDetailsFragment
import com.boost.upgrades.utils.Constants.Companion.HISTORY_DETAILS_FRAGMENT
import kotlinx.android.synthetic.main.history_fragment.*

class HistoryFragment : BaseFragment(), HistoryFragmentListener {

    lateinit var root: View

    lateinit var historyAdapter: HistoryAdapter

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var viewModel: HistoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.history_fragment, container, false)

        historyAdapter = HistoryAdapter(ArrayList(), this)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(HistoryViewModel::class.java)

        initRecyclerView()

        history_back.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }
    }

    fun initRecyclerView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 1)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        order_history_recycler.apply {
            layoutManager = gridLayoutManager
            order_history_recycler.adapter = historyAdapter

        }
    }

    override fun viewHistoryItem(pos: Int) {
        (activity as UpgradeActivity).addFragment(HistoryDetailsFragment.newInstance(), HISTORY_DETAILS_FRAGMENT)
    }

}
