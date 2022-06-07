package com.boost.upgrades.ui.popup

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.appservice.utils.changeColorOfSubstring
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.interfaces.AddCardListener
import com.boost.upgrades.interfaces.EmailPopupListener
import com.boost.upgrades.ui.payment.PaymentViewModel
import com.boost.upgrades.utils.KeyboardUtils
import com.boost.upgrades.utils.Utils
import com.boost.upgrades.utils.Utils.hideSoftKeyboard
import com.framework.views.customViews.CustomTextView
import kotlinx.android.synthetic.main.add_card_popup.*
import org.json.JSONObject
import java.util.*


class AddCardPopUpFragement : DialogFragment() {

  lateinit var root: View
  var isKeyboardShowing = false
  private lateinit var viewModel: PaymentViewModel

  var monthList = arrayListOf<String>()
  var yearList = arrayListOf<String>()

  var cardNumber = ""
  var cardType = ""

  var customerId: String? = null

  companion object {
    lateinit var listener: AddCardListener
    fun newInstance(cardListener: AddCardListener) = AddCardPopUpFragement().apply {
      listener = cardListener
    }
  }

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
    root = inflater.inflate(R.layout.add_card_popup, container, false)
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    customerId = arguments!!.getString("customerId")
    //month and year range
    monthList = Utils.monthRange()
    yearList = Utils.yearRange(currentYear, currentYear + 10)

    return root

  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)

//        val monthListAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, monthList)
//        expiry_month_value.adapter = monthListAdapter
//
//        val yearListAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, yearList)
//        expiry_year_value.adapter = yearListAdapter
    setMonthYearAdaptor()

//        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

    add_card_outer_layout.setOnClickListener {
      if (isKeyboardShowing) {
        hideSoftKeyboard(requireActivity())
        isKeyboardShowing = false
      } else {
        dialog!!.dismiss()
      }
    }

    add_card_container_layout.setOnClickListener {
      hideSoftKeyboard(requireActivity())
    }

    add_cart_cancel_submit.setOnClickListener {
      clearData()
      dialog!!.dismiss()
    }

    add_cart_payment_submit.setOnClickListener {
      if (validateCardDetails()) {
        val data = JSONObject()
        data.put("method", "card")
        data.put("card[name]", name_on_card_value.text.toString())
        data.put("card[number]", cardNumber)
        data.put("card[expiry_month]", expiry_month_value.selectedItem.toString())
        data.put(
          "card[expiry_year]",
          expiry_year_value.selectedItem.toString()
            .substring(Math.max(expiry_year_value.selectedItem.toString().length - 2, 0))
        )
        data.put("card[cvv]", add_card_cvv.text.toString())
        if (save_card_for_fast_payment.isChecked) {
          data.put("customer_id", customerId);
//                    data.put("customer_id", "cust_ETcczL3iRGxBNt");
          data.put("save", "1");
        }
//                viewModel.UpdateCardData(data)
        listener.cardSelected(data)
        clearData()
        dialog!!.dismiss()
      }
    }

    card_number_value.addTextChangedListener(object : TextWatcher {

      private val space = '-'

      override fun afterTextChanged(s: Editable?) {

        // Remove spacing char
        if (s!!.length > 0 && s.length % 5 == 0) {
          val c: Char = s[s.length - 1]
          if (space == c) {
            s.delete(s.length - 1, s.length)
          }
        } else if (s!!.length > 0 && s.length % 5 != 0) {
          val c: Char = s[s.length - 1]
          if (space == c) {
            s.delete(s.length - 1, s.length)
          }
        }

        // Insert char where needed.
        if (s.length > 0 && s.length % 5 == 0) {
          val c: Char = s[s.length - 1]
          // Only if its a digit where there should be a space we insert a space
          if (Character.isDigit(c) && TextUtils.split(
              s.toString(),
              java.lang.String.valueOf(space)
            ).size <= 3
          ) {
            s.insert(s.length - 1, java.lang.String.valueOf(space))
          }
        }

        if (s.length > 7 && s.length < 9) {
          cardType = (requireActivity() as UpgradeActivity).razorpay.getCardNetwork(
            s.toString().replace("-", "")
          )
          Log.e("getCardNetwork", ">>>>>>>>>" + cardType)
          when (cardType) {
            "visa" -> {
              card_type_image.setImageResource(R.drawable.visacard)
            }
            "mastercard" -> {
              card_type_image.setImageResource(R.drawable.mastercard)
            }
            "maestro16" -> {
              card_type_image.setImageResource(R.drawable.maestrocard)
            }
            "amex" -> {
              card_type_image.setImageResource(R.drawable.amexcard)
            }
            "rupay" -> {
              card_type_image.setImageResource(R.drawable.rupaycard)
            }
            "maestro" -> {
              card_type_image.setImageResource(R.drawable.maestrocard)
            }
            "diners" -> {
              card_type_image.setImageResource(R.drawable.dinerscard)
            }
            "unknown" -> {
              card_type_image.setImageResource(R.drawable.card_number)
            }
          }
        }

        if (s.length <= 7) {
          card_type_image.setImageResource(R.drawable.card_number)
        }
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //nothing
      }

      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //nothing
      }

    })

  }

  fun validateCardDetails(): Boolean {
    if (name_on_card_value.text.isEmpty() || card_number_value.text.isEmpty() || expiry_month_value.selectedItem.toString()
        .isEmpty() || expiry_year_value.selectedItem.toString().isEmpty()
    ) {
      Toast.makeText(requireContext(), "Fields are empty", Toast.LENGTH_SHORT).show()
      return false
    }
    cardNumber = card_number_value.text.toString().replace("-", "", false)
    if (cardNumber.length != 16) {
      invalid_cardnumber.visibility = View.VISIBLE
      return false
    }
    invalid_cardnumber.visibility = View.GONE
    if ((requireActivity() as UpgradeActivity).razorpay.isValidCardNumber(cardNumber)) {
      invalid_cardnumber.visibility = View.GONE
      return true
    } else {
      invalid_cardnumber.visibility = View.VISIBLE
      return false
    }
  }

  fun clearData() {
    name_on_card_value.text.clear()
    card_number_value.text.clear()
    add_card_cvv.text.clear()
    setMonthYearAdaptor()
  }

  override fun onStop() {
    super.onStop()
    clearData()
  }

  fun setMonthYearAdaptor() {
    val monthListAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
      requireContext(),
      android.R.layout.simple_spinner_dropdown_item,
      monthList
    )
    expiry_month_value.adapter = monthListAdapter

    val yearListAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
      requireContext(),
      android.R.layout.simple_spinner_dropdown_item,
      yearList
    )
    expiry_year_value.adapter = yearListAdapter
  }

  override fun onResume() {
    super.onResume()
//    UserExperiorController.startScreen("MarketPlaceAddCardPopUpFragement")

  }

}