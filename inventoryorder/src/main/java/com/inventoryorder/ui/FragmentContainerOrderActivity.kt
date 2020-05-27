package com.inventoryorder.ui

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.framework.models.BaseViewModel
import com.framework.views.customViews.CustomToolbar
import com.inventoryorder.R
import com.inventoryorder.base.AppBaseActivity
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.ui.booking.BookingDetailsFragment
import com.inventoryorder.ui.booking.BookingsFragment
import com.inventoryorder.ui.createappointment.BookingSuccessfulFragment
import com.inventoryorder.ui.createappointment.NewBookingFragmentOne
import com.inventoryorder.ui.createappointment.NewBookingFragmentTwo
import com.inventoryorder.ui.order.OrderDetailFragment
import com.inventoryorder.ui.order.OrdersFragment
import com.inventoryorder.ui.order.TestBottomSheetFragment

open class FragmentContainerOrderActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null
  private var ordersFragment: OrdersFragment? = null
  private var orderDetailFragment: OrderDetailFragment? = null
  private var bookingDetails: BookingDetailsFragment? = null
  private var bookingsFragment: BookingsFragment? = null
  private var newBookingFragmentOne: NewBookingFragmentOne? = null
  private var newBookingFragmentTwo: NewBookingFragmentTwo? = null
  private var bookingSuccessfulFragment: BookingSuccessfulFragment? = null
  private var testBottomSheetFragment : TestBottomSheetFragment? = null

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
      FragmentType.CREATE_NEW_BOOKING,
      FragmentType.CREATE_NEW_BOOKING_PAGE_2 -> R.style.AppTheme_Order_create
      FragmentType.TEST_BOTTOM_SHEET_FRAGMENT -> null
      else -> super.customTheme()
    }
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }


  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.ALL_ORDER_VIEW,
      FragmentType.ORDER_DETAIL_VIEW,
      FragmentType.BOOKING_DETAIL_VIEW,
      FragmentType.ALL_BOOKING_VIEW,
      FragmentType.CREATE_NEW_BOOKING,
      FragmentType.CREATE_NEW_BOOKING_PAGE_2 -> ContextCompat.getColor(this, R.color.colorPrimary)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.ALL_ORDER_VIEW,
      FragmentType.ORDER_DETAIL_VIEW,
      FragmentType.BOOKING_DETAIL_VIEW,
      FragmentType.ALL_BOOKING_VIEW,
      FragmentType.CREATE_NEW_BOOKING,
      FragmentType.CREATE_NEW_BOOKING_PAGE_2 -> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun isHideToolbar(): Boolean {
    return when (type) {
      FragmentType.BOOKING_SUCCESSFUL -> true
      FragmentType.TEST_BOTTOM_SHEET_FRAGMENT -> true
      else -> super.isHideToolbar()
    }
  }

  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.ALL_ORDER_VIEW -> resources.getString(R.string.orders)
      FragmentType.ORDER_DETAIL_VIEW -> "# XXXXXXX"
      FragmentType.ALL_BOOKING_VIEW -> resources.getString(R.string.bookings)
      FragmentType.BOOKING_DETAIL_VIEW -> "# XXXXXXX"
      FragmentType.CREATE_NEW_BOOKING -> "New Booking"
      FragmentType.CREATE_NEW_BOOKING_PAGE_2 -> "New Booking"
      else -> super.getToolbarTitle()
    }
  }


  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.ALL_ORDER_VIEW,
      FragmentType.ORDER_DETAIL_VIEW,
      FragmentType.BOOKING_DETAIL_VIEW,
      FragmentType.ALL_BOOKING_VIEW,
      FragmentType.CREATE_NEW_BOOKING,
      FragmentType.CREATE_NEW_BOOKING_PAGE_2 -> ContextCompat.getDrawable(this, R.drawable.ic_arrow_left)
      else -> super.getNavigationIcon()
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
      FragmentType.ALL_ORDER_VIEW -> {
        ordersFragment = OrdersFragment.newInstance()
        ordersFragment

      }
      FragmentType.ORDER_DETAIL_VIEW -> {
        orderDetailFragment = OrderDetailFragment.newInstance()
        orderDetailFragment
      }
      FragmentType.ALL_BOOKING_VIEW -> {
        bookingsFragment = BookingsFragment.newInstance()
        bookingsFragment
      }
      FragmentType.BOOKING_DETAIL_VIEW -> {
        bookingDetails = BookingDetailsFragment.newInstance()
        bookingDetails
      }
      FragmentType.CREATE_NEW_BOOKING -> {
        newBookingFragmentOne = NewBookingFragmentOne.newInstance()
        newBookingFragmentOne
      }
      FragmentType.CREATE_NEW_BOOKING_PAGE_2 -> {
        newBookingFragmentTwo = NewBookingFragmentTwo.newInstance()
        newBookingFragmentTwo
      }
      FragmentType.BOOKING_SUCCESSFUL -> {
        bookingSuccessfulFragment = BookingSuccessfulFragment.newInstance()
        bookingSuccessfulFragment
      }
      FragmentType.TEST_BOTTOM_SHEET_FRAGMENT ->{
        testBottomSheetFragment = TestBottomSheetFragment()
        testBottomSheetFragment
      }
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    ordersFragment?.onActivityResult(requestCode, resultCode, data)
    bookingsFragment?.onActivityResult(requestCode, resultCode, data)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      android.R.id.home -> {
        onBackPressed()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onBackPressed() {
    val bundle = bookingDetails?.getBundleData() ?: orderDetailFragment?.getBundleData()
    bundle?.let {
      val intent = Intent()
      intent.putExtra(IntentConstant.RESULT_DATA.name, it)
      setResult(RESULT_OK, intent)
    }
    super.onBackPressed()
  }
}

fun Fragment.startFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false, isResult: Boolean = false) {
  val intent = Intent(activity, FragmentContainerOrderActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun startFragmentActivityNew(activity: Activity, type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean) {
  val intent = Intent(activity, FragmentContainerOrderActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  activity.startActivity(intent)
}

fun AppCompatActivity.startFragmentActivity(type: FragmentType, bundle: Bundle = Bundle(), clearTop: Boolean = false) {
  val intent = Intent(this, FragmentContainerOrderActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}
