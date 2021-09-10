package com.inventoryorder.ui

import android.view.Menu
import android.view.MenuInflater
import androidx.databinding.ViewDataBinding
import com.framework.exceptions.IllegalFragmentTypeException
import com.inventoryorder.R
import com.inventoryorder.base.AppBaseFragment
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.model.PreferenceData
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
import com.inventoryorder.viewmodel.OrderCreateViewModel

open class BaseInventoryFragment<binding : ViewDataBinding> : AppBaseFragment<binding, OrderCreateViewModel>() {

  protected val fpTag: String?
    get() {
      return preferenceData?.fpTag
    }
  protected val clientId: String?
    get() {
      return preferenceData?.clientId
    }

  protected val auth: String
    get() {
      return preferenceData?.authorization ?: ""
    }

  protected var preferenceData: PreferenceData? = null

  override fun getLayout(): Int {
    return when (this) {
      is OrdersFragment -> R.layout.fragment_orders
      is OrderDetailFragment -> R.layout.fragment_order_detail
      is AppointmentsFragment -> R.layout.fragment_appointments
      is AppointmentDetailsFragment -> R.layout.fragment_appointment_details
      is AppointmentSpaFragment -> R.layout.fragment_appointments_spa
      is AppointmentSpaDetailsFragment -> R.layout.fragment_appointment_spa_details
      is VideoConsultFragment -> R.layout.fragment_video_consult
      is VideoConsultDetailsFragment -> R.layout.fragment_video_consult_details
      is NewBookingFragmentOne -> R.layout.fragment_new_booking_one
      is NewBookingFragmentTwo -> R.layout.fragment_new_booking_two
      is BookingSuccessfulFragment -> R.layout.fragment_booking_successful
      is CreateAppointmentFragment -> R.layout.fragment_new_appointment
      is CreateOrderOnBoardingFragment -> R.layout.fragment_order_on_boarding
      is AddCustomerFragment -> R.layout.fragment_add_customer
      is AddProductFragment -> R.layout.fragment_add_product
      is BillingDetailFragment -> R.layout.fragment_billing_detail
      is OrderInvoiceFragment -> R.layout.fragment_order_inoice
      is OrderPlacedFragment -> R.layout.fragment_order_placed
      is SpaAppointmentFragment -> R.layout.fragment_spa_appointment
      is ReviewAndConfirmFragment -> R.layout.fragment_review_and_confirm
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    preferenceData =
      arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name) as? PreferenceData
  }

  override fun getViewModelClass(): Class<OrderCreateViewModel> {
    return OrderCreateViewModel::class.java
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    when (this) {
      is OrdersFragment -> inflater.inflate(R.menu.menu_search_icon, menu)
      is OrderDetailFragment -> inflater.inflate(R.menu.menu_invoice, menu)
      is AppointmentsFragment -> inflater.inflate(R.menu.menu_search_filter_icon, menu)
      is AppointmentSpaFragment -> inflater.inflate(R.menu.menu_search_filter_icon, menu)
      is AppointmentSpaDetailsFragment -> inflater.inflate(R.menu.menu_invoice, menu)
      is AppointmentDetailsFragment -> inflater.inflate(R.menu.menu_invoice, menu)
      is VideoConsultFragment -> inflater.inflate(R.menu.menu_search_filter_icon, menu)
      is VideoConsultDetailsFragment -> inflater.inflate(R.menu.menu_share_button, menu)
      is NewBookingFragmentOne -> inflater.inflate(R.menu.menu_toolbar, menu)
      is NewBookingFragmentTwo -> inflater.inflate(R.menu.menu_toolbar, menu)
      is OrderInvoiceFragment -> inflater.inflate(R.menu.menu_order_invoice, menu)
    }
  }
}