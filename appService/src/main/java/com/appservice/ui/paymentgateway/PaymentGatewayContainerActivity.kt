package com.appservice.ui.paymentgateway

import android.app.Activity
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
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar


open class PaymentGatewayContainerActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null

  private var inactivePaymentGatewayFragment: InactivePaymentGatewayFragment? = null
  private var paymentGatewayFragment : PaymentGatewayFragment? = null
  private var scanPanCardFragment: ScanPanCardFragment? = null
  private var kycDetailsFragment: KYCDetailsFragment? = null
  private var paymentDocsSubmittedFragment: PaymentDocsSubmittedFragment? = null
  private var paymentDocsUnderVerificationFragment: PaymentDocsUnderVerificationFragment? = null
  private var paymentDocsVerificationFailedFragment: PaymentDocsVerificationFailedFragment? =  null
  private var paymentDocsVerificationSuccessfulFragment: PaymentDocsVerificationSuccessfulFragment? = null
  private var kycStatusFragment: KYCStatusFragment? = null


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

  override fun customTheme(): Int? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY,  FragmentType.KYC_DETAILS,FragmentType.PAYMENT_DOCS_SUBMITTED,
      FragmentType.PAYMENT_DOCS_UNDER_VERIFICATION, FragmentType.PAYMENT_DOCS_VERIFICATION_FAILED,
      FragmentType.PAYMENT_DOCS_VERIFICATION_SUCCESS, FragmentType.KYC_STATUS-> R.style.AppTheme_payment_gateway
      FragmentType.SCAN_PAN_CARD -> R.style.AppTheme_account
      else -> super.customTheme()
    }
  }

  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY, FragmentType.KYC_DETAILS,
      FragmentType.PAYMENT_DOCS_SUBMITTED, FragmentType.PAYMENT_DOCS_UNDER_VERIFICATION,
      FragmentType.PAYMENT_DOCS_VERIFICATION_FAILED, FragmentType.PAYMENT_DOCS_VERIFICATION_SUCCESS,
      FragmentType.KYC_STATUS-> ContextCompat.getColor(this, R.color.orange)
      FragmentType.SCAN_PAN_CARD -> ContextCompat.getColor(this, R.color.toolbar_grey)
//      FragmentType.KYC_DETAILS -> ContextCompat.getColor(this, R.color.orange)
//      FragmentType.PAYMENT_DOCS_SUBMITTED -> ContextCompat.getColor(this, R.color.orange)
//      FragmentType.PAYMENT_DOCS_UNDER_VERIFICATION -> ContextCompat.getColor(this, R.color.orange)
//      FragmentType.PAYMENT_DOCS_VERIFICATION_FAILED -> ContextCompat.getColor(this, R.color.orange)
//      FragmentType.PAYMENT_DOCS_VERIFICATION_SUCCESS -> ContextCompat.getColor(this, R.color.orange)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY, FragmentType.SCAN_PAN_CARD, FragmentType.KYC_DETAILS,
      FragmentType.PAYMENT_DOCS_SUBMITTED, FragmentType.PAYMENT_DOCS_UNDER_VERIFICATION, FragmentType.KYC_STATUS,
      FragmentType.PAYMENT_DOCS_VERIFICATION_FAILED, FragmentType.PAYMENT_DOCS_VERIFICATION_SUCCESS-> ContextCompat.getColor(this, R.color.white)
//      FragmentType.SCAN_PAN_CARD -> ContextCompat.getColor(this, R.color.white)
//      FragmentType.KYC_DETAILS -> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY, FragmentType.KYC_DETAILS,
      FragmentType.PAYMENT_DOCS_SUBMITTED, FragmentType.PAYMENT_DOCS_UNDER_VERIFICATION, FragmentType.KYC_STATUS,
      FragmentType.PAYMENT_DOCS_VERIFICATION_FAILED, FragmentType.PAYMENT_DOCS_VERIFICATION_SUCCESS-> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_new)
      FragmentType.SCAN_PAN_CARD -> ContextCompat.getDrawable(this, R.drawable.ic_round_close_white)
//      FragmentType.KYC_DETAILS -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_new)
      else -> super.getNavigationIcon()
    }
  }

  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY -> getString(R.string.self_branded_payment_gateway)
      FragmentType.SCAN_PAN_CARD -> getString(R.string.take_photo_of_your_pan_card)
      FragmentType.PAYMENT_DOCS_SUBMITTED -> getString(R.string.custom_payment_gateway)
      FragmentType.PAYMENT_DOCS_UNDER_VERIFICATION, FragmentType.PAYMENT_DOCS_VERIFICATION_SUCCESS,
      FragmentType.PAYMENT_DOCS_VERIFICATION_FAILED -> getString(R.string.kyc_status_all_caps)
      FragmentType.KYC_STATUS, FragmentType.KYC_DETAILS -> getString(R.string.kyc_information)
      else -> super.getToolbarTitle()
    }
  }

  override fun isHideToolbar(): Boolean {
    return when (type) {
//      FragmentType.PAYMENT_GATEWAY -> false
      else -> super.isHideToolbar()
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
      FragmentType.PAYMENT_DOCS_SUBMITTED -> {
        paymentDocsSubmittedFragment = PaymentDocsSubmittedFragment()
        paymentDocsSubmittedFragment
      }
      FragmentType.PAYMENT_DOCS_UNDER_VERIFICATION -> {
        paymentDocsUnderVerificationFragment = PaymentDocsUnderVerificationFragment()
        paymentDocsUnderVerificationFragment
      }
      FragmentType.PAYMENT_DOCS_VERIFICATION_FAILED -> {
        paymentDocsVerificationFailedFragment = PaymentDocsVerificationFailedFragment()
        paymentDocsVerificationFailedFragment
      }
      FragmentType.PAYMENT_DOCS_VERIFICATION_SUCCESS -> {
        paymentDocsVerificationSuccessfulFragment = PaymentDocsVerificationSuccessfulFragment()
        paymentDocsVerificationSuccessfulFragment
      }
      FragmentType.KYC_STATUS ->{
        kycStatusFragment = KYCStatusFragment()
        kycStatusFragment
      }

      else -> throw IllegalFragmentTypeException()
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
