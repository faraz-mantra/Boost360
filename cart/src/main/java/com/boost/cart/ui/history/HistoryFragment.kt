package com.boost.cart.ui.history

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
import com.boost.cart.R
import com.boost.cart.base_class.BaseFragment

import com.boost.cart.CartActivity
import com.boost.cart.adapter.HistoryAdapter
import com.boost.cart.data.api_model.GetPurchaseOrder.GetPurchaseOrderResponse
import com.boost.cart.data.api_model.GetPurchaseOrder.Result
import com.boost.cart.interfaces.HistoryFragmentListener
import com.boost.cart.ui.historydetails.HistoryDetailsFragment
import com.boost.cart.utils.Constants.Companion.HISTORY_DETAILS_FRAGMENT
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
      (activity as CartActivity).popFragmentFromBackStack()
    }
  }

  private fun loadData() {
    viewModel.loadPurchasedItems(
      (activity as? CartActivity)?.getAccessToken()?:"",
      (activity as CartActivity).fpid!!,
      (activity as CartActivity).clientid
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
    (activity as CartActivity).addFragment(historyDetailsFragment, HISTORY_DETAILS_FRAGMENT)
  }

}
