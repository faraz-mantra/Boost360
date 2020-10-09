package com.boost.upgrades.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.boost.upgrades.R
import com.boost.upgrades.ui.cart.CartViewModel
import com.boost.upgrades.utils.Utils.isValidGSTIN
import com.boost.upgrades.utils.WebEngageController
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.gstin_popup.*
import kotlinx.android.synthetic.main.renewal_popup.*

class RenewalPopUpFragment : DialogFragment(){

    lateinit var root: View

    private lateinit var viewModel: CartViewModel
    private var renewMode = ""

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
        root = inflater.inflate(R.layout.renewal_popup, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(requireActivity()).get(CartViewModel::class.java)


        renew_popup_outer_layout.setOnClickListener {
            dialog!!.dismiss()
        }

        enter_card_renew_layout.setOnClickListener {  }

        renew_submit.setOnClickListener {
            if(validationRenewalOption()){
                viewModel.updateRenewValue(renewMode)
                dialog!!.dismiss()
            }
        }
        radioGrpOrdering.setOnCheckedChangeListener({ group, checkedId ->
            when (checkedId) {
                R.id.actionAutoRenew -> {
                    renewMode = "AUTO_RENEW"
//                    viewModel.updateRenewValue("AUTO_RENEW")
                }
                R.id.actionRemindRenew -> {
                    renewMode = "REMIND_ME"
//                    viewModel.updateRenewValue("REMIND_ME")
                }
            }
        })
        WebEngageController.trackEvent("ADDONS_MARKETPLACE GSTIN Loaded", "GSTIN", "")

    }

    fun validationRenewalOption(): Boolean{
        val value = renewMode
        if(value.isEmpty()){
            Toast.makeText(requireContext(),"EmptyField",Toast.LENGTH_LONG).show()
            return false
        }
        return true

    }


}