package com.inventoryorder.holders

import android.graphics.Paint
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import com.framework.enums.setDrawable
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBookingsAllOrderBinding
import com.inventoryorder.model.bookingdetails.AllBookingsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class AllBookingsViewHolder (binding : ItemBookingsAllOrderBinding): AppBaseRecyclerViewHolder<ItemBookingsAllOrderBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)

        val data = item as AllBookingsModel



        binding.mainView.setOnClickListener {
            listener?.onItemClick(adapterPosition,data, RecyclerViewActionType.ALL_BOOKING_ITEM_CLICKED.ordinal)
        }

        if(adapterPosition == 2){

            activity?.let {
                binding.orderType.background = ContextCompat.getDrawable(it,R.drawable.cancel_order_bg)
            }
            binding.orderType.text = "New Booking"
            binding.orderId.setTextColor(getResources()!!.getColor(R.color.primary_grey))
            binding.tvOrderAmount.setTextColor(getResources()!!.getColor(R.color.primary_grey))
            binding.tvOrderDate.setTextColor(getResources()!!.getColor(R.color.primary_grey))
            binding.tvPaymentMode.setTextColor(getResources()!!.getColor(R.color.primary_grey))
            binding.tvCustomersLocation.setTextColor(getResources()!!.getColor(R.color.primary_grey))
            binding.itemDesc.setTextColor(getResources()!!.getColor(R.color.primary_grey))

            binding.tvTimeElapsedText.visibility = View.GONE
            binding.tvTimeElapsedTime.visibility = View.GONE

            binding.buttonConfirm2Grey.paintFlags = binding.buttonConfirm2Grey.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.buttonConfirm2Grey.visibility = View.VISIBLE
            binding.buttonConfirm1.visibility = View.GONE
//            binding.buttonConfirm2Grey.setBackgroundColor(getResources()!!.getColor(R.color.primary_grey))


        }
        if(adapterPosition == 3){

            binding.tvOrderAmount.setTextColor(getResources()!!.getColor(R.color.primary_grey))
            binding.orderId.setTextColor(getResources()!!.getColor(R.color.primary_grey))

            activity?.let {
                binding.orderType.background = ContextCompat.getDrawable(it,R.drawable.cancel_order_bg)
            }
            binding.orderType.text = "Cancelled"

//            Visibility
            binding.tvTimeElapsedText.visibility = View.GONE
            binding.tvTimeElapsedTime.visibility = View.GONE
            binding.tvOrderDate.visibility = View.GONE
            binding.tvPaymentMode.visibility = View.GONE
            binding.tvCustomersLocation.visibility = View.GONE
            binding.title.visibility = View.GONE
            binding.payment.visibility = View.GONE
            binding.location.visibility = View.GONE
            binding.itemDesc.visibility = View.GONE
            binding.itemCount.visibility = View.GONE
            binding.buttonConfirm1.visibility = View.GONE
            binding.next1.visibility = View.GONE
            binding.next2.visibility = View.VISIBLE
            

        }

        if(adapterPosition == 4){

            binding.tvOrderAmount.setTextColor(getResources()!!.getColor(R.color.primary_grey))
            binding.orderId.setTextColor(getResources()!!.getColor(R.color.primary_grey))

            activity?.let {
                binding.orderType.background = ContextCompat.getDrawable(it,R.drawable.cancel_order_bg)
            }
            binding.orderType.text = "Cancelled"

//            Visibility
            binding.tvTimeElapsedText.visibility = View.GONE
            binding.tvTimeElapsedTime.visibility = View.GONE
            binding.tvOrderDate.visibility = View.GONE
            binding.tvPaymentMode.visibility = View.GONE
            binding.tvCustomersLocation.visibility = View.GONE
            binding.title.visibility = View.GONE
            binding.payment.visibility = View.GONE
            binding.location.visibility = View.GONE
            binding.itemDesc.visibility = View.GONE
            binding.itemCount.visibility = View.GONE
            binding.buttonConfirm1.visibility = View.GONE
            binding.next1.visibility = View.GONE
            binding.next2.visibility = View.VISIBLE

        }

//        if(adapterPosition == 5){
//
//            binding.tvOrderAmount.setTextColor(getResources()!!.getColor(R.color.primary_grey))
//            binding.orderId.setTextColor(getResources()!!.getColor(R.color.primary_grey))
//
//            activity?.let {
//                binding.orderType.background = ContextCompat.getDrawable(it,R.drawable.cancel_order_bg)
//            }
//            binding.orderType.text = "Cancelled"
//
////            Visibility
//            binding.tvTimeElapsedText.visibility = View.GONE
//            binding.tvTimeElapsedTime.visibility = View.GONE
//            binding.tvOrderDate.visibility = View.GONE
//            binding.tvPaymentMode.visibility = View.GONE
//            binding.tvCustomersLocation.visibility = View.GONE
//            binding.title.visibility = View.GONE
//            binding.payment.visibility = View.GONE
//            binding.location.visibility = View.GONE
//            binding.itemDesc.visibility = View.GONE
//            binding.itemCount.visibility = View.GONE
//            binding.buttonConfirm.visibility = View.GONE
//            binding.next1.visibility = View.GONE
//            binding.next2.visibility = View.VISIBLE
//
//        }

    }


}