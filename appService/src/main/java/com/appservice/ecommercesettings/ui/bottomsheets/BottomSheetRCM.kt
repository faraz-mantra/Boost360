import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetRcmBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetRCM : BaseBottomSheetDialog<BottomSheetRcmBinding, BaseViewModel>() {
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_rcm
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.understoodBtn)


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