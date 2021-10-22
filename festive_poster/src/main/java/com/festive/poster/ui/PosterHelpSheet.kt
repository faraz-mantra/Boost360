package com.festive.poster.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat.getColor
import com.festive.poster.R
import com.festive.poster.databinding.SheetPosterHelpBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class PosterHelpSheet : BaseBottomSheetDialog<SheetPosterHelpBinding, BaseViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(): PosterHelpSheet {
      val bundle = Bundle().apply {}
      val fragment = PosterHelpSheet()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.sheet_poster_help
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.rivCloseBottomSheet, binding?.btnConfirm, binding?.supportBtn)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.rivCloseBottomSheet, binding?.btnConfirm -> dismiss()
      binding?.supportBtn -> {
        needHelp()
        dismiss()
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
  }

  private fun needHelp() {
    val s = SpannableString(resources.getString(R.string.need_help_desc))
    Linkify.addLinks(s, Linkify.ALL)
    val alertDialog = AlertDialog.Builder(ContextThemeWrapper(baseActivity, R.style.CustomAlertDialogTheme))
    alertDialog.setTitle(getString(R.string.need_help_title)).setMessage(s).setPositiveButton(resources.getString(R.string.okay), null)
    val alert = alertDialog.create()
    alert.show()
    alert.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
    alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getColor(baseActivity, R.color.colorAccentLight))
  }
}