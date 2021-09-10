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
import com.inventoryorder.ui.appointment.AppointmentDetailsFragment
import com.inventoryorder.ui.appointment.AppointmentsFragment
import com.inventoryorder.ui.appointment.createAptConsult.CreateAppointmentFragment
import com.inventoryorder.ui.appointmentSpa.create.ReviewAndConfirmFragment
import com.inventoryorder.ui.appointmentSpa.create.SpaAppointmentFragment
import com.inventoryorder.ui.appointmentSpa.list.AppointmentSpaDetailsFragment
import com.inventoryorder.ui.appointmentSpa.list.AppointmentSpaFragment
import com.inventoryorder.ui.consultation.VideoConsultDetailsFragment
import com.inventoryorder.ui.consultation.VideoConsultFragment
import com.inventoryorder.ui.createAptOld.BookingSuccessfulFragment
import com.inventoryorder.ui.createAptOld.NewBookingFragmentOne
import com.inventoryorder.ui.createAptOld.NewBookingFragmentTwo
import com.inventoryorder.ui.order.OrderDetailFragment
import com.inventoryorder.ui.order.OrderInvoiceFragment
import com.inventoryorder.ui.order.OrdersFragment
import com.inventoryorder.ui.order.createorder.*

open class FragmentContainerOrderActivity : AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null
  private var ordersFragment: OrdersFragment? = null
  private var orderDetailFragment: OrderDetailFragment? = null
  private var createOrderOnBoardingFragment: CreateOrderOnBoardingFragment? = null
  private var addCustomerFragment: AddCustomerFragment? = null
  private var addProductFragment: AddProductFragment? = null
  private var billingDetailFragment: BillingDetailFragment? = null
  private var appointmentDetails: AppointmentDetailsFragment? = null
  private var appointmentsFragment: AppointmentsFragment? = null
  private var appointmentSpaFragment: AppointmentSpaFragment? = null
  private var appointmentSpaDetailsFragment: AppointmentSpaDetailsFragment? = null
  private var createAppointmentFragment: CreateAppointmentFragment? = null
  private var newBookingFragmentOne: NewBookingFragmentOne? = null
  private var newBookingFragmentTwo: NewBookingFragmentTwo? = null
  private var bookingSuccessfulFragment: BookingSuccessfulFragment? = null
  private var videoConsultFragment: VideoConsultFragment? = null
  private var videoConsultDetailsFragment: VideoConsultDetailsFragment? = null
  private var orderInvoiceFragment: OrderInvoiceFragment? = null
  private var orderPlacedFragment: OrderPlacedFragment? = null
  private var spaAppointmentFragment: SpaAppointmentFragment? = null
  private var reviewAndConfirmFragment: ReviewAndConfirmFragment? = null

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
      FragmentType.REVIEW_SPA_DETAILS -> R.style.AppThemeReviewAndConfirm

      FragmentType.CREATE_NEW_ORDER,
      FragmentType.APPOINTMENT_DETAIL_VIEW,
      FragmentType.VIDEO_CONSULT_DETAIL_VIEW,
      FragmentType.ORDER_DETAIL_VIEW,
      -> R.style.AppTheme_Order_create_appointment

      FragmentType.CREATE_NEW_BOOKING,
      FragmentType.CREATE_NEW_BOOKING_PAGE_2,
      -> R.style.AppTheme_Order_create

      FragmentType.CREATE_APPOINTMENT_VIEW,
      FragmentType.CREATE_SPA_APPOINTMENT,
      FragmentType.ADD_PRODUCT,
      FragmentType.ADD_CUSTOMER,
      FragmentType.BILLING_DETAIL
      -> R.style.Add_Order_create_appointment

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
      FragmentType.APPOINTMENT_DETAIL_VIEW,
      FragmentType.ALL_APPOINTMENT_VIEW,
      FragmentType.CREATE_NEW_BOOKING,
      FragmentType.CREATE_NEW_BOOKING_PAGE_2,
      FragmentType.ALL_VIDEO_CONSULT_VIEW,
      FragmentType.VIDEO_CONSULT_DETAIL_VIEW,
      FragmentType.ADD_CUSTOMER,
      FragmentType.ADD_PRODUCT,
      FragmentType.BILLING_DETAIL,
      FragmentType.CREATE_APPOINTMENT_VIEW,
      FragmentType.ORDER_INVOICE_VIEW,
      FragmentType.ORDER_PLACED,
      FragmentType.CREATE_SPA_APPOINTMENT,
      FragmentType.REVIEW_SPA_DETAILS,
      FragmentType.ALL_APPOINTMENT_SPA_VIEW,
      FragmentType.APPOINTMENT_SPA_DETAIL_VIEW,
      -> ContextCompat.getColor(this, R.color.colorPrimary)
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.ALL_ORDER_VIEW,
      FragmentType.ORDER_DETAIL_VIEW,
      FragmentType.APPOINTMENT_DETAIL_VIEW,
      FragmentType.ALL_APPOINTMENT_VIEW,
      FragmentType.CREATE_NEW_BOOKING,
      FragmentType.CREATE_NEW_BOOKING_PAGE_2,
      FragmentType.ALL_VIDEO_CONSULT_VIEW,
      FragmentType.VIDEO_CONSULT_DETAIL_VIEW,
      FragmentType.ADD_CUSTOMER,
      FragmentType.ADD_PRODUCT,
      FragmentType.BILLING_DETAIL,
      FragmentType.CREATE_APPOINTMENT_VIEW,
      FragmentType.ORDER_INVOICE_VIEW,
      FragmentType.ORDER_PLACED,
      FragmentType.CREATE_SPA_APPOINTMENT,
      FragmentType.REVIEW_SPA_DETAILS,
      FragmentType.ALL_APPOINTMENT_SPA_VIEW,
      FragmentType.APPOINTMENT_SPA_DETAIL_VIEW,
      -> ContextCompat.getColor(this, R.color.white)
      else -> super.getToolbarTitleColor()
    }
  }

  override fun isHideToolbar(): Boolean {
    return when (type) {
      FragmentType.CREATE_NEW_ORDER,
      FragmentType.BOOKING_SUCCESSFUL,
      FragmentType.ORDER_PLACED,
      -> true
      else -> super.isHideToolbar()
    }
  }

  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.ALL_ORDER_VIEW -> resources.getString(R.string.orders)
      FragmentType.ALL_APPOINTMENT_SPA_VIEW,
      FragmentType.ALL_APPOINTMENT_VIEW,
      -> resources.getString(R.string.appointments)
      FragmentType.ALL_VIDEO_CONSULT_VIEW -> resources.getString(R.string.video_consultation)
      FragmentType.APPOINTMENT_SPA_DETAIL_VIEW,
      FragmentType.APPOINTMENT_DETAIL_VIEW,
      -> getString(R.string.appointment_details_n)
      FragmentType.ORDER_DETAIL_VIEW,
      FragmentType.VIDEO_CONSULT_DETAIL_VIEW,
      -> getString(R.string.hash_xxxxxxxx)
      FragmentType.CREATE_NEW_BOOKING -> resources.getString(R.string.new_booking_n)
      FragmentType.CREATE_NEW_BOOKING_PAGE_2 -> resources.getString(R.string.new_booking_n)
      FragmentType.CREATE_APPOINTMENT_VIEW -> getString(R.string.new_appointment_n)
      FragmentType.ADD_CUSTOMER -> getString(R.string.add_a_customer_n)
      FragmentType.ADD_PRODUCT -> getString(R.string.add_product)
      FragmentType.BILLING_DETAIL -> getString(R.string.review_confirm)
      FragmentType.ORDER_INVOICE_VIEW -> getString(R.string.invoice_preview)
      FragmentType.CREATE_SPA_APPOINTMENT -> getString(R.string.adding_appointment)
      FragmentType.REVIEW_SPA_DETAILS -> getString(R.string.string_review_and_confirm)
      else -> super.getToolbarTitle()
    }
  }

  override fun getSubtitleAlpha(): Float? {
    return null
  }

  override fun getToolbarSubTitle(): String? {
    return when (type) {
      FragmentType.ADD_PRODUCT -> "Step 1/3"
      FragmentType.ADD_CUSTOMER -> "Step 2/3"
      FragmentType.BILLING_DETAIL -> "Step 3/3"
      else -> super.getToolbarSubTitle()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.ALL_ORDER_VIEW,
      FragmentType.ORDER_DETAIL_VIEW,
      FragmentType.APPOINTMENT_DETAIL_VIEW,
      FragmentType.ALL_APPOINTMENT_VIEW,
      FragmentType.CREATE_NEW_BOOKING,
      FragmentType.CREATE_NEW_BOOKING_PAGE_2,
      FragmentType.ALL_VIDEO_CONSULT_VIEW,
      FragmentType.VIDEO_CONSULT_DETAIL_VIEW,
      FragmentType.ADD_CUSTOMER,
      FragmentType.ADD_PRODUCT,
      FragmentType.BILLING_DETAIL,
      FragmentType.CREATE_APPOINTMENT_VIEW,
      FragmentType.ORDER_INVOICE_VIEW,
      FragmentType.CREATE_SPA_APPOINTMENT,
      FragmentType.REVIEW_SPA_DETAILS,
      FragmentType.ALL_APPOINTMENT_SPA_VIEW,
      FragmentType.APPOINTMENT_SPA_DETAIL_VIEW,
      -> ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_left)
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
      FragmentType.CREATE_NEW_ORDER -> {
        createOrderOnBoardingFragment = CreateOrderOnBoardingFragment.newInstance()
        createOrderOnBoardingFragment
      }
      FragmentType.ADD_CUSTOMER -> {
        addCustomerFragment = AddCustomerFragment.newInstance()
        addCustomerFragment
      }
      FragmentType.ADD_PRODUCT -> {
        addProductFragment = AddProductFragment.newInstance()
        addProductFragment
      }
      FragmentType.BILLING_DETAIL -> {
        billingDetailFragment = BillingDetailFragment.newInstance()
        billingDetailFragment
      }
      FragmentType.ALL_APPOINTMENT_VIEW -> {
        appointmentsFragment = AppointmentsFragment.newInstance()
        appointmentsFragment
      }
      FragmentType.ALL_APPOINTMENT_SPA_VIEW -> {
        appointmentSpaFragment = AppointmentSpaFragment.newInstance()
        appointmentSpaFragment
      }

      FragmentType.APPOINTMENT_SPA_DETAIL_VIEW -> {
        appointmentSpaDetailsFragment = AppointmentSpaDetailsFragment.newInstance()
        appointmentSpaDetailsFragment
      }
      FragmentType.APPOINTMENT_DETAIL_VIEW -> {
        appointmentDetails = AppointmentDetailsFragment.newInstance()
        appointmentDetails
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
      FragmentType.ALL_VIDEO_CONSULT_VIEW -> {
        videoConsultFragment = VideoConsultFragment.newInstance()
        videoConsultFragment
      }
      FragmentType.VIDEO_CONSULT_DETAIL_VIEW -> {
        videoConsultDetailsFragment = VideoConsultDetailsFragment.newInstance()
        videoConsultDetailsFragment
      }
      FragmentType.CREATE_APPOINTMENT_VIEW -> {
        createAppointmentFragment = CreateAppointmentFragment.newInstance()
        createAppointmentFragment
      }
      FragmentType.ORDER_INVOICE_VIEW -> {
        orderInvoiceFragment = OrderInvoiceFragment.newInstance()
        orderInvoiceFragment
      }
      FragmentType.ORDER_PLACED -> {
        orderPlacedFragment = OrderPlacedFragment.newInstance()
        orderPlacedFragment
      }
      FragmentType.CREATE_SPA_APPOINTMENT -> {
        spaAppointmentFragment = SpaAppointmentFragment()
        spaAppointmentFragment
      }
      FragmentType.REVIEW_SPA_DETAILS -> {
        reviewAndConfirmFragment = ReviewAndConfirmFragment()
        reviewAndConfirmFragment
      }
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    ordersFragment?.onActivityResult(requestCode, resultCode, data)
    appointmentsFragment?.onActivityResult(requestCode, resultCode, data)
    videoConsultFragment?.onActivityResult(requestCode, resultCode, data)
    createAppointmentFragment?.onActivityResult(requestCode, resultCode, data)
    videoConsultDetailsFragment?.onActivityResult(requestCode, resultCode, data)
    orderInvoiceFragment?.onActivityResult(requestCode, resultCode, data)
    addCustomerFragment?.onActivityResult(requestCode, resultCode, data)
    billingDetailFragment?.onActivityResult(requestCode, resultCode, data)
    reviewAndConfirmFragment?.onActivityResult(requestCode, resultCode, data)
    spaAppointmentFragment?.onActivityResult(requestCode, resultCode, data)
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
    val bundle = appointmentDetails?.getBundleData() ?: orderDetailFragment?.getBundleData()
    ?: videoConsultDetailsFragment?.getBundleData()
    ?: bookingSuccessfulFragment?.getBundleData() ?: billingDetailFragment?.getBundleData()
    ?: addCustomerFragment?.getBundleData()
    ?: orderPlacedFragment?.getBundleData() ?: reviewAndConfirmFragment?.getBundleData()
    ?: spaAppointmentFragment?.getBundleData()
    ?: appointmentSpaDetailsFragment?.getBundleData() ?: addProductFragment?.getBundleData()
    bundle?.let {
      val intent = Intent()
      intent.putExtra(IntentConstant.RESULT_DATA.name, it)
      setResult(RESULT_OK, intent)
    }
    super.onBackPressed()
  }
}

fun Fragment.startFragmentOrderActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false,
  isResult: Boolean = false
) {
  val intent = Intent(activity, FragmentContainerOrderActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun startFragmentActivityNew(
  activity: Activity,
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean
) {
  val intent = Intent(activity, FragmentContainerOrderActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  activity.startActivity(intent)
}

fun AppCompatActivity.startFragmentOrderActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false,
  isResult: Boolean = false
) {
  val intent = Intent(this, FragmentContainerOrderActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}
