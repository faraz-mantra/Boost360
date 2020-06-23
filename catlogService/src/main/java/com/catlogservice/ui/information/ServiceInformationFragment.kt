package com.catlogservice.ui.information

import com.catlogservice.R
import com.catlogservice.base.AppBaseFragment
import com.catlogservice.databinding.FragmentServiceInformationBinding
import com.catlogservice.viewmodel.ServiceViewModel

class ServiceInformationFragment : AppBaseFragment<FragmentServiceInformationBinding, ServiceViewModel>() {

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
    binding?.serverBy?.type?.hint = "e.g. Served by"
    binding?.serverBy?.value?.hint = "Mr. Shruti"
    binding?.duration?.type?.hint = "e.g Duration"
    binding?.duration?.value?.hint = "30 min"
    binding?.method?.type?.hint = "e.g. Method"
    binding?.method?.value?.hint = "Dry"

  }
}