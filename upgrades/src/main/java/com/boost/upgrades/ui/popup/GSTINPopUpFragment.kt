package com.boost.upgrades.ui.popup

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.boost.upgrades.R
import com.boost.upgrades.ui.cart.CartViewModel
import com.boost.upgrades.utils.Utils.isValidGSTIN
import kotlinx.android.synthetic.main.gstin_popup.*

class GSTINPopUpFragment : DialogFragment(){

    lateinit var root: View

    private lateinit var viewModel: CartViewModel

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.gstin_popup, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(requireActivity()).get(CartViewModel::class.java)

        entered_gstin_value.setFilters(entered_gstin_value.filters + InputFilter.AllCaps())

        gstin_popup_outer_layout.setOnClickListener {
            dialog!!.dismiss()
        }

        enter_gstin_layout.setOnClickListener {  }

        gstin_submit.setOnClickListener {
            if(validationGSTIN()){
                viewModel.updateGSTIN(entered_gstin_value.text.toString())
                entered_gstin_value.setText("")
                dialog!!.dismiss()
            }
        }

    }

    fun validationGSTIN(): Boolean{
        val value = entered_gstin_value.text.toString()
        if(value.isEmpty()){
            Toast.makeText(requireContext(),"EmptyField",Toast.LENGTH_LONG).show()
            return false
        }
        if (isValidGSTIN(value)){
            return true
        }else{
            Toast.makeText(requireContext(),"Invalid GSTIN Number!!",Toast.LENGTH_LONG).show()
            return false
        }

    }


}