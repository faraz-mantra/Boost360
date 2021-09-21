package com.example.template.views.edit_post

import android.R.attr
import androidx.databinding.DataBindingUtil
import com.example.template.R
import com.example.template.databinding.ActivityEditPostBinding
import com.example.template.databinding.BsheetEditPostBinding
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.util.Log

import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import com.caverock.androidsvg.SVG
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import android.R.attr.label
import android.content.*


class EditPostActivity: BaseActivity<ActivityEditPostBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_edit_post
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
       // binding?.ivTemplate?.setImageAsset("frame_14.svg")
        binding?.btnTapToEdit?.setOnClickListener {
            val bSheet = BottomSheetDialog(this,R.style.BottomSheetTheme)
            val binding = DataBindingUtil.inflate<BsheetEditPostBinding>(layoutInflater,R.layout.bsheet_edit_post,null,false)
            bSheet.setContentView(binding.root)
            bSheet.show()

            binding.btnDone.setOnClickListener {
                replaceText("SMILEY DENTAL CLINIC",binding.etHeader1.text.toString())
                replaceText("Straighten your teeth with Invisible braces starting at RS.399/-",
                binding.etHeader2.text.toString())
                bSheet.dismiss()
            }
        }


    }

    fun replaceText(key:String,value:String){

        var reader: BufferedReader? = null;
        try {
            reader = BufferedReader(
                InputStreamReader(assets.open("frame_14.svg"))
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
            binding?.ivTemplate?.setSVG(SVG.getFromString(modified))
            val clipboard =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label",modified)
            clipboard.setPrimaryClip(clip)


        } catch (e: IOException) {
            //log the exception
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (e:IOException) {
                    //log the exception
                    e.printStackTrace()
                }
            }
        }
    }
}