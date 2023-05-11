package com.boost.payment.ui.popup

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.payment.R
import com.boost.payment.interfaces.UpiPayListener
import com.boost.payment.ui.payment.PaymentViewModel
import com.framework.utils.copyToClipBoard
import com.framework.webengageconstant.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.add_payment_link_popup.*

class PaymentLinkPopUpFragement : DialogFragment() {

  private lateinit var amountValue: String
  private lateinit var paymentLinkValue: String
  lateinit var root: View
  private lateinit var viewModel: PaymentViewModel

  var validatingStatus = false
  lateinit var prefs: SharedPrefs

  companion object {
    lateinit var listener: UpiPayListener
    fun newInstance(upiPayListener: UpiPayListener) = PaymentLinkPopUpFragement().apply {
      listener = upiPayListener
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
    root = inflater.inflate(R.layout.add_payment_link_popup, container, false)
    prefs = activity?.let { SharedPrefs(it) }!!
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)
    payment_link_text.text = paymentLinkValue
    upi_popup_description.text = "Share with someone for ₹"+amountValue
    upi_popup_outer_layout.setOnClickListener {
      dialog!!.dismiss()
    }

    share_via_whatsapp.setOnClickListener {
      if (appInstalledOrNot("com.whatsapp")) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        intent.putExtra(Intent.EXTRA_TEXT, upi_popup_description.text.toString()+"\n\n"+paymentLinkValue)
        try {
          requireActivity().startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
          Toasty.info(requireContext(),"Whatsapp have not been installed.").show()
        }
            } else {
                Toasty.info(requireContext(), "Install WhatsApp to Continue").show()
            }
    }

    share_via_telegram.setOnClickListener {
      if (appInstalledOrNot("org.telegram.messenger")) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("org.telegram.messenger")
        intent.putExtra(Intent.EXTRA_TEXT, upi_popup_description.text.toString()+"\n\n"+paymentLinkValue)
        try {
          requireActivity().startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
          Toasty.info(requireContext(),"Telegram have not been installed.").show()
        }
      } else {
        Toasty.info(requireContext(), "Install Telegram to Continue").show()
      }
    }

    share_via_gmail.setOnClickListener {
      if (appInstalledOrNot("com.google.android.gm")) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.google.android.gm")
        intent.putExtra(Intent.EXTRA_TEXT, upi_popup_description.text.toString()+"\n\n"+paymentLinkValue)
        try {
          requireActivity().startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
          Toasty.info(requireContext(),"Gmail have not been installed.").show()
        }
      } else {
        Toasty.info(requireContext(), "Install GMail to Continue").show()
      }
    }

    share_via_more.setOnClickListener {
      val intent = Intent(Intent.ACTION_SEND)
      intent.type = "text/plain"
      intent.putExtra(Intent.EXTRA_TEXT, upi_popup_description.text.toString()+"\n\n"+paymentLinkValue)
      try {
        requireActivity().startActivity(intent)
      } catch (ex: ActivityNotFoundException) {
        Toasty.info(requireContext(),"Gmail have not been installed.").show()
      }
    }

    link_copy_layout.setOnClickListener {
      copyToClipBoard(paymentLinkValue)
    }

    upi_popup_container_layout.setOnClickListener {}

  }

  private fun appInstalledOrNot(packageName: String): Boolean {
    val pm: PackageManager = requireActivity().packageManager
    try {
      pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
      return true
    } catch (e: PackageManager.NameNotFoundException) {
    }
    try {
      startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
    } catch (e: ActivityNotFoundException) {
      startActivity(
        Intent(
          Intent.ACTION_VIEW,
          Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        )
      )
    }
    return false
  }

  fun setAmount(amount: String) {
    amountValue = amount
  }

  fun setPaymentLink(result: String) {
    paymentLinkValue = result
  }
}