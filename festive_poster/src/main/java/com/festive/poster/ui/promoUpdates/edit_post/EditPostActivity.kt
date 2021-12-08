package com.festive.poster.ui.promoUpdates.edit_post

import android.content.Intent
import android.view.View
import com.festive.poster.R
import com.festive.poster.databinding.ActivityEditPostBinding
import com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity
import com.festive.poster.ui.promoUpdates.bottomSheet.CaptionBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.DeleteDraftBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.EditTemplateBottomSheet
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel


class EditPostActivity: BaseActivity<ActivityEditPostBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.activity_edit_post
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        initUI()
    }

    private fun initUI() {
        binding?.captionLayout?.etInput?.isEnabled = false
        setOnClickListener(binding?.btnTapToEdit, binding?.captionLayout?.etInput, binding?.ivCloseEditing, binding?.tvPreviewAndPost)
    }

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.btnTapToEdit -> {
                EditTemplateBottomSheet().show(supportFragmentManager, EditTemplateBottomSheet::class.java.name)
            }
            binding?.captionLayout?.etInput -> {
                CaptionBottomSheet().show(supportFragmentManager, CaptionBottomSheet::class.java.name)
            }
            binding?.ivCloseEditing -> {
                DeleteDraftBottomSheet().show(supportFragmentManager, DeleteDraftBottomSheet::class.java.name)
            }
            binding?.tvPreviewAndPost -> {
                binding?.root?.context?.startActivity(Intent(binding?.root?.context, PostPreviewSocialActivity::class.java))
            }
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