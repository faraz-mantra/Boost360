package com.appservice.ui.updatesBusiness

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.AddUpdateBusinessFragmentBinding
import com.appservice.viewmodel.UpdatesViewModel

class AddUpdateBusinessFragment : AppBaseFragment<AddUpdateBusinessFragmentBinding, UpdatesViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddUpdateBusinessFragment {
      val fragment = AddUpdateBusinessFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.add_update_business_fragment
  }

  override fun getViewModelClass(): Class<UpdatesViewModel> {
    return UpdatesViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
  }

}