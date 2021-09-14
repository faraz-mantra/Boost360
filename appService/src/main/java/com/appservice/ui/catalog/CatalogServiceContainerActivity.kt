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
import com.appservice.appointment.ui.*
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.appservice.ui.catalog.catalogProduct.addProduct.ProductDetailFragment
import com.appservice.ui.catalog.catalogProduct.addProduct.information.ProductInformationFragment
import com.appservice.ui.catalog.catalogProduct.listing.FragmentProductCategory
import com.appservice.ui.catalog.catalogProduct.listing.FragmentProductHome
import com.appservice.ui.catalog.catalogProduct.listing.FragmentProductListing
import com.appservice.ui.catalog.catalogService.ServiceCatalogHomeFragment
import com.appservice.ui.catalog.catalogService.addService.ServiceDetailFragment
import com.appservice.ui.catalog.catalogService.addService.information.ServiceInformationFragment
import com.appservice.ui.catalog.catalogService.addService.weeklyTime.ServiceTimingFragment
import com.appservice.ui.catalog.catalogService.listing.ServiceListingFragment
import com.appservice.ui.catalog.common.WeeklyAppointmentFragment
import com.appservice.ui.ecomAptSetting.*
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar


open class CatalogServiceContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var serviceInformationFragment: ServiceInformationFragment? = null
  private var productDetailFragment: ProductDetailFragment? = null
  private var productInformationFragment: ProductInformationFragment? = null
  private var weeklyAppointmentFragment: WeeklyAppointmentFragment? = null
  private var serviceTimingFragment: ServiceTimingFragment? = null
  private var createCategoryFragment: CreateCategoryFragment? = null
  private var serviceListingFragment: ServiceListingFragment? = null
  private var serviceCatalogHomeFragment: ServiceCatalogHomeFragment? = null
  private var fragmentAppointmentSettings: FragmentAppointmentSettings? = null
  private var fragmentCatalogSettings: FragmentCatalogSettings? = null
  private var fragmentCustomerInvoiceSetup: FragmentCustomerInvoiceSetup? = null
  private var fragmentCustomerPolicies: FragmentCustomerPolicies? = null
  private var fragmentAccountAddHome: FragmentAccountAddHome? = null
  private var fragmentAddAccountDetails: FragmentAddAccountDetails? = null
  private var fragmentEditAccountDetails: FragmentEditBankDetails? = null
  private var type: FragmentType? = null
  private var serviceDetailFragment: ServiceDetailFragment? = null
  private var fragmentPaymentCollectionSetup: FragmentPaymentCollectionSetup? = null
  private var fragmentProductCategory: FragmentProductCategory? = null
  private var fragmentProductHome: FragmentProductHome? = null
  private var fragmentProductListing: FragmentProductListing? = null

  private var fragmentEcommerceSettings: FragmentEcommerceSettings? = null
  private var fragmentEcommerceCatalogSettings: FragmentEcommerceCatalogSettings? = null
  private var fragmentEcommerceCustomerInvoiceSetup: FragmentEcommerceCustomerInvoiceSetup? = null
  private var fragmentEcommerceCustomerPolicies: FragmentEcommerceCustomerPolicies? = null
  private var fragmentEcommercePaymentCollectionSetup: FragmentEcommercePaymentCollectionSetup? = null
  private var fragmentEcomDeliveryConfig: FragmentEcomDeliveryConfig? = null

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
       FragmentType.APPOINTMENT_CATALOG_SETTINGS, FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_INVOICE,
      FragmentType.APPOINTMENT_PAYMENT_SETTINGS, FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_POLICIES, FragmentType.APPOINTMENT_FRAGMENT_ACCOUNT_ADD_HOME, FragmentType.APPOINTMENT_ADD_ACCOUNT_DETAILS,
      FragmentType.EDIT_ACCOUNT_DETAILS, FragmentType.FRAGMENT_PRODUCT_LISTING, FragmentType.FRAGMENT_PRODUCT_HOME, FragmentType.FRAGMENT_PRODUCT_CATEGORY,
      -> R.style.CatalogTheme
      FragmentType.SERVICE_INFORMATION, FragmentType.SERVICE_TIMING_FRAGMENT -> R.style.CatalogTheme_Information
      FragmentType.SERVICE_CATALOG_HOME_FRAGMENT -> R.style.OffersThemeBase
      FragmentType.APPOINTMENT_SETTINGS,
      -> R.style.CatalogTheme_FragmentAppointment
      FragmentType.ECOMMERCE_SETTINGS,
      FragmentType.ECOMMERCE_DELIVERY_CONFIG,
      FragmentType.ECOMMERCE_PAYMENT_SETTINGS,
      FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_POLICIES,
      FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_INVOICE,
      FragmentType.ECOMMERCE_CATALOG_SETTINGS,
      FragmentType.ECOMMERCE_ADD_ACCOUNT_DETAILS,
      -> {
        R.style.EcommerceSettings
      }
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
      FragmentType.SERVICE_CATALOG_HOME_FRAGMENT,
      FragmentType.APPOINTMENT_CATALOG_SETTINGS, FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_INVOICE, FragmentType.FRAGMENT_PRODUCT_LISTING, FragmentType.FRAGMENT_PRODUCT_HOME, FragmentType.FRAGMENT_PRODUCT_CATEGORY,
      FragmentType.APPOINTMENT_FRAGMENT_ACCOUNT_ADD_HOME, FragmentType.APPOINTMENT_ADD_ACCOUNT_DETAILS, FragmentType.EDIT_ACCOUNT_DETAILS,
      FragmentType.APPOINTMENT_SETTINGS, FragmentType.APPOINTMENT_PAYMENT_SETTINGS, FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_POLICIES,
      FragmentType.ECOMMERCE_SETTINGS,
      FragmentType.ECOMMERCE_DELIVERY_CONFIG,
      FragmentType.ECOMMERCE_PAYMENT_SETTINGS,
      FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_POLICIES,
      FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_INVOICE,
      FragmentType.ECOMMERCE_CATALOG_SETTINGS,
      FragmentType.ECOMMERCE_ADD_ACCOUNT_DETAILS,
      -> ContextCompat.getColor(this, R.color.orange)
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
      FragmentType.SERVICE_INFORMATION, FragmentType.SERVICE_LISTING, FragmentType.SERVICE_DETAIL_VIEW, FragmentType.APPOINTMENT_ADD_ACCOUNT_DETAILS,
      FragmentType.PRODUCT_DETAIL_VIEW, FragmentType.PRODUCT_INFORMATION, FragmentType.SERVICE_TIMING_FRAGMENT, FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_POLICIES,
      FragmentType.CREATE_CATEGORY, FragmentType.SERVICE_CATALOG_HOME_FRAGMENT, FragmentType.APPOINTMENT_CATALOG_SETTINGS, FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_INVOICE,
      FragmentType.APPOINTMENT_SETTINGS, FragmentType.APPOINTMENT_FRAGMENT_ACCOUNT_ADD_HOME, FragmentType.APPOINTMENT_PAYMENT_SETTINGS, FragmentType.EDIT_ACCOUNT_DETAILS, FragmentType.FRAGMENT_PRODUCT_LISTING, FragmentType.FRAGMENT_PRODUCT_HOME, FragmentType.FRAGMENT_PRODUCT_CATEGORY,
      FragmentType.ECOMMERCE_SETTINGS,
      FragmentType.ECOMMERCE_DELIVERY_CONFIG,
      FragmentType.ECOMMERCE_PAYMENT_SETTINGS,
      FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_POLICIES,
      FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_INVOICE,
      FragmentType.ECOMMERCE_CATALOG_SETTINGS,
      FragmentType.ECOMMERCE_ADD_ACCOUNT_DETAILS,
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
      FragmentType.APPOINTMENT_SETTINGS -> getString(R.string.appointment_settings)
      FragmentType.APPOINTMENT_PAYMENT_SETTINGS -> getString(R.string.payment_collection_setup)
      FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_POLICIES -> getString(R.string.policies_for_customer)
      FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_INVOICE -> getString(R.string.customer_invoice_setup)
      FragmentType.APPOINTMENT_CATALOG_SETTINGS -> getString(R.string.catalog_setup)
      FragmentType.APPOINTMENT_ADD_ACCOUNT_DETAILS -> getString(R.string.my_bank_account)
      FragmentType.ECOMMERCE_SETTINGS -> getString(R.string.ecommerce_settings)
      FragmentType.ECOMMERCE_PAYMENT_SETTINGS -> getString(R.string.payment_collection_setup)
      FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_POLICIES -> getString(R.string.policies_for_customer)
      FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_INVOICE -> getString(R.string.customer_invoice_setup)
      FragmentType.ECOMMERCE_CATALOG_SETTINGS -> getString(R.string.catalog_setup)
      FragmentType.ECOMMERCE_ADD_ACCOUNT_DETAILS -> getString(R.string.my_bank_account)
      FragmentType.EDIT_ACCOUNT_DETAILS -> getString(R.string.my_bank_account)
      FragmentType.SERVICE_CATALOG_HOME_FRAGMENT -> getString(R.string.catalog)
      FragmentType.FRAGMENT_PRODUCT_LISTING -> getString(R.string.catalog)
      FragmentType.FRAGMENT_PRODUCT_HOME -> getString(R.string.catalog)
      FragmentType.FRAGMENT_PRODUCT_CATEGORY -> getString(R.string.catalog)
      FragmentType.ECOMMERCE_DELIVERY_CONFIG -> getString(R.string.delivery_setup)
      else -> super.getToolbarTitle()
    }
  }

  override fun getToolbarTitleGravity(): Int {
    return when (type) {
      FragmentType.SERVICE_TIMING_FRAGMENT, FragmentType.SERVICE_INFORMATION -> Gravity.CENTER
      else -> Gravity.START
    }
  }

  override fun isHideToolbar(): Boolean {
    return when (type) {
      FragmentType.APPOINTMENT_FRAGMENT_ACCOUNT_ADD_HOME -> {
        true
      }
      else -> super.isHideToolbar()
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
      FragmentType.ECOMMERCE_CATALOG_SETTINGS -> {
        fragmentEcommerceCatalogSettings = FragmentEcommerceCatalogSettings.newInstance()
        fragmentEcommerceCatalogSettings
      }
      FragmentType.SERVICE_TIMING_FRAGMENT -> {
        serviceTimingFragment = ServiceTimingFragment.newInstance()
        serviceTimingFragment
      }
      FragmentType.ECOMMERCE_DELIVERY_CONFIG -> {
        fragmentEcomDeliveryConfig = FragmentEcomDeliveryConfig.newInstance()
        fragmentEcomDeliveryConfig
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
      FragmentType.SERVICE_CATALOG_HOME_FRAGMENT -> {
        serviceCatalogHomeFragment = ServiceCatalogHomeFragment.newInstance()
        serviceCatalogHomeFragment
      }
      FragmentType.FRAGMENT_PRODUCT_HOME -> {
        fragmentProductHome = FragmentProductHome.newInstance()
        fragmentProductHome
      }
      FragmentType.FRAGMENT_PRODUCT_LISTING -> {
        fragmentProductListing = FragmentProductListing.newInstance()
        fragmentProductListing
      }
      FragmentType.FRAGMENT_PRODUCT_CATEGORY -> {
        fragmentProductCategory = FragmentProductCategory.newInstance()
        fragmentProductCategory
      }
      FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_INVOICE -> {
        fragmentCustomerInvoiceSetup = FragmentCustomerInvoiceSetup.newInstance()
        fragmentCustomerInvoiceSetup
      }
      FragmentType.APPOINTMENT_PAYMENT_SETTINGS -> {
        fragmentPaymentCollectionSetup = FragmentPaymentCollectionSetup.newInstance()
        fragmentPaymentCollectionSetup
      }
      FragmentType.APPOINTMENT_FRAGMENT_CUSTOMER_POLICIES -> {
        fragmentCustomerPolicies = FragmentCustomerPolicies.newInstance()
        fragmentCustomerPolicies
      }
      FragmentType.APPOINTMENT_SETTINGS -> {
        fragmentAppointmentSettings = FragmentAppointmentSettings.newInstance()
        fragmentAppointmentSettings
      }
      FragmentType.APPOINTMENT_CATALOG_SETTINGS -> {
        fragmentCatalogSettings = FragmentCatalogSettings.newInstance()
        fragmentCatalogSettings
      }
      FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_INVOICE -> {
        fragmentEcommerceCustomerInvoiceSetup = FragmentEcommerceCustomerInvoiceSetup.newInstance()
        fragmentEcommerceCustomerInvoiceSetup
      }
      FragmentType.ECOMMERCE_PAYMENT_SETTINGS -> {
        fragmentEcommercePaymentCollectionSetup = FragmentEcommercePaymentCollectionSetup.newInstance()
        fragmentEcommercePaymentCollectionSetup
      }
      FragmentType.ECOMMERCE_FRAGMENT_CUSTOMER_POLICIES -> {
        fragmentEcommerceCustomerPolicies = FragmentEcommerceCustomerPolicies.newInstance()
        fragmentEcommerceCustomerPolicies
      }
      FragmentType.ECOMMERCE_SETTINGS -> {
        fragmentEcommerceSettings = FragmentEcommerceSettings.newInstance()
        fragmentEcommerceSettings
      }
      FragmentType.APPOINTMENT_FRAGMENT_ACCOUNT_ADD_HOME -> {
        fragmentAccountAddHome = FragmentAccountAddHome.newInstance()
        fragmentAccountAddHome
      }
      FragmentType.APPOINTMENT_ADD_ACCOUNT_DETAILS -> {
        fragmentAddAccountDetails = FragmentAddAccountDetails.newInstance()
        fragmentAddAccountDetails
      }
      FragmentType.EDIT_ACCOUNT_DETAILS -> {
        fragmentEditAccountDetails = FragmentEditBankDetails.newInstance()
        fragmentEditAccountDetails
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
    fragmentProductListing?.onActivityResult(requestCode, resultCode, data)
    productDetailFragment?.onActivityResult(requestCode, resultCode, data)
    weeklyAppointmentFragment?.onActivityResult(requestCode, resultCode, data)
    createCategoryFragment?.onActivityResult(requestCode, resultCode, data)
    serviceCatalogHomeFragment?.onActivityResult(requestCode, resultCode, data)
    fragmentEcommerceSettings?.onActivityResult(requestCode, resultCode, data)
    fragmentCustomerPolicies?.onActivityResult(requestCode, resultCode, data)
    fragmentPaymentCollectionSetup?.onActivityResult(requestCode, resultCode, data)
    fragmentCustomerInvoiceSetup?.onActivityResult(requestCode, resultCode, data)
    serviceCatalogHomeFragment?.onActivityResult(requestCode, resultCode, data)
    fragmentEditAccountDetails?.onActivityResult(requestCode, resultCode, data)
    fragmentEcomDeliveryConfig?.onActivityResult(requestCode, resultCode, data)
    fragmentEcommerceCustomerInvoiceSetup?.onActivityResult(requestCode, resultCode, data)
  }
}

fun Fragment.startFragmentActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false,
  isResult: Boolean = false
) {
  val intent = Intent(activity, CatalogServiceContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun startFragmentActivityNew(
  activity: Activity,
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean,
  isResult: Boolean = false
) {
  val intent = Intent(activity, CatalogServiceContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) activity.startActivity(intent) else activity.startActivityForResult(
    intent,
    300
  )
}

fun AppCompatActivity.startFragmentActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false
) {
  val intent = Intent(this, CatalogServiceContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}
