package com.nowfloats.education.batches.ui.batchesfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.utils.Utils
import com.framework.base.BaseFragment
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.views.fabButton.FloatingActionButton
import com.framework.views.zero.FragmentZeroCase
import com.framework.views.zero.OnZeroCaseClicked
import com.framework.views.zero.RequestZeroCaseBuilder
import com.framework.views.zero.ZeroCases
import com.framework.views.zero.old.AppFragmentZeroCase
import com.framework.views.zero.old.AppOnZeroCaseClicked
import com.framework.views.zero.old.AppRequestZeroCaseBuilder
import com.framework.views.zero.old.AppZeroCases

import com.nowfloats.education.batches.BatchesActivity
import com.nowfloats.education.batches.adapter.BatchesAdapter
import com.nowfloats.education.batches.model.Data
import com.nowfloats.education.batches.ui.batchesdetails.BatchesDetailsFragment
import com.nowfloats.education.helper.Constants.BATCHES_DETAILS_FRAGMENT
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.ItemClickEventListener
import com.thinksity.R
import com.thinksity.databinding.BatchesFragmentBinding
import org.koin.android.ext.android.inject

class BatchesFragment : BaseFragment<BatchesFragmentBinding, BaseViewModel>(), ItemClickEventListener, AppOnZeroCaseClicked {

  private val myViewModel by inject<BatchesViewModel>()
  private val batchesAdapter: BatchesAdapter by lazy { BatchesAdapter(this) }
  private lateinit var zeroCaseFragment: AppFragmentZeroCase


  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

  }

  private fun initBatchesRecyclerview(view: View) {
    val recyclerview = view.findViewById<RecyclerView>(R.id.batches_recycler)
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    recyclerview.apply {
      layoutManager = gridLayoutManager
      adapter = batchesAdapter
    }

    if (Utils.isConnectedToInternet(requireContext())) {
      showLoader(getString(R.string.loading_batches))
      myViewModel.getUpcomingBatches()
    } else {
      showLongToast(getString(R.string.no_internet))
    }
  }

  private fun initLiveDataObservers() {
    myViewModel.apply {
      upcomingBatchResponse.observe(viewLifecycleOwner, Observer {
        if (!it.Data.isNullOrEmpty()) {
          nonEmptyView()
          setRecyclerviewAdapter(it.Data)
        }else emptyView()
        hideLoader()
      })

      errorResponse.observe(viewLifecycleOwner, Observer {
        hideLoader()
        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
      })

      deleteBatchResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
        if (!it.isNullOrBlank()) {
          hideLoader()
          if (it == SUCCESS) {
            Toast.makeText(requireContext(), getString(R.string.batch_deleted_successfully), Toast.LENGTH_SHORT).show()
            showLoader(getString(R.string.loading_batches))
            setDeleteBatchesLiveDataValue("")
            getUpcomingBatches()
          }
        }
      })
    }
  }

  private fun setRecyclerviewAdapter(batchesResponseData: List<Data>) {
    batchesAdapter.items = batchesResponseData
    batchesAdapter.notifyDataSetChanged()
  }

  fun setHeader(view: View) {
    val backButton: LinearLayout = view.findViewById(R.id.back_button)
    (view.findViewById(R.id.right_icon) as? ImageView)?.visibility = View.INVISIBLE
    val btnAdd: FloatingActionButton = view.findViewById(R.id.btn_add)
    val title: TextView = view.findViewById(R.id.title)
    title.text = getString(R.string.upcoming_batches)
    btnAdd.setOnClickListener {
      (activity as BatchesActivity).addFragment(
        BatchesDetailsFragment.newInstance(),
        BATCHES_DETAILS_FRAGMENT
      )
    }
    backButton.setOnClickListener { requireActivity().onBackPressed() }
  }

  override fun itemMenuOptionStatus(pos: Int, status: Boolean) {
    updateItemMenuOptionStatus(pos, status)
  }

  override fun onEditClick(data: Any, position: Int) {
    batchesAdapter.menuOption(position, false)
    (activity as BatchesActivity).addFragment(
      BatchesDetailsFragment.newInstance(data as Data),
      BATCHES_DETAILS_FRAGMENT
    )
  }

  override fun onDeleteClick(data: Any, position: Int) {
    batchesAdapter.menuOption(position, false)
    showLoader("Deleting batch")
    myViewModel.deleteUpcomingBatch(data as Data)
  }

  private fun updateItemMenuOptionStatus(position: Int, status: Boolean) {
    batchesAdapter.menuOption(position, status)
    batchesAdapter.notifyDataSetChanged()
  }

  companion object {
    fun newInstance(): BatchesFragment = BatchesFragment()
  }

  private fun nonEmptyView() {
    setHasOptionsMenu(true)
    binding?.mainlayout?.visible()
    binding?.childContainer?.gone()
  }
  private fun emptyView() {
    setHasOptionsMenu(false)
    binding?.mainlayout?.gone()
    binding?.childContainer?.visible()
  }
  override fun primaryButtonClicked() {
    (activity as BatchesActivity).addFragment(
      BatchesDetailsFragment.newInstance(),
      BATCHES_DETAILS_FRAGMENT
    )
  }

  override fun secondaryButtonClicked() {
  }

  override fun ternaryButtonClicked() {
  }

  override fun appOnBackPressed() {

  }


  override fun getLayout(): Int {
    return R.layout.batches_fragment
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    zeroCaseFragment = AppRequestZeroCaseBuilder(AppZeroCases.UPCOMING_BATCHES, this, requireActivity()).getRequest().build()
    addFragment(containerID = binding?.childContainer?.id, zeroCaseFragment,false)

    binding?.root?.let {
      setHeader(it)
      initLiveDataObservers()
      initBatchesRecyclerview(it)
    }


  }
}