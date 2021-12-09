package com.festive.poster.ui.promoUpdates.bottomSheet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import com.festive.poster.R
import com.festive.poster.databinding.BsheetEditPostBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.imagepicker.ImageTags.Tags.TAG
import com.framework.models.BaseViewModel
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class EditTemplateBottomSheet : BaseBottomSheetDialog<BsheetEditPostBinding, BaseViewModel>() {


    private var callbacks:EditTemplateBottomSheet.Callbacks?=null
    companion object {
        @JvmStatic
        fun newInstance(callbacks:Callbacks): EditTemplateBottomSheet {
            val bundle = Bundle().apply {}
            val fragment = EditTemplateBottomSheet()
            fragment.callbacks = callbacks
            fragment.arguments = bundle
            return fragment
        }
    }

    interface Callbacks{
        fun onDone(header1:String,header2:String)
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_edit_post
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(
            binding?.btnDone,
            binding?.btnCancel,
            binding?.etHeader1,
            binding?.etHeader2
        )
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnDone -> {
                callbacks?.onDone(binding?.etHeader1?.text.toString(),
                    binding?.etHeader2?.text.toString())
              //  replaceText("SMILEY DENTAL CLINIC", binding?.etHeader1?.text.toString(), v)
              //  replaceText("Straighten your teeth with Invisible braces starting at RS.399/-", binding?.etHeader2?.text.toString(), v)
                dismiss()
            }

            binding?.btnCancel -> {
                dismiss()
            }
        }
    }

/*
    fun replaceText(key: String, value: String, v: View){

        var reader: BufferedReader? = null;
        try {
            reader = BufferedReader(
                InputStreamReader(v.context.assets.open("frame_14.svg"))
            );

            // do reading, usually loop until end of file reading
            var mLine:StringBuilder = StringBuilder()
            var line:String?=""
            while (line!= null) {
                line = reader.readLine()
                //process line
                if (line!=null)
                    mLine.append(line)
            }

            var result =mLine.toString()
            // val modified = result.replace("SMILEY DENTAL CLINIC","Suman Clinic")
            val modified = result.replace(key,value)

            Log.i(TAG, "readFile: "+modified)
            //binding?.ivTemplate?.setSVG(SVG.getFromString(modified))
            val clipboard = v.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label",modified)
            clipboard.setPrimaryClip(clip)


        } catch (e: IOException) {
            //log the exception
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (e: IOException) {
                    //log the exception
                    e.printStackTrace()
                }
            }
        }
    }
*/
}