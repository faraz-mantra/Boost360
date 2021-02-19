package com.inventoryorder.ui.appointmentspa

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.observeOnce
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentSpaAppointmentBinding
import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.model.spaAppointment.GetServiceListingResponse
import com.inventoryorder.model.spaAppointment.ServiceItem
import com.inventoryorder.model.spaAppointment.bookingslot.request.BookingSlotsRequest
import com.inventoryorder.model.spaAppointment.bookingslot.request.DateRange
import com.inventoryorder.model.spaAppointment.bookingslot.response.BookingSlotResponse
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.CustomArrayAdapter
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.appointmentspa.bottomsheet.SelectDateTimeBottomSheetDialog
import com.inventoryorder.ui.startFragmentOrderActivity

class SpaAppointmentFragment : BaseInventoryFragment<FragmentSpaAppointmentBinding>() {

    private var serviceList : ArrayList<ServiceItem> ?= null
    private var serviceAdapter : ArrayAdapter<ServiceItem> ?= null
    private var startDate = "2021-02-20"
    private var bookingSlotResponse : BookingSlotResponse ?= null
    private var selectedService : ServiceItem ?= null

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): SpaAppointmentFragment {
            val fragment = SpaAppointmentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        getSearchListing()
        serviceList = ArrayList()
        setOnClickListener(binding?.buttonReviewDetails, binding?.textAddApptDateTime)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v) {
            binding?.buttonReviewDetails -> {
                startFragmentOrderActivity(FragmentType.REVIEW_SPA_DETAILS, Bundle())
            }

            binding?.textAddApptDateTime -> {
                val selectedDateTimeBottomSheetDialog = SelectDateTimeBottomSheetDialog(bookingSlotResponse!!, selectedService!!)
                selectedDateTimeBottomSheetDialog.show(this.parentFragmentManager, SelectDateTimeBottomSheetDialog::class.java.name)
            }
        }
    }

    private fun getSearchListing() {
        showProgress(getString(R.string.loading))
        viewModel?.getSearchListing(preferenceData?.fpTag!!, "", "", 0, 10)?.observeOnce(viewLifecycleOwner, Observer {
            hideProgress()

            if (it.error is NoNetworkException) {
                showLongToast(resources.getString(R.string.internet_connection_not_available))
                return@Observer
            }

            if (it.isSuccess()) {
                var serviceListingResponse = (it as? GetServiceListingResponse)
                if (serviceListingResponse!= null) {
                    serviceList?.addAll(serviceListingResponse.Result?.Data!!)
                    setArrayAdapter()
                }
            } else {
                showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.not_able_to_get_services_list))
            }
        })
    }

    private fun getBookingSlots(bookingSlotsRequest: BookingSlotsRequest) {
        showProgress(getString(R.string.loading))
        viewModel?.getBookingSlots(bookingSlotsRequest)?.observeOnce(viewLifecycleOwner, Observer {
            hideProgress()

            if (it.error is NoNetworkException) {
                showLongToast(resources.getString(R.string.internet_connection_not_available))
                return@Observer
            }

            if (it.isSuccess()) {
                bookingSlotResponse = (it as? BookingSlotResponse)
                binding?.groupTiming?.visibility = View.VISIBLE
            } else {
                showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.not_able_to_get_booking_slots))
                binding?.groupTiming?.visibility = View.GONE
            }
        })
    }

    private fun setArrayAdapter() {
        serviceAdapter = CustomArrayAdapter(this.requireActivity(), R.layout.layout_service_item, serviceList!!)
        serviceAdapter?.setNotifyOnChange(true)
        binding?.editServiceName?.threshold = 1
        binding?.editServiceName?.setAdapter(serviceAdapter)
        binding?.editServiceName?.onItemClickListener = AdapterView.OnItemClickListener { p0, view, pos, id ->
            selectedService = serviceList?.get(pos)
            var bookingSlotsRequest = BookingSlotsRequest(BatchType = "DAILY",
                    ServiceId = serviceList?.get(pos)?._id!!,
                    DateRange = DateRange(StartDate = startDate, EndDate = startDate))
            getBookingSlots(bookingSlotsRequest)
        }
    }
}