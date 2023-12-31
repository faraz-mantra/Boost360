package com.boost.payment.ui.popup

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.boost.payment.R
import com.boost.payment.PaymentActivity
import com.boost.payment.interfaces.AddCardListener
import com.boost.payment.ui.payment.PaymentViewModel
import com.boost.payment.utils.Utils
import com.boost.payment.utils.Utils.hideSoftKeyboard
import kotlinx.android.synthetic.main.add_cards_popup.*
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
    root = inflater.inflate(R.layout.add_cards_popup, container, false)
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    customerId = requireArguments()!!.getString("customerId")
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

    back_btn.setOnClickListener {
      clearData()
      dialog!!.dismiss()
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
        data.put("card[expiry_month]", expiry_mm_yyyy_value.text.split("/").get(0).toString())
        data.put(
          "card[expiry_year]",
          expiry_mm_yyyy_value.text.split("/").get(1).toString()
            .substring(Math.max(expiry_mm_yyyy_value.text.split("/").get(1).toString().length - 2, 0))
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

    expiry_mm_yyyy_value.addTextChangedListener(object : TextWatcher{
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

      override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
        if (start == 1 && start+added == 2 && p0?.contains('/') == false) {
          expiry_mm_yyyy_value.setText(p0.toString() + "/")
          expiry_mm_yyyy_value.setSelection(expiry_mm_yyyy_value.length())
        } else if (start == 3 && start-removed == 2 && p0?.contains('/') == true) {
          expiry_mm_yyyy_value.setText(p0.toString().replace("/", ""))
          expiry_mm_yyyy_value.setSelection(expiry_mm_yyyy_value.length())
        }
      }

      override fun afterTextChanged(s: Editable?) {}

    })

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
          cardType = (requireActivity() as PaymentActivity).razorpay.getCardNetwork(
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
              card_type_image.setImageResource(R.drawable.card_numbers)
            }
          }
        }

        if (s.length <= 7) {
          card_type_image.setImageResource(R.drawable.card_numbers)
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
    if (name_on_card_value.text.isEmpty() || card_number_value.text.isEmpty() || expiry_mm_yyyy_value.text.isEmpty()
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
    if(!expiry_mm_yyyy_value.text.contains("/")
      || (expiry_mm_yyyy_value.text.split("/").get(0).length != 2
      && expiry_mm_yyyy_value.text.split("/").get(1).length != 4 )){
      Toast.makeText(requireContext(), "Invalid Expiry", Toast.LENGTH_LONG).show()
      expiry_mm_yyyy_value.setBackgroundResource(R.drawable.edittext_border_red_line_bg)
      expiry_mm_yyyy_value.text.clear()
      expiry_mm_yyyy_value.requestFocus()
      return false
    }else{
      expiry_mm_yyyy_value.setBackgroundResource(R.drawable.edittext_border_line_bg)
    }
    if ((requireActivity() as PaymentActivity).razorpay.isValidCardNumber(cardNumber)) {
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
  }

  override fun onStop() {
    super.onStop()
    clearData()
  }

  override fun onResume() {
    super.onResume()
//    UserExperiorController.startScreen("MarketPlaceAddCardPopUpFragement")

  }

}