package com.dashboard.controller.ui.business.bottomsheet

import android.content.DialogInterface
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.dashboard.R
import com.dashboard.databinding.BottomSheetBusinessCategoryBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import kotlinx.android.synthetic.main.popup_window_website_menu.*

class BusinessCategoryBottomSheet : BaseBottomSheetDialog<BottomSheetBusinessCategoryBinding, BaseViewModel>() {

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_business_category
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    dialog.behavior.isDraggable = false
    setOnClickListener(
      binding?.btnContactSupport,
      binding?.btnUnderstood,
      binding?.rivCloseBottomSheet
    )
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnContactSupport -> {
       needHelp()
      }
      binding?.btnUnderstood, binding?.rivCloseBottomSheet -> {
        dismiss()
      }
    }
  }
  protected fun needHelp() {
    val s = SpannableString(resources.getString(com.boost.presignin.R.string.need_help_desc))
    Linkify.addLinks(s, Linkify.ALL)
    val alertDialog = AlertDialog.Builder(ContextThemeWrapper(baseActivity, com.boost.presignin.R.style.AlertDialogCustom))
    alertDialog.setTitle(getString(com.boost.presignin.R.string.need_help_title)).setMessage(s).setPositiveButton(resources.getString(
      com.boost.presignin.R.string.okay), null)
    val alert = alertDialog.create()
    alert.show()
    alert.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
    alert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(baseActivity, com.boost.presignin.R.color.colorAccent))
  }
}