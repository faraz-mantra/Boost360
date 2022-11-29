package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentBrowseTabBinding
import com.festive.poster.models.*
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.festive.poster.viewmodels.PromoUpdatesViewModel
import com.framework.base.BaseActivity
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.rest.NetworkResult
import com.framework.utils.showToast
import com.framework.utils.toArrayList

class BrowseTabFragment : AppBaseFragment<FragmentBrowseTabBinding, FestivePosterViewModel>(), RecyclerItemClickListener {

  private var adapter: AppBaseRecyclerViewAdapter<BrowseTabCategory>? = null
  private var promoUpdatesViewModel: PromoUpdatesViewModel? = null

  private var session: UserSessionManager? = null

  override fun getLayout(): Int {
    return R.layout.fragment_browse_tab
  }

  override fun getViewModelClass(): Class<FestivePosterViewModel> {
    return FestivePosterViewModel::class.java
  }

  companion object {
    fun newInstance(bundle: Bundle = Bundle()): BrowseTabFragment {
      val fragment = BrowseTabFragment()
      fragment.arguments = bundle
      return fragment
    }


  }


  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(requireActivity())
    promoUpdatesViewModel = ViewModelProvider(requireActivity()).get(PromoUpdatesViewModel::class.java)
    fetchDataFromServer()


  }

  private fun fetchDataFromServer() {
    startShimmer()
    promoUpdatesViewModel?.browseAllLData?.observe(viewLifecycleOwner) {
      stopShimmer()

      when (it) {
        is NetworkResult.Loading -> {
          if (adapter?.isEmpty() == true) startShimmer()
        }
        is NetworkResult.Success -> {
          stopShimmer()
          val data = it.data ?: return@observe
          data.let { list ->
            val uiList = list.asBrowseTabModels().toArrayList()
            adapter = AppBaseRecyclerViewAdapter(
              requireActivity() as BaseActivity<*, *>, uiList, this
            )
            binding.rvCat.adapter = adapter
            binding.rvCat.layoutManager = GridLayoutManager(requireActivity(), 2)
          }
        }
        is NetworkResult.Error -> {
          stopShimmer()
          showToast(it.msg)
        }
      }


    }
  }


  private fun startShimmer() {
    binding.shimmerLayout.visible()
    binding.rvCat.gone()
    binding.shimmerLayout.startShimmer()
  }

  private fun stopShimmer() {
    binding.shimmerLayout.gone()
    binding.rvCat.visible()
    binding.shimmerLayout.stopShimmer()
  }


  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.BROWSE_TAB_POSTER_CAT_CLICKED.ordinal -> {
        addFragment(R.id.container, BrowseAllFragment.newInstance((item as? BrowseTabCategory)?.id), true, true)
      }
    }
  }
}