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

  private var paymentGatewayFragment: PaymentGatewayFragment? = null


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
      FragmentType.PAYMENT_GATEWAY -> R.style.AppTheme_add_account
      else -> super.customTheme()
    }
  }

  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY -> ContextCompat.getColor(this, R.color.color_primary)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY -> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_new)
      else -> super.getNavigationIcon()
    }
  }

  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.PAYMENT_GATEWAY -> "Account"
      else -> super.getToolbarTitle()
    }
  }

  override fun isHideToolbar(): Boolean {
    return when (type) {
//      FragmentType.PAYMENT_GATEWAY -> true
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
