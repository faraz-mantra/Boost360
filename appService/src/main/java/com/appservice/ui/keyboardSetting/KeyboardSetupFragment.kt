package com.appservice.ui.keyboardSetting

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.databinding.FragmentKeyboardSetupBinding
import com.appservice.model.keyboard.KeyboardActionItem
import com.appservice.model.keyboard.KeyboardTabsResponse
import com.appservice.model.keyboard.getKeyboardTabs
import com.appservice.model.keyboard.saveKeyboardTabs
import com.appservice.viewmodel.KeyboardViewModel
import com.framework.extensions.observeOnce
import com.framework.utils.toArrayList

class KeyboardSetupFragment : AppBaseFragment<FragmentKeyboardSetupBinding, KeyboardViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): KeyboardSetupFragment {
      val fragment = KeyboardSetupFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_keyboard_setup
  }

  override fun getViewModelClass(): Class<KeyboardViewModel> {
    return KeyboardViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.btnKeyboardTabs, binding?.outlineOne, binding?.outlineTwo)
    loadLocalData()
    toggleView()
  }

  private fun toggleView() {
    binding?.switchDarkMode?.setOnClickListener {
      binding?.switchDarkMode?.isOn = true
      showShortToast(getString(R.string.coming_soon))
    }
    binding?.switchAutoDarken?.setOnClickListener {
      binding?.switchAutoDarken?.isOn = true
      showShortToast(getString(R.string.coming_soon))
    }
  }

  private fun loadLocalData() {
    viewModel?.getMessageUpdates(baseActivity)?.observeOnce(viewLifecycleOwner, { resp ->
      val data = (resp as? KeyboardTabsResponse)?.data
      var actionData = (data?.firstOrNull { it.types?.contains(sessionLocal.fP_AppExperienceCode ?: "") == true }?.actionItem)?.toArrayList()
      if (actionData.isNullOrEmpty().not()) {
        val previousData = getKeyboardTabs()
        if (previousData.isNullOrEmpty().not()) {
          val newList = ArrayList<KeyboardActionItem>()
          previousData.zip(actionData!!) { old, new ->
            if (old.type.equals(new.type)) new.isEnabled = old.isEnabled
            newList.add(new)
          }
          actionData = newList
        }
        saveKeyboardTabs(actionData)
      }
    })
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnKeyboardTabs -> {
        if (getKeyboardTabs().isNullOrEmpty().not()) startKeyboardFragmentActivity(FragmentType.KEYBOARD_TABS_FRAGMENT)
        else loadLocalData()
      }
      binding?.outlineOne -> showOutlineView(true)
      binding?.outlineTwo -> showOutlineView(false)
    }
  }

  private fun showOutlineView(isFirst: Boolean) {
    if (isFirst) showShortToast(getString(R.string.coming_soon))
//    binding?.outlineOne?.background = if (isFirst) ContextCompat.getDrawable(baseActivity, R.drawable.stroke_accent_keyboard) else null
//    binding?.outlineTwo?.background = if (isFirst.not()) ContextCompat.getDrawable(baseActivity, R.drawable.stroke_accent_keyboard) else null
  }
}