package com.appservice.ui.catlogService

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.appservice.ui.catlogService.information.ServiceInformationFragment
import com.appservice.ui.catlogService.service.ServiceDetailFragment
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder


open class FragmentContainerServiceActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null
  private var serviceDetailFragment: ServiceDetailFragment? = null
  private var serviceInformationFragment: ServiceInformationFragment? = null


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

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }

  override fun getToolbarTitleSize(): Float? {
    return resources.getDimension(R.dimen.body_2)
  }

  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.SERVICE_INFORMATION, FragmentType.SERVICE_DETAIL_VIEW -> ContextCompat.getColor(this, R.color.color_primary)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.SERVICE_INFORMATION, FragmentType.SERVICE_DETAIL_VIEW -> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.SERVICE_INFORMATION, FragmentType.SERVICE_DETAIL_VIEW -> ContextCompat.getDrawable(this, R.drawable.ic_arrow_left)
      else -> super.getNavigationIcon()
    }
  }

  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.SERVICE_INFORMATION -> resources.getString(R.string.other_information)
      FragmentType.SERVICE_DETAIL_VIEW -> resources.getString(R.string.service_details)
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
      FragmentType.SERVICE_DETAIL_VIEW -> {
        serviceDetailFragment = ServiceDetailFragment.newInstance()
        serviceDetailFragment
      }
      FragmentType.SERVICE_INFORMATION -> {
        serviceInformationFragment = ServiceInformationFragment.newInstance()
        serviceInformationFragment
      }
      else -> throw IllegalFragmentTypeException()
    }
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    serviceDetailFragment?.onActivityResult(requestCode, resultCode, data)
    serviceInformationFragment?.onActivityResult(requestCode, resultCode, data)
  }

  override fun onBackPressed() {
    if (serviceInformationFragment != null) {
      serviceInformationFragment?.onNavPressed()
    } else super.onBackPressed()
  }
}

fun Fragment.startFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false, isResult: Boolean = false) {
  val intent = Intent(activity, FragmentContainerServiceActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun startFragmentActivityNew(activity: Activity, type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean, isResult: Boolean = false) {
  val intent = Intent(activity, FragmentContainerServiceActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) activity.startActivity(intent) else activity.startActivityForResult(intent, 300)
}

fun AppCompatActivity.startFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false) {
  val intent = Intent(this, FragmentContainerServiceActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}
