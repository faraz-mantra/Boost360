package com.inventoryorder.ui.createappointment

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentNewBookingTwoBinding
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity
import kotlinx.android.synthetic.main.fragment_new_booking_two.*

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

        setOnClickListener(binding?.buttonCreateBooking,binding?.tvBack,binding?.buttonPayAtClinic,binding?.buttonPayOnline)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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

            binding?.buttonPayAtClinic ->{
                buttonPayAtClinic.background = ContextCompat.getDrawable(baseActivity, R.color.orange)
                buttonPayAtClinic.setTextColor(resources.getColor(R.color.warm_grey_10))

//                buttonPayOnline.backgroundTintList = ContextCompat.getColorStateList(baseActivity,R.color.white)
                buttonPayOnline.background = ContextCompat.getDrawable(baseActivity,R.color.white)
                buttonPayOnline.setTextColor(resources.getColor(R.color.primary_grey))
            }
            binding?.buttonPayOnline ->{
                buttonPayOnline.background = ContextCompat.getDrawable(baseActivity,R.color.orange)
                buttonPayOnline.setTextColor(resources.getColor(R.color.warm_grey_10))

                buttonPayAtClinic.background = ContextCompat.getDrawable(baseActivity, R.color.white)
//                buttonPayAtClinic.backgroundTintList = ContextCompat.getColorStateList(baseActivity,R.color.white)
                buttonPayAtClinic.setTextColor(resources.getColor(R.color.primary_grey))

            }



        }

    }




}