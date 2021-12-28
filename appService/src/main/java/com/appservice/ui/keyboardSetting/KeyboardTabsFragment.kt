package com.appservice.ui.keyboardSetting

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentKeyboardSetupBinding
import com.appservice.databinding.FragmentKeyboardTabsBinding
import com.appservice.model.keyboard.KeyboardActionItem
import com.appservice.model.keyboard.getKeyboardTabs
import com.appservice.model.keyboard.saveKeyboardTabs
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.viewmodel.KeyboardViewModel
import com.framework.models.BaseViewModel
import java.util.ArrayList

class KeyboardTabsFragment : AppBaseFragment<FragmentKeyboardTabsBinding, KeyboardViewModel>(), RecyclerItemClickListener {

  val itemListTabs= getKeyboardTabs()

  var adapterTab: AppBaseRecyclerViewAdapter<KeyboardActionItem>?=null
  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): KeyboardTabsFragment {
      val fragment = KeyboardTabsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_keyboard_tabs
  }

  override fun getViewModelClass(): Class<KeyboardViewModel> {
    return KeyboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setAdapterTabs()
  }

  private fun setAdapterTabs() {
    binding?.baseRecyclerView?.apply {
      adapterTab = AppBaseRecyclerViewAdapter(baseActivity, itemListTabs, this@KeyboardTabsFragment)
      adapter = adapterTab
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val data = (item as? KeyboardActionItem) ?: return
    itemListTabs.map { if (it.type.equals(data.type)) it.isEnabled = data.isEnabled?.not() ?: false }
    saveKeyboardTabs(itemListTabs)
    if (adapterTab!=null) adapterTab?.notifyDataSetChanged()
  }
}