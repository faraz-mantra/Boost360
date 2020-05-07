package com.inventoryorder.ui.booking

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.framework.views.customViews.CustomButton
import com.inventoryorder.R
import com.inventoryorder.databinding.FragmentInventoryBookingDetailsBinding
import com.inventoryorder.model.bookingdetails.BookingDetailsModel
import com.inventoryorder.model.bottomsheet.LocationsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.order.DeliveryBottomSheetDialog

class BookingDetailsFragment : BaseInventoryFragment<FragmentInventoryBookingDetailsBinding>() {

    private var locationsBottomSheetDialog: LocationBottomSheetDialog? = null
    private var serviceLocationsList = LocationsModel().getData()

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): BookingDetailsFragment {
            val fragment = BookingDetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val item: MenuItem = menu.findItem(R.id.menu_item_share)
        item.actionView.findViewById<CustomButton>(R.id.button_share).setOnClickListener {
            showLongToast("Coming soon..")
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setBookingDetailsAdapter(BookingDetailsModel().getOrderDetails())
        setOnClickListener(binding?.btnBusiness,binding?.buttonConfirmOrder,binding?.tvCancelOrder)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.btnBusiness ->{ showBottomSheetDialog()}
            binding?.buttonConfirmOrder -> {
                showShortToast("Coming Soon...")
            }
            binding?.tvCancelOrder -> {
                showShortToast("Coming Soon...")
            }
        }
    }
    private fun setBookingDetailsAdapter(bookingDetailsModel: ArrayList<BookingDetailsModel>){
        binding?.recyclerViewBookingDetails?.post {
            val adapter = AppBaseRecyclerViewAdapter(baseActivity, bookingDetailsModel)
            binding?.recyclerViewBookingDetails?.adapter = adapter
        }
    }

    private fun showBottomSheetDialog() {
        locationsBottomSheetDialog = LocationBottomSheetDialog()
        locationsBottomSheetDialog?.onDoneClicked = { clickDeliveryItem(it) }
        locationsBottomSheetDialog?.setList(serviceLocationsList)
        locationsBottomSheetDialog?.show(this.parentFragmentManager, DeliveryBottomSheetDialog::class.java.name)
    }

    private fun clickDeliveryItem(list: LocationsModel?) {
        serviceLocationsList.forEach { it.isSelected = (it.serviceOptionSelectedName == list?.serviceOptionSelectedName) }
    }
}