package com.nowfloats.education.toppers.ui.topperhome

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.framework.base.BaseFragment
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.models.firestore.FirestoreManager
import com.framework.views.fabButton.FloatingActionButton
import com.framework.views.zero.FragmentZeroCase
import com.framework.views.zero.OnZeroCaseClicked
import com.framework.views.zero.RequestZeroCaseBuilder
import com.framework.views.zero.ZeroCases
import com.inventoryorder.model.ordersdetails.OrderItem
import com.nowfloats.education.helper.Constants.SUCCESS
import com.nowfloats.education.helper.Constants.TOPPERS_DETAILS_FRAGMENT
import com.nowfloats.education.helper.Constants.TOPPERS_FRAGMENT
import com.nowfloats.education.helper.ItemClickEventListener
import com.nowfloats.education.toppers.ToppersActivity
import com.nowfloats.education.toppers.adapter.TopperAdapter
import com.nowfloats.education.toppers.model.Data
import com.nowfloats.education.toppers.ui.topperdetails.TopperDetailsFragment
import com.nowfloats.util.Utils
import com.thinksity.R
import com.thinksity.databinding.ToppersFragmentBinding
import org.koin.android.ext.android.inject

class ToppersFragment : BaseFragment<ToppersFragmentBinding, BaseViewModel>(), ItemClickEventListener,
  OnZeroCaseClicked {

  private val myViewModel by inject<ToppersViewModel>()
  private val toppersAdapter: TopperAdapter by lazy { TopperAdapter(this) }
  private lateinit var toppersActivity: ToppersActivity
  private lateinit var zeroCaseFragment: FragmentZeroCase



  private fun initBatchesRecyclerview(view: View) {
    val recyclerview = view.findViewById<RecyclerView>(R.id.topper_recycler)
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    recyclerview.apply {
      layoutManager = gridLayoutManager
      adapter = toppersAdapter
    }

    if (Utils.isNetworkConnected(requireContext())) {
      showLoader(getString(R.string.loading_our_topper))
      myViewModel.getOurToppers()
    } else {
      showLongToast(resources.getString(R.string.noInternet))
    }
  }

  private fun initLiveDataObservers() {
    myViewModel.apply {
      ourTopperResponse.observe(viewLifecycleOwner, Observer {
        if (!it.Data.isNullOrEmpty()) {
          nonEmptyView()
          setRecyclerviewAdapter(it.Data)
        } else{
          emptyView()
          setRecyclerviewAdapter(arrayListOf())
        }
        hideLoader()
      })

      errorMessage.observe(viewLifecycleOwner, {
        hideLoader()
        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
      })

      deleteTopperResponse.observe(viewLifecycleOwner, Observer {
        if (!it.isNullOrBlank()) {
          if (it == SUCCESS) {
            Toast.makeText(requireContext(), getString(R.string.topper_deleted_successfully), Toast.LENGTH_SHORT).show()
            showLoader(getString(R.string.loading_topper))
            setDeleteTopperLiveDataValue("")
            myViewModel.getOurToppers()
          }
        }
        hideLoader()
      })
    }
  }

  private fun setRecyclerviewAdapter(topperResponseData: List<Data>) {
    toppersAdapter.items = topperResponseData
    toppersAdapter.notifyDataSetChanged()
  }

  fun setHeader(view: View) {
    val backButton: LinearLayout = view.findViewById(R.id.back_button)
    (view.findViewById(R.id.right_icon) as? ImageView)?.visibility = View.INVISIBLE
    val btnAdd: FloatingActionButton = view.findViewById(R.id.btn_add)
    val title: TextView = view.findViewById(R.id.title)
    title.text = getString(R.string.our_toppers)
    btnAdd.setOnClickListener {
      (activity as ToppersActivity).addFragment(
        TopperDetailsFragment.newInstance(),
        TOPPERS_DETAILS_FRAGMENT
      )
    }
    backButton.setOnClickListener { requireActivity().onBackPressed() }
  }

  override fun itemMenuOptionStatus(pos: Int, status: Boolean) {
    updateItemMenuOptionStatus(pos, status)
  }

  override fun onEditClick(data: Any, position: Int) {
    toppersAdapter.menuOption(position, false)
    (activity as ToppersActivity).addFragment(
      TopperDetailsFragment.newInstance(data as Data, true),
      TOPPERS_FRAGMENT
    )
  }

  override fun onDeleteClick(data: Any, position: Int) {
    toppersAdapter.menuOption(position, false)
    showLoader(getString(R.string.deleting_topper))
    myViewModel.deleteOurTopper(data as Data)
  }

  private fun updateItemMenuOptionStatus(position: Int, status: Boolean) {
    toppersAdapter.menuOption(position, status)
    toppersAdapter.notifyDataSetChanged()
  }

  companion object {
    fun newInstance(): ToppersFragment = ToppersFragment()
  }

  override fun getLayout(): Int {
    return R.layout.toppers_fragment
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    zeroCaseFragment = RequestZeroCaseBuilder(ZeroCases.TOPPERS, this, baseActivity).getRequest().build()

    addFragment(containerID = binding?.childContainer?.id, zeroCaseFragment,false)

    toppersActivity = activity as ToppersActivity
    binding?.root?.let {
      setHeader(it)
      initLiveDataObservers()
      initBatchesRecyclerview(it)
    }

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

//    binding?.bookingRecycler?.gone()
//    binding?.errorView?.visible()
//    binding?.btnAdd?.gone()
  }

  override fun primaryButtonClicked() {
    (activity as ToppersActivity).addFragment(
      TopperDetailsFragment.newInstance(),
      TOPPERS_DETAILS_FRAGMENT
    )
  }



  override fun secondaryButtonClicked() {
  }

  override fun ternaryButtonClicked() {
  }

  override fun onBackPressed() {
  }
}