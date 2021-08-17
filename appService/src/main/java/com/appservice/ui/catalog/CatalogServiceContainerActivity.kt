package com.appservice.ui.catalog

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.ui.catalog.catalogProduct.addProduct.ProductDetailFragment
import com.appservice.ui.catalog.catalogProduct.addProduct.information.ProductInformationFragment
import com.appservice.ui.catalog.catalogService.addService.ServiceDetailFragment
import com.appservice.ui.catalog.catalogService.addService.information.ServiceInformationFragment
import com.appservice.ui.catalog.catalogService.addService.weeklyTime.ServiceTimingFragment
import com.appservice.ui.catalog.catalogService.listing.ServiceListingFragment
import com.appservice.ui.catalog.common.CreateCategoryFragment
import com.appservice.ui.catalog.common.WeeklyAppointmentFragment
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar


open class CatalogServiceContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null
  private var fragment: AppBaseFragment<*, *>? = null
  private var serviceDetailFragment: ServiceDetailFragment? = null
  private var serviceInformationFragment: ServiceInformationFragment? = null
  private var productDetailFragment: ProductDetailFragment? = null
  private var productInformationFragment: ProductInformationFragment? = null
  private var weeklyAppointmentFragment: WeeklyAppointmentFragment? = null
  private var serviceTimingFragment: ServiceTimingFragment? = null
  private var createCategoryFragment: CreateCategoryFragment? = null
  private var serviceListingFragment: ServiceListingFragment? = null

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
      FragmentType.CREATE_CATEGORY, FragmentType.SERVICE_LISTING -> R.style.CatalogTheme
      FragmentType.PRODUCT_DETAIL_VIEW, FragmentType.SERVICE_DETAIL_VIEW, FragmentType.PRODUCT_INFORMATION -> R.style.AddCatalogTheme
      FragmentType.SERVICE_INFORMATION, FragmentType.SERVICE_TIMING_FRAGMENT -> R.style.CatalogTheme_Information
      else -> super.customTheme()
    }
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }

  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.PRODUCT_INFORMATION, FragmentType.PRODUCT_DETAIL_VIEW, FragmentType.SERVICE_DETAIL_VIEW,
      FragmentType.SERVICE_LISTING, FragmentType.CREATE_CATEGORY,
      -> ContextCompat.getColor(this, R.color.colorPrimary)
      FragmentType.SERVICE_INFORMATION, FragmentType.SERVICE_TIMING_FRAGMENT -> ContextCompat.getColor(this, R.color.color_primary)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.SERVICE_INFORMATION, FragmentType.SERVICE_LISTING,
      FragmentType.SERVICE_DETAIL_VIEW,
      -> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.SERVICE_INFORMATION, FragmentType.SERVICE_LISTING, FragmentType.SERVICE_DETAIL_VIEW,
      FragmentType.PRODUCT_DETAIL_VIEW, FragmentType.PRODUCT_INFORMATION, FragmentType.SERVICE_TIMING_FRAGMENT,
      FragmentType.CREATE_CATEGORY,
      -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_new)
      else -> super.getNavigationIcon()
    }
  }


  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.SERVICE_INFORMATION -> resources.getString(R.string.other_information_n)
      FragmentType.SERVICE_DETAIL_VIEW -> resources.getString(R.string.service_details_n)
      FragmentType.PRODUCT_DETAIL_VIEW -> resources.getString(R.string.product_details_n)
      FragmentType.PRODUCT_INFORMATION -> resources.getString(R.string.additional_information)
      FragmentType.SERVICE_TIMING_FRAGMENT -> getString(R.string.weekly_appointment)
      FragmentType.CREATE_CATEGORY -> getString(R.string.categories)
      FragmentType.SERVICE_LISTING -> getString(R.string.services)
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
      FragmentType.CREATE_CATEGORY -> {
        createCategoryFragment = CreateCategoryFragment.newInstance()
        createCategoryFragment
      }
      FragmentType.SERVICE_TIMING_FRAGMENT -> {
        serviceTimingFragment = ServiceTimingFragment.newInstance()
        serviceTimingFragment
      }

      FragmentType.SERVICE_DETAIL_VIEW -> {
        serviceDetailFragment = ServiceDetailFragment.newInstance()
        serviceDetailFragment
      }
      FragmentType.SERVICE_INFORMATION -> {
        serviceInformationFragment = ServiceInformationFragment.newInstance()
        serviceInformationFragment
      }
      FragmentType.PRODUCT_DETAIL_VIEW -> {
        productDetailFragment = ProductDetailFragment.newInstance()
        productDetailFragment
      }
      FragmentType.PRODUCT_INFORMATION -> {
        productInformationFragment = ProductInformationFragment.newInstance()
        productInformationFragment
      }
      FragmentType.SERVICE_LISTING -> {
        serviceListingFragment = ServiceListingFragment.newInstance()
        serviceListingFragment
      }
      else -> throw IllegalFragmentTypeException()
    }
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    serviceListingFragment?.onActivityResult(requestCode, resultCode, data)
    serviceDetailFragment?.onActivityResult(requestCode, resultCode, data)
    serviceInformationFragment?.onActivityResult(requestCode, resultCode, data)
    productInformationFragment?.onActivityResult(requestCode, resultCode, data)
    productDetailFragment?.onActivityResult(requestCode, resultCode, data)
    weeklyAppointmentFragment?.onActivityResult(requestCode, resultCode, data)
    createCategoryFragment?.onActivityResult(requestCode, resultCode, data)
  }
}

fun Fragment.startFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false, isResult: Boolean = false) {
  val intent = Intent(activity, CatalogServiceContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun startFragmentActivityNew(activity: Activity, type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean, isResult: Boolean = false) {
  val intent = Intent(activity, CatalogServiceContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) activity.startActivity(intent) else activity.startActivityForResult(intent, 300)
}

fun AppCompatActivity.startFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false) {
  val intent = Intent(this, CatalogServiceContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}
