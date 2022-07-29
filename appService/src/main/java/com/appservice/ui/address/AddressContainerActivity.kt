package com.appservice.ui.address

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.appservice.ui.address.ui.BusinessAddressFragment
import com.appservice.ui.address.ui.SearchLocationFragment
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar

open class AddressContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null

  override fun getLayout(): Int {
    return com.framework.R.layout.activity_fragment_container
  }


  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    intent?.extras?.getInt(FRAGMENT_TYPE)?.let { type = FragmentType.values()[it] }
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView() {
    super.onCreateView()
    setFragment()
  }

  override fun customTheme(): Int? {
    return when (type) {
      FragmentType.SEARCH_LOCATION_VIEW -> R.style.AppTheme_domain
      else -> R.style.AddressTheme
    }
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }

  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.ADDRESS_UPDATE_VIEW -> ContextCompat.getColor(this, R.color.colorPrimary)
      FragmentType.SEARCH_LOCATION_VIEW -> ContextCompat.getColor(this, R.color.black_4a4a4a)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.ADDRESS_UPDATE_VIEW, FragmentType.SEARCH_LOCATION_VIEW -> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.ADDRESS_UPDATE_VIEW, FragmentType.SEARCH_LOCATION_VIEW -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_new)
      else -> super.getNavigationIcon()
    }
  }

  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.ADDRESS_UPDATE_VIEW -> getString(R.string.business_address)
      FragmentType.SEARCH_LOCATION_VIEW -> getString(R.string.search_location)
      else -> super.getToolbarTitle()
    }
  }

  private fun shouldAddToBackStack(): Boolean {
    return when (type) {
      else -> false
    }
  }

  private fun setFragment() {
    val fragment = getFragmentInstance(type)
    fragment?.arguments = intent.extras
    binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
  }

  private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
    return when (type) {
      FragmentType.ADDRESS_UPDATE_VIEW -> BusinessAddressFragment.newInstance()
      FragmentType.SEARCH_LOCATION_VIEW -> SearchLocationFragment.newInstance()
      else -> BusinessAddressFragment.newInstance()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    for (fragment in supportFragmentManager.fragments) {
      fragment.onActivityResult(requestCode, resultCode, data)
    }
  }
}

fun Activity.startAddressFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean, isResult: Boolean = false) {
  val intent = Intent(this, AddressContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun AppCompatActivity.startAddressFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false, isResult: Boolean = false) {
  val intent = Intent(this, AddressContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}
