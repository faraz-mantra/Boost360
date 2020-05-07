package com.inventoryorder.ui.order

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.FragmentInventoryAllBookingsBinding
import com.inventoryorder.model.bookingdetails.AllBookingsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.ui.startFragmentActivity

class InventoryAllBookingsFragment : BaseOrderFragment<FragmentInventoryAllBookingsBinding>(), RecyclerItemClickListener {

    var allBookingsModel : AllBookingsModel? = null

    companion object {

        fun newInstance(bundle: Bundle? = null): InventoryAllBookingsFragment {
            val fragment = InventoryAllBookingsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()

        allBookingsModel = arguments?.getSerializable(IntentConstant.BOOKING_ITEM.name) as? AllBookingsModel
        setRecyclerViewAdapter()

    }

    private fun setRecyclerViewAdapter() {

        val list = ArrayList<AllBookingsModel>()
        list.add(AllBookingsModel())
        list.add(AllBookingsModel())
        list.add(AllBookingsModel())
        list.add(AllBookingsModel())

        binding?.recyclerViewBookingDetails?.post {
            val adapter = AppBaseRecyclerViewAdapter(baseActivity, list,this)
            binding?.recyclerViewBookingDetails?.adapter = adapter
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem = menu.findItem(R.id.menu_item_search)
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.ALL_BOOKING_ITEM_CLICKED.ordinal -> {
                val bookingItem = item as AllBookingsModel
                val bundle = Bundle()
                bundle.putSerializable(IntentConstant.BOOKING_ITEM.name, bookingItem)
                startFragmentActivity(FragmentType.BOOKING_DETAIL, bundle)
            }
        }

    }

}