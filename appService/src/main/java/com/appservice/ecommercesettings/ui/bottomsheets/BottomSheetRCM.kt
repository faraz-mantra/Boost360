import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.text.bold
import com.appservice.R
import com.appservice.databinding.BottomSheetRcmBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.fromHtml
import com.google.common.io.Files.append

class BottomSheetRCM : BaseBottomSheetDialog<BottomSheetRcmBinding, BaseViewModel>() {
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_rcm
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.understoodBtn)
    val s = SpannableStringBuilder()
      .bold { append(getString(R.string.rcm_bold_text)) }
      .append(" ")
      .append(getString(R.string.rcm_text))
    binding?.ctvRcmText?.text = (s)


  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.understoodBtn -> {
        dismiss()
      }
    }
  }
}