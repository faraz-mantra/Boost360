package com.inventoryorder.ui.createappointment

import android.os.Bundle
import android.view.View
import android.view.Window
import com.inventoryorder.databinding.FragmentBookingSuccessfulBinding
import com.inventoryorder.ui.BaseInventoryFragment

class BookingSuccessfulFragment : BaseInventoryFragment<FragmentBookingSuccessfulBinding>(){

    companion object{
        fun newInstance(bundle: Bundle? = null) : BookingSuccessfulFragment{
            val fragment = BookingSuccessfulFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding?.tvHome)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.tvHome ->{
                baseActivity.onBackPressed()
            }

        }
    }




}