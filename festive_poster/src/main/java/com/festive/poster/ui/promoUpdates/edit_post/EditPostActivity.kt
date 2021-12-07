package com.festive.poster.ui.promoUpdates.edit_post

import androidx.databinding.DataBindingUtil
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.util.Log

import com.caverock.androidsvg.SVG
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import android.content.*
import com.festive.poster.R
import com.festive.poster.databinding.ActivityEditPostBinding
import com.festive.poster.databinding.BsheetEditPostBinding
import com.festive.poster.ui.festivePoster.PosterHelpSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.CaptionBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.DeleteDraftBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.EditTemplateBottomSheet
import com.framework.extensions.afterTextChanged
import com.framework.utils.fromHtml


class EditPostActivity: BaseActivity<ActivityEditPostBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_edit_post
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        initUI()
       // binding?.ivTemplate?.setImageAsset("frame_14.svg")

    }

    private fun initUI() {
        binding?.captionLayout?.etInput?.isEnabled = false
        binding?.btnTapToEdit?.setOnClickListener {
            EditTemplateBottomSheet().show(supportFragmentManager, EditTemplateBottomSheet::class.java.name)
            /*val bSheet = BottomSheetDialog(this,R.style.BottomSheetTheme)
            val binding = DataBindingUtil.inflate<BsheetEditPostBinding>(layoutInflater,R.layout.bsheet_edit_post,null,false)
            bSheet.setContentView(binding.root)
            bSheet.show()

            binding.btnDone.setOnClickListener {
                replaceText("SMILEY DENTAL CLINIC",binding.etHeader1.text.toString())
                replaceText("Straighten your teeth with Invisible braces starting at RS.399/-",
                binding.etHeader2.text.toString())
                bSheet.dismiss()
            }

            binding.btnCancel.setOnClickListener {
                bSheet.dismiss()
            }

            binding?.etHeader1?.afterTextChanged {
                binding.tvTitleCount.text = fromHtml("<font color=#09121F>${it.length}</font><font color=#9DA4B2> /17</font>")
            }

            binding?.etHeader2?.afterTextChanged {
                binding.tvSubtitleCount.text = fromHtml("<font color=#09121F>${it.length}</font><font color=#9DA4B2> /80</font>")
            }*/
        }
        binding?.captionLayout?.inputLayout?.setOnClickListener {
            CaptionBottomSheet().show(supportFragmentManager, CaptionBottomSheet::class.java.name)
        }

        binding?.ivCloseEditing?.setOnClickListener {
            DeleteDraftBottomSheet().show(supportFragmentManager, DeleteDraftBottomSheet::class.java.name)
        }
    }

    /* fun replaceText(key:String,value:String){

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
     }*/
}