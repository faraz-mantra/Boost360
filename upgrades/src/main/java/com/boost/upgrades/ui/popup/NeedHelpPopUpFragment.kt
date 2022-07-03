package com.boost.upgrades.ui.popup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.Utils
import com.boost.upgrades.utils.WebEngageController
import com.framework.webengageconstant.ADDONS_MARKETPLACE_EXPERT_SPEAK
import com.framework.webengageconstant.CLICK
import com.framework.webengageconstant.NO_EVENT_VALUE
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.need_help_popup.*




class NeedHelpPopUpFragment : DialogFragment() {

  lateinit var root: View
  lateinit var prefs: SharedPrefs

  companion object {
    fun newInstance() = NeedHelpPopUpFragment()
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
    root = inflater.inflate(R.layout.need_help_popup, container, false)
    return root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    prefs = SharedPrefs(activity as UpgradeActivity)

    need_help_expert.setOnClickListener {
      callExpertContact(prefs.getExpertContact())
    }

    need_help_email.setOnClickListener {
      sendNeedHelpEmail(arrayOf<String>("ria@nowfloats.com"), "Need Help", "Hi Ria,\n")
    }

    need_help_outer_layout.setOnClickListener {
      Utils.hideSoftKeyboard(requireActivity())
      dialog!!.dismiss()
    }

  }

  private fun sendNeedHelpEmail(
    addresses: Array<String>, subject: String, text: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.putExtra(Intent.EXTRA_EMAIL, addresses)
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, text)
    intent.type = "message/rfc822"
    startActivity(Intent.createChooser(intent, "Send Email Using..."))
  }



  fun callExpertContact(phone: String?) {
    Log.d("callExpertContact", " " + phone)
    if (phone != null) {
      WebEngageController.trackEvent(ADDONS_MARKETPLACE_EXPERT_SPEAK, CLICK, NO_EVENT_VALUE)
      val callIntent = Intent(Intent.ACTION_DIAL)
      callIntent.data = Uri.parse("tel:" + phone)
      startActivity(Intent.createChooser(callIntent, "Call by:"))
    } else {
      Toasty.error(
        requireContext(),
        "Expert will be available tomorrow from 10AM",
        Toast.LENGTH_LONG
      ).show()
    }
  }


}