package com.boost.upgrades.ui.popup

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.boost.upgrades.R
import com.boost.upgrades.ui.cart.CartViewModel
import com.boost.upgrades.utils.Utils
import com.boost.upgrades.utils.WebEngageController
import com.framework.webengageconstant.ADDONS_MARKETPLACE_TAN_NUMBER_LOADED
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.TAN_NUMBER
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.tan_popup.*

class TANPopUpFragment : DialogFragment() {

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
    root = inflater.inflate(R.layout.tan_popup, container, false)

    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    viewModel = ViewModelProviders.of(requireActivity()).get(CartViewModel::class.java)

    entered_tan_value.setFilters(entered_tan_value.filters + InputFilter.AllCaps())

    tan_popup_outer_layout.setOnClickListener {
      dialog!!.dismiss()
    }

    enter_tan_layout.setOnClickListener { }

    tan_submit.setOnClickListener {
      Utils.hideSoftKeyboard(requireActivity())
      if (validationTAN()) {
        viewModel.updateTAN(entered_tan_value.text.toString())
        entered_tan_value.setText("")
        dialog!!.dismiss()
      }
    }

    WebEngageController.trackEvent(ADDONS_MARKETPLACE_TAN_NUMBER_LOADED, TAN_NUMBER, NO_EVENT_VALUE)

  }

  fun validationTAN(): Boolean {
    val value = entered_tan_value.text.toString()
    if (value.isEmpty()) {
      Toasty.error(requireContext(), "Please enter a valid TAN").show()
      return false
    }
    return true


  }
  override fun onResume() {
    super.onResume()
//    UserExperiorController.startScreen("MarketPlaceTANPopUpFragment")

  }


}