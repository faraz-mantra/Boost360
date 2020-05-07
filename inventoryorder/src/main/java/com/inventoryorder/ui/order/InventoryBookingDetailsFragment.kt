package com.inventoryorder.ui.order

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.framework.views.customViews.CustomButton
import com.inventoryorder.R
import com.inventoryorder.databinding.FragmentInventoryBookingDetailsBinding
import com.inventoryorder.model.bookingdetails.BookingDetailsModel
import com.inventoryorder.model.bottomsheet.ServiceLocationsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter

class InventoryBookingDetailsFragment : BaseOrderFragment<FragmentInventoryBookingDetailsBinding>() {

    private var serviceLocationsBottomSheetDialog : ServiceLocationBottomSheetDialog? = null
    private var serviceLocationsList = ServiceLocationsModel().getData()

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): InventoryBookingDetailsFragment {
            val fragment = InventoryBookingDetailsFragment()
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
            binding?.buttonConfirmOrder ->{ showShortToast("Coming Soon")}
            binding?.tvCancelOrder ->{ showShortToast("Coming Soon")}
        }
    }
    private fun setBookingDetailsAdapter(bookingDetailsModel: ArrayList<BookingDetailsModel>){

        binding?.recyclerViewBookingDetails?.post {
            val adapter = AppBaseRecyclerViewAdapter(baseActivity, bookingDetailsModel)
            binding?.recyclerViewBookingDetails?.adapter = adapter
        }
    }

    private fun showBottomSheetDialog() {
        serviceLocationsBottomSheetDialog = ServiceLocationBottomSheetDialog()
        serviceLocationsBottomSheetDialog?.onDoneClicked = { clickDeliveryItem(it) }
        serviceLocationsBottomSheetDialog?.setList(serviceLocationsList)
        serviceLocationsBottomSheetDialog?.show(this.parentFragmentManager, DeliveryBottomSheetDialog::class.java.name)
    }

    private fun clickDeliveryItem(serviceList: ServiceLocationsModel?) {
        serviceLocationsList.forEach { it.isSelected = (it.serviceOptionSelectedName == serviceList?.serviceOptionSelectedName) }
    }


}