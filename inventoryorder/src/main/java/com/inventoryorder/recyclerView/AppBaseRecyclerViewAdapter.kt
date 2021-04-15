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
    itemClickListener: RecyclerItemClickListener? = null,
) : BaseRecyclerViewAdapter<T>(activity, list, itemClickListener) {

  override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*> {
    val inflater = LayoutInflater.from(parent.context)
    val recyclerViewItemType = values().first { it.getLayout() == viewType }
    val binding = getViewDataBinding(inflater, recyclerViewItemType, parent)
    return when (recyclerViewItemType) {
      PAGINATION_LOADER -> PagingViewHolder(binding as PaginationOrderLoaderBinding)
      ORDERS_ITEM_TYPE -> OrderSummaryViewHolder(binding as ItemOrderTypeBinding)
      INVENTORY_ORDER_ITEM -> OrdersViewHolder(binding as ItemOrderBinding)
      ITEM_ORDER_DETAILS -> OrderItemDetailsViewHolder(binding as ItemOrderDetailsBinding)
      PRODUCT_ITEM -> ProductItemViewHolder(binding as ItemProductBinding)
      PRODUCT_ITEM_SELECTED -> ProductSelectedItemViewHolder(binding as ItemProductsAddedBinding)
      PRODUCT_BOTTOM_SHEET_OPTIONS -> CreateOrderBottomSheetItemViewHolder(binding as BottomSheetOrderOptionBinding)
      ITEM_DELIVERY_OPTIONS -> DeliveryViewHolder(binding as ItemBottomSheetPickUpDeliveryOptionBinding)
      APPOINTMENT_ITEM_TYPE -> AppointmentsViewHolder(binding as ItemAppointmentsOrderBinding)
      APPOINTMENT_SPA_ITEM_TYPE -> AppointmentSpaViewHolder(binding as ItemAppointmentsSpaBinding)
      APPOINTMENT_DETAILS -> AppointmentDetailsViewHolder(binding as ItemBookingDetailsBinding)
      APPOINTMENT_SPA_DETAILS -> AppointmentSpaDetailsViewHolder(binding as ItemAppointmentSpaDetailsBinding)
      ITEM_SERVICE_LOCATIONS -> LocationsViewHolder(binding as ItemBottomSheetServiceLocationsBinding)
      ITEM_CHOOSE_PURPOSE -> ChoosePurposeViewHolder(binding as ItemBottomSheetChoosePurposeBinding)
      VIDEO_CONSULT_ITEM_TYPE -> VideoConsultsViewHolder(binding as ItemVideoConsultOrderBinding)
      VIDEO_CONSULT_DETAILS -> VideoConsultDetailsViewHolder(binding as ItemVideoConsultDetailsBinding)
      DATE_VIEW_TYPE -> DateViewHolder(binding as ItemDateViewBinding)
      APPOINTMENT_SCHEDULE -> AppointmentScheduleViewHolder(binding as ItemAppointmentScheduleBinding)
      GENDER_SELECTION -> GenderSelectionViewHolder(binding as ItemBottomSheetSelectGenderBinding)
      APPOINTMENT_TYPE -> AppointmentTypeViewHolder(binding as ItemBottomSheetAppointmentTypeBinding)
      PICK_INVENTORY_NATURE -> PickInventoryNatureViewHolder(binding as ItemBottomSheetPickInventoryNatureBinding)
      FILTER_ORDER_ITEM -> FilterOrderViewHolder(binding as ItemBottomSheetFilterBinding)
      TIME_SLOT_ITEM -> TimeSlotViewHolder(binding as ItemBottomTimeSlotBinding)
      WEEK_TIMING_SELECTED -> WeekTimeViewHolder(binding as ItemWeekTimeSelectBinding)
      SERVICES_DEPARTMENT -> ServicesViewHolder(binding as ItemConsultationServicesBinding)
      ORDER_MENU_ITEM -> OrderMenuHolder(binding as ItemOrderMenuBinding)
      STAFF_ITEM -> StaffItemViewHolder(binding as StaffItemBinding)
      SLOTS_ITEM -> SlotsItemViewHolder(binding as ItemTimeSlotAppointmentBinding)
      ITEM_FAQ -> FaqViewHolder(binding as ItemFaqBinding)
      ITEM_VIDEO -> VideoListViewHolder(binding as RecyclerItemListVideosBinding)
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

  open fun setRefreshItem(position: Int, t: T) {
    list[position] = t
    notifyItemChanged(position)
  }

  open fun getItem(position: Int): T? {
    return list[position]
  }

  open fun list(): ArrayList<T> {
    return list
  }

}