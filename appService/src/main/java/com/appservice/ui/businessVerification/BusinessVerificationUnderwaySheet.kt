package com.appservice.ui.businessVerification

import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Toast
import com.appservice.R
import com.appservice.databinding.SheetBusinessVerificationUnderwayBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BusinessVerificationUnderwaySheet : BaseBottomSheetDialog<SheetBusinessVerificationUnderwayBinding, BaseViewModel>() {

  lateinit var onSubmitClick:(Unit)->Unit

  val clickableSpan: ClickableSpan = object : ClickableSpan() {
    override fun onClick(textView: View) {
      val intent = Intent(Intent.ACTION_SENDTO)
      intent.data = Uri.parse("mailto:") // only email apps should handle this

      intent.putExtra(Intent.EXTRA_EMAIL, "ria@nowfloats.com")
      intent.putExtra(Intent.EXTRA_SUBJECT, "Need help with Boost360")
      if (intent.resolveActivity(context!!.packageManager) != null) {
        context!!.startActivity(intent)
      } else {
        Toast.makeText(context, "Unable to send email", Toast.LENGTH_SHORT).show()
      }
    }
  }

  override fun getLayout(): Int {
    return R.layout.sheet_business_verification_underway
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  companion object{
    fun newInstance(onSubmitClickParam:(Unit)->Unit):BusinessVerificationUnderwaySheet{
      return BusinessVerificationUnderwaySheet().apply {
        onSubmitClick=onSubmitClickParam
      }
    }
  }

  override fun onCreateView() {
    setOnClickListener(binding?.btnSubmitSubmit)
    val spannableString =
      SpannableString("This process usually takes 1 working day. Once verified we will notify you. If you want to make any change to above account details, please write to ria@nowfloats.com with this screenshot attached.")
    val underlineSpan = UnderlineSpan()
    val textPaint = TextPaint()
    underlineSpan.updateDrawState(textPaint)
    spannableString.setSpan(underlineSpan, 149,166, 0)
    spannableString.setSpan(clickableSpan, 149,166, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    binding?.processDescription?.text = spannableString
    binding?.processDescription?.isClickable = true;
    binding?.processDescription?.movementMethod = LinkMovementMethod.getInstance()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when(v){
      binding?.btnSubmitSubmit->{
        dismiss()
        onSubmitClick.invoke(Unit)
      }
    }
  }
}