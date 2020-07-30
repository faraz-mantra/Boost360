package com.appservice.ui.paymentgateway

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar

open class PaymentGatewayContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null

  private var paymentGatewayFragment: PaymentGatewayFragment? = null
  private var scanPanCardFragment: ScanPanCardFragment? = null
  private var kycDetailsFragment: KYCDetailsFragment? = null
  private var kycStatusFragment: KYCStatusFragment? = null
  private var cropImageFragment: CropImageFragment? = null


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
      FragmentType.SCAN_PAN_CARD, FragmentType.CROP_IMAGE -> R.style.AppTheme_payment_dark
      else -> super.customTheme()
    }
  }

  override fun getToolbarTitleGravity(): Int {
    return when (type) {
      FragmentType.KYC_STATUS, FragmentType.KYC_DETAILS -> Gravity.CENTER
      else -> Gravity.START
    }
  }

  override fun getToolbarTitleSize(): Float? {
    return resources.getDimension(R.dimen.heading_7)
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }

  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY, FragmentType.KYC_DETAILS, FragmentType.KYC_STATUS -> ContextCompat.getColor(this, R.color.colorPrimary)
      FragmentType.SCAN_PAN_CARD, FragmentType.CROP_IMAGE -> ContextCompat.getColor(this, R.color.color_primary)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY, FragmentType.SCAN_PAN_CARD, FragmentType.KYC_DETAILS, FragmentType.CROP_IMAGE,
      FragmentType.KYC_STATUS -> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY, FragmentType.KYC_DETAILS, FragmentType.KYC_STATUS -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_new)
      else -> super.getNavigationIcon()
    }
  }

  override fun isVisibleBackButton(): Boolean {
    return when (type) {
      FragmentType.SCAN_PAN_CARD, FragmentType.CROP_IMAGE -> false
      else -> super.isVisibleBackButton()
    }
  }

  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY -> getString(R.string.self_branded_payment_gateway)
      FragmentType.SCAN_PAN_CARD, FragmentType.CROP_IMAGE -> getString(R.string.take_photo_of_your_pan_card)
      FragmentType.KYC_STATUS, FragmentType.KYC_DETAILS -> getString(R.string.kyc_information)
      else -> super.getToolbarTitle()
    }
  }


  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val toolbarMenu = menu ?: return super.onCreateOptionsMenu(menu)
    val menuRes = getMenuRes() ?: return super.onCreateOptionsMenu(menu)
    menuInflater.inflate(menuRes, toolbarMenu)
    return true
  }

  open fun getMenuRes(): Int? {
    return when (type) {
      else -> null
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
      FragmentType.PAYMENT_GATEWAY -> {
        paymentGatewayFragment = PaymentGatewayFragment.newInstance()
        paymentGatewayFragment
      }
      FragmentType.SCAN_PAN_CARD -> {
        scanPanCardFragment = ScanPanCardFragment.newInstance()
        scanPanCardFragment
      }
      FragmentType.KYC_DETAILS -> {
        kycDetailsFragment = KYCDetailsFragment()
        kycDetailsFragment
      }
      FragmentType.KYC_STATUS -> {
        kycStatusFragment = KYCStatusFragment()
        kycStatusFragment
      }
      FragmentType.CROP_IMAGE -> {
        cropImageFragment = CropImageFragment()
        cropImageFragment
      }
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    kycDetailsFragment?.onActivityResult(requestCode, resultCode, data)
  }

  override fun onBackPressed() {
    when (type) {
      FragmentType.KYC_STATUS -> kycStatusFragment?.onNavPressed()
      else -> finish()
    }
  }
}

fun Fragment.startFragmentPaymentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false, isResult: Boolean = false) {
  val intent = Intent(activity, PaymentGatewayContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun startFragmentPaymentActivityNew(activity: Activity, type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean) {
  val intent = Intent(activity, PaymentGatewayContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  activity.startActivity(intent)
}

fun AppCompatActivity.startFragmentPaymentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false) {
  val intent = Intent(this, PaymentGatewayContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}

