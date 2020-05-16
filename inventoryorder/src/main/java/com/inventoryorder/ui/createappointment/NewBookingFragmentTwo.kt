package com.inventoryorder.ui.createappointment

import android.os.Bundle
import android.view.View
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentNewBookingTwoBinding
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity

class NewBookingFragmentTwo : BaseInventoryFragment<FragmentNewBookingTwoBinding>()  {

    companion object{

        fun newInstance(bundle: Bundle?= null) : NewBookingFragmentTwo {

            val fragment  = NewBookingFragmentTwo()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()

        setOnClickListener(binding?.buttonCreateBooking,binding?.tvBack)
    }

    override fun onClick(v: View) {
        super.onClick(v)

        when(v){

            binding?.tvBack ->{
                baseActivity.onBackPressed()
            }

            binding?.buttonCreateBooking ->{
                startFragmentActivity(FragmentType.BOOKING_SUCCESSFUL, Bundle())
                baseActivity.finish()
            }


        }

    }




}