package com.boost.cart.ui.popup

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.boost.cart.R
import com.boost.cart.CartActivity
import com.boost.cart.utils.SharedPrefs
import com.boost.cart.utils.WebEngageController
import kotlinx.android.synthetic.main.renewal_popup.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.Observer
import com.boost.cart.ui.home.CartViewModel
import com.framework.analytics.SentryController
import com.framework.webengageconstant.ADDONS_MARKETPLACE_GSTIN_LOADED
import com.framework.webengageconstant.AUTO_RENEW
import com.framework.webengageconstant.NO_EVENT_VALUE

class RenewalPopUpFragment : DialogFragment() {

  lateinit var root: View

  private lateinit var viewModel: CartViewModel
  private var renewMode = ""
  var autoRenew: Boolean = false
  var remindMelater: Boolean = false
  lateinit var prefs: SharedPrefs

  override fun onStart() {
    super.onStart()
    val width = ViewGroup.LayoutParams.MATCH_PARENT
    val height = ViewGroup.LayoutParams.MATCH_PARENT
    dialog!!.window!!.setLayout(width, height)
    dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.e("onDestroy", "onDestroy of LoginFragment")
    viewModel.updateRenewValue("")
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.renewal_popup, container, false)
    prefs = SharedPrefs(activity as CartActivity)
    return root
  }

  @SuppressLint("FragmentLiveDataObserve")
  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    viewModel = ViewModelProviders.of(requireActivity()).get(CartViewModel::class.java)


    renew_popup_outer_layout.setOnClickListener {
      radioGrpOrdering.clearCheck()
      dialog!!.dismiss()
    }

    enter_card_renew_layout.setOnClickListener { }

    renew_submit.setOnClickListener {
      if (validationRenewalOption()) {
        viewModel.updateRenewPopupClick(renewMode)
//                viewModel.updateProceedClick(true)
        radioGrpOrdering.clearCheck()
        dialog!!.dismiss()
      }
    }
    radioGrpOrdering.setOnCheckedChangeListener({ group, checkedId ->
      when (checkedId) {
        R.id.actionAutoRenew -> {
          renewMode = "AUTO_RENEW"
          autoRenew = true
          remindMelater = false
//                    viewModel.updateRenewValue("AUTO_RENEW")
        }
        R.id.actionRemindRenew -> {
          renewMode = "REMIND_ME"
          autoRenew = false
          remindMelater = true
//                    viewModel.updateRenewValue("REMIND_ME")
        }
      }
    })
    WebEngageController.trackEvent(ADDONS_MARKETPLACE_GSTIN_LOADED, AUTO_RENEW, NO_EVENT_VALUE)
    var calue = getNextRenewalDate()
    val strRenewFormat = resources.getString(R.string.auto_renewal_date, calue)
//        val strRenewFormat = resources.getString(R.string.radio_auto_renew, calue)
    autoRenewSubtext.setText(strRenewFormat)
//        actionAutoRenew.setText(strRenewFormat)
    autoRenewSubtext.setOnClickListener {
      Log.v("flagListener1", "autoRenew: " + autoRenew + " remindMelater: " + remindMelater)
      if (!autoRenew) {
        autoRenew = true
        remindMelater = false
        actionAutoRenew.setChecked(true)
        renewMode = "AUTO_RENEW"
      } else {
//                autoRenew = false
//                actionAutoRenew.setChecked(false)
//                renewMode = "REMIND_ME"
      }
    }
    remindRenewSubtext.setOnClickListener {
      Log.v("flagListener2", "autoRenew: " + autoRenew + " remindMelater: " + remindMelater)
      if (!remindMelater) {
        autoRenew = false
        remindMelater = true
        actionRemindRenew.setChecked(true)
        renewMode = "REMIND_ME"
      } else {
//                autoRenew = false
//                actionAutoRenew.setChecked(false)
//                renewMode = "REMIND_ME"
      }
    }
//        initMvvM()


  }

  fun getNextRenewalDate(): String {
    val c = Calendar.getInstance().time

    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = df.format(c)  // Today's date
//        val dt = "2012-01-04"

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val todayDate: Calendar = Calendar.getInstance()
    try {
      todayDate.setTime(sdf.parse(formattedDate))
    } catch (e: ParseException) {
      SentryController.captureException(e)
      e.printStackTrace()
      SentryController.captureException(e)
    }
//        todayDate.add(Calendar.DATE, 365) // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
    todayDate.add(
      Calendar.DATE,
      prefs.getStoreMonthsValidity()
    ) // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE

    val sdf1 = SimpleDateFormat("dd")
    val nextDate: String = sdf1.format(todayDate.getTime())
    val sdf2 = SimpleDateFormat("MMMM yyyy")
    val nextDate2: String = sdf2.format(todayDate.getTime())
    Log.v("SimpleDateFormat", " " + nextDate)
    Log.v("SimpleDateFormat", " " + nextDate2)
    return nextDate + "th " + nextDate2
  }

  fun validationRenewalOption(): Boolean {
    val value = renewMode
    if (value.isEmpty()) {
      Toast.makeText(requireContext(), "EmptyField", Toast.LENGTH_LONG).show()
      return false
    }
    return true

  }

  @SuppressLint("FragmentLiveDataObserve")
  fun initMvvM() {

    viewModel.getRenewPopupClick().observe(this, Observer {
      if (it != null) {
        viewModel.updateRenewValue(it)
      }
    })
  }

  override fun onResume() {
    super.onResume()
//    UserExperiorController.startScreen("MarketPlaceRenewalPopUpFragment")

  }

}