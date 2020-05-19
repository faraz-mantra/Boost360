package com.inventoryorder.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.framework.base.BaseActivity
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType.*
import com.inventoryorder.databinding.*
import com.inventoryorder.holders.*


open class AppBaseRecyclerViewAdapter<T : AppBaseRecyclerViewItem>(
    activity: BaseActivity<*, *>,
    list: ArrayList<T>,
    itemClickListener: RecyclerItemClickListener? = null
) : BaseRecyclerViewAdapter<T>(activity, list, itemClickListener) {

  override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*> {
    val inflater = LayoutInflater.from(parent.context)
    val recyclerViewItemType = values().first { it.getLayout() == viewType }
    val binding = getViewDataBinding(inflater, recyclerViewItemType, parent)
    return when (recyclerViewItemType) {
      PAGINATION_LOADER -> PagingViewHolder(binding as PaginationLoaderBinding)
      ORDERS_ITEM_TYPE -> OrderSummaryViewHolder(binding as ItemOrderTypeBinding)
      INVENTORY_ORDER_ITEM -> OrdersViewHolder(binding as ItemOrderBinding)
      ITEM_ORDER_DETAILS -> OrderItemDetailsViewHolder(binding as ItemOrderDetailsBinding)
      ITEM_DELIVERY_OPTIONS -> DeliveryViewHolder(binding as ItemBottomSheetPickUpDeliveryOptionBinding)
      BOOKING_DETAILS -> BookingDetailsViewHolder(binding as ItemBookingDetailsBinding)
      ITEM_SERVICE_LOCATIONS -> LocationsViewHolder(binding as ItemBottomSheetServiceLocationsBinding)
      BOOKINGS_ITEM_TYPE -> BookingsViewHolder(binding as ItemBookingsAllOrderBinding)
      ITEM_CHOOSE_PURPOSE -> ChoosePurposeViewHolder(binding as ItemBottomSheetChoosePurposeBinding)
      BOOKINGS_DATE_TYPE -> BookingDateViewHolder(binding as ItemBookingsDateBinding)
      APPOINTMENT_SCHEDULE -> AppointmentScheduleViewHolder (binding as ItemAppointmentScheduleBinding)
      GENDER_SELECTION -> GenderSelectionViewHolder (binding as ItemBottomSheetSelectGenderBinding)
      APPOINTMENT_TYPE -> AppointmentTypeViewHolder (binding as ItemBottomSheetAppointmentTypeBinding)
    }
  }

  fun runLayoutAnimation(recyclerView: RecyclerView?, anim: Int = R.anim.layout_animation_fall_down) = recyclerView?.apply {
    layoutAnimation = AnimationUtils.loadLayoutAnimation(context, anim)
    notifyDataSetChanged()
    scheduleLayoutAnimation()
  }

  override fun getItemViewType(position: Int): Int {
    return if (isLoaderVisible) {
      return if (position == list.size - 1) PAGINATION_LOADER.getLayout() else super.getItemViewType(position)
    } else super.getItemViewType(position)
  }

  fun notify(list: ArrayList<T>?) {
    list?.let { updateList(it) }
  }

  open fun addItems(addList: ArrayList<T>?) {
    addList?.let { list.addAll(it) }
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int {
    return if (list.isNotEmpty()) list.size else 0
  }


  open fun remove(item: T) {
    val position = list.indexOf(item)
    if (position > -1) {
      list.removeAt(position)
      notifyItemRemoved(position)
    }
  }

  open fun clear() {
    isLoaderVisible = false
    while (itemCount > 0) {
      getItem(0)?.let { remove(it) }
    }
  }

  open fun isEmpty(): Boolean {
    return itemCount == 0
  }

  open fun addLoadingFooter(t: T) {
    isLoaderVisible = true
    list.add(t)
    notifyItemInserted(list.size - 1)
  }

  open fun removeLoadingFooter() {
    isLoaderVisible = false
    val position = list.size - 1
    if (position > -1) {
      val item: T? = getItem(position)
      if (item != null) {
        list.removeAt(position)
        notifyItemRemoved(position)
      }
    }
  }

  open fun getItem(position: Int): T? {
    return list[position]
  }

  open fun list(): ArrayList<T> {
    return list
  }

}