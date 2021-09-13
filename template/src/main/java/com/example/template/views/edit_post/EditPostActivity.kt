package com.example.template.views.edit_post

import androidx.databinding.DataBindingUtil
import com.example.template.R
import com.example.template.databinding.ActivityEditPostBinding
import com.example.template.databinding.BsheetEditPostBinding
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.content.DialogInterface

import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import java.util.*


class EditPostActivity: BaseActivity<ActivityEditPostBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_edit_post
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.btnTapToEdit?.setOnClickListener {
            val bSheet = BottomSheetDialog(this,R.style.BottomSheetTheme)
            val binding = DataBindingUtil.inflate<BsheetEditPostBinding>(layoutInflater,R.layout.bsheet_edit_post,null,false)
            bSheet.setContentView(binding.root)
            bSheet.show()
           /* Objects.requireNonNull(bSheet.window)
                .setSoftInputMode(SOFT_INPUT_STATE_VISIBLE)
            dialog.setContentView(sheetView)
            dialog.setOnShowListener(DialogInterface.OnShowListener { dialog ->
                val d = dialog as BottomSheetDialog
                val bottomSheetInternal: View =
                    d.findViewById(com.google.android.material.R.id.design_bottom_sheet)
                BottomSheetBehavior.from(bottomSheetInternal)
                    .setState(BottomSheetBehavior.STATE_EXPANDED)
            })
            dialog.show()*/
        }
    }
}