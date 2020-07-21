package com.appservice.ui.catlogService.information

import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentServiceInformationBinding
import com.appservice.model.KeySpecification
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.viewmodel.ServiceViewModel
import com.google.android.material.chip.Chip


class ServiceInformationFragment : AppBaseFragment<FragmentServiceInformationBinding, ServiceViewModel>(), RecyclerItemClickListener {

  companion object {
    fun newInstance(): ServiceInformationFragment {
      return ServiceInformationFragment()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_service_information
  }

  override fun getViewModelClass(): Class<ServiceViewModel> {
    return ServiceViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.cbFacebookPage, binding?.cbGoogleMerchantCenter, binding?.cbTwitterPage)
    serviceTagsSet(arrayListOf("Nowfloat", "Test 1", "Welcome", "Boost", "Test 1", "Welcome"))
    specificationAdapter()
  }

  private fun specificationAdapter() {
    binding?.rvSpecification?.apply {
      val adapterSpecification = AppBaseRecyclerViewAdapter(baseActivity, KeySpecification().data(), this@ServiceInformationFragment)
      adapter = adapterSpecification
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
    }
  }

  private fun serviceTagsSet(serviceTags: MutableList<String>) {
    serviceTags.forEach { tag ->
      val mChip: Chip = baseActivity.layoutInflater.inflate(R.layout.item_chip, binding?.chipsService, false) as Chip
      mChip.text = tag
      mChip.setOnCloseIconClickListener {
        binding?.chipsService?.removeView(mChip)
        serviceTags.remove(tag)
      }
      binding?.chipsService?.addView(mChip)
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }

}