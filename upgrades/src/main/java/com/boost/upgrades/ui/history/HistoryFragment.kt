package com.boost.upgrades.ui.history

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.HistoryAdapter
import com.boost.upgrades.data.api_model.GetPurchaseOrder.GetPurchaseOrderResponse
import com.boost.upgrades.data.api_model.GetPurchaseOrder.Result
import com.boost.upgrades.interfaces.HistoryFragmentListener
import com.boost.upgrades.ui.historydetails.HistoryDetailsFragment
import com.boost.upgrades.utils.Constants.Companion.HISTORY_DETAILS_FRAGMENT
import com.google.gson.Gson
import kotlinx.android.synthetic.main.history_fragment.*
import kotlinx.android.synthetic.main.my_addons_fragment.*

class HistoryFragment : BaseFragment(), HistoryFragmentListener {

  lateinit var root: View

  lateinit var historyAdapter: HistoryAdapter

  companion object {
    fun newInstance() = HistoryFragment()
  }

  private lateinit var viewModel: HistoryViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.history_fragment, container, false)

    historyAdapter = HistoryAdapter(ArrayList(), this)

    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(requireActivity()).get(HistoryViewModel::class.java)

    loadData()
    initMVVM()
    initRecyclerView()
    shimmer_view_history.startShimmer()
    history_back.setOnClickListener {
      (activity as UpgradeActivity).popFragmentFromBackStack()
    }
  }

  private fun loadData() {
    viewModel.loadPurchasedItems(
      (activity as UpgradeActivity).fpid!!,
      (activity as UpgradeActivity).clientid
    )
  }

  @SuppressLint("FragmentLiveDataObserve")
  private fun initMVVM() {
    viewModel.updateResult().observe(this, Observer {
      updateRecycler(it)
    })
    viewModel.updatesLoader().observe(this, Observer {
      if (!it) {
        if (shimmer_view_history.isShimmerStarted) {
          shimmer_view_history.stopShimmer()
          shimmer_view_history.visibility = View.GONE
        }
      }
    })
  }

  fun updateRecycler(result: GetPurchaseOrderResponse) {
    if (result.StatusCode == 200 && result.Result != null) {
      if (shimmer_view_history.isShimmerStarted) {
        shimmer_view_history.stopShimmer()
        shimmer_view_history.visibility = View.GONE
      }
      historyAdapter.addupdates(result.Result)
      historyAdapter.notifyDataSetChanged()
      order_history_recycler.setFocusable(false)
      order_history_recycler.visibility = View.VISIBLE
      empty_history.visibility = View.GONE
    } else {
      if (shimmer_view_history.isShimmerStarted) {
        shimmer_view_history.stopShimmer()
        shimmer_view_history.visibility = View.GONE
      }
      order_history_recycler.visibility = View.GONE
      empty_history.visibility = View.VISIBLE
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

  override fun viewHistoryItem(item: Result) {
    val historyDetailsFragment = HistoryDetailsFragment.newInstance()
    val args = Bundle()
    args.putSerializable("data", Gson().toJson(item))
    historyDetailsFragment.arguments = args
    (activity as UpgradeActivity).addFragment(historyDetailsFragment, HISTORY_DETAILS_FRAGMENT)
  }

}
