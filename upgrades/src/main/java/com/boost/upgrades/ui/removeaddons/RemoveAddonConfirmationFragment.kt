package com.boost.upgrades.ui.removeaddons

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import kotlinx.android.synthetic.main.remove_addon_confirmation_fragment.*

class RemoveAddonConfirmationFragment : BaseFragment() {

  companion object {
    fun newInstance() = RemoveAddonConfirmationFragment()
  }

  private lateinit var viewModel: RemoveAddonConfirmationViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.remove_addon_confirmation_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(RemoveAddonConfirmationViewModel::class.java)


    remove_confirm_submit.setOnClickListener {
      (activity as UpgradeActivity).popFragmentFromBackStack()
    }
  }

}
