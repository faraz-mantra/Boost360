package com.festive.poster.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.festive.poster.R
import com.festive.poster.databinding.SheetPosterHelpBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class PosterHelpSheet: BaseBottomSheetDialog<SheetPosterHelpBinding, BaseViewModel>() {


    companion object{
        @JvmStatic
        fun newInstance(): PosterHelpSheet {
            val bundle = Bundle().apply {
            }
            val fragment =PosterHelpSheet()
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

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }


}