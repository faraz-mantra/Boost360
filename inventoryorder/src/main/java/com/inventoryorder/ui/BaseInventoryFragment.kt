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
import com.inventoryorder.ui.consultation.VideoConsultDetailsFragment
import com.inventoryorder.ui.consultation.VideoConsultFragment
import com.inventoryorder.ui.createappointment.BookingSuccessfulFragment
import com.inventoryorder.ui.createappointment.NewBookingFragmentOne
import com.inventoryorder.ui.createappointment.NewBookingFragmentTwo
import com.inventoryorder.ui.order.OrderDetailFragment
import com.inventoryorder.ui.order.OrdersFragment
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
      is VideoConsultFragment -> R.layout.fragment_video_consult
      is VideoConsultDetailsFragment -> R.layout.fragment_video_consult_details
      is NewBookingFragmentOne -> R.layout.fragment_new_booking_one
      is NewBookingFragmentTwo -> R.layout.fragment_new_booking_two
      is BookingSuccessfulFragment -> R.layout.fragment_booking_successful
      else -> throw IllegalFragmentTypeException()
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    preferenceData = arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name) as? PreferenceData
  }

  override fun getViewModelClass(): Class<OrderCreateViewModel> {
    return OrderCreateViewModel::class.java
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    when (this) {
      is OrdersFragment -> inflater.inflate(R.menu.menu_search_icon, menu)
      is OrderDetailFragment -> inflater.inflate(R.menu.menu_share_button, menu)
      is AppointmentsFragment -> inflater.inflate(R.menu.menu_search_icon, menu)
      is AppointmentDetailsFragment -> inflater.inflate(R.menu.menu_share_button, menu)
      is VideoConsultFragment -> inflater.inflate(R.menu.menu_search_icon, menu)
      is VideoConsultDetailsFragment -> inflater.inflate(R.menu.menu_share_button, menu)
      is NewBookingFragmentOne -> inflater.inflate(R.menu.menu_toolbar, menu)
      is NewBookingFragmentTwo -> inflater.inflate(R.menu.menu_toolbar, menu)
    }
  }
}