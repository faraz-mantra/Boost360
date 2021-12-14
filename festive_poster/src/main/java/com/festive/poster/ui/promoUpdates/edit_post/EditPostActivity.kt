package com.festive.poster.ui.promoUpdates.edit_post

import android.app.Activity
import android.content.*
import android.content.Intent
import android.graphics.Typeface
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityEditPostBinding
import com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity
import com.festive.poster.models.PostUpdateTaskRequest
import com.festive.poster.models.PosterModel
import com.festive.poster.ui.promoUpdates.bottomSheet.CaptionBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.DeleteDraftBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.EditTemplateBottomSheet
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.STTUtils
import com.framework.utils.convertStringToObj
import com.framework.utils.highlightHashTag
import com.framework.webengageconstant.EVENT_LABEL_NULL
import com.framework.webengageconstant.POST_AN_UPDATE
import com.google.gson.Gson
import java.lang.Exception


class EditPostActivity: AppBaseActivity<ActivityEditPostBinding, FestivePosterViewModel>() {


    private var sttUtils:STTUtils?=null

    private var sessionLocal: UserSessionManager?=null
    var posterModel:PosterModel?=null
    private var mSpannable: Spannable? = null
    private var hashTagIsComing = 0
    override fun getLayout(): Int {
        return R.layout.activity_edit_post
    }

    override fun getViewModelClass(): Class<FestivePosterViewModel> {
        return FestivePosterViewModel::class.java
    }

    companion object {

        val IK_POSTER="IK_POSTER"
        fun launchActivity(context: Context,posterModel: PosterModel){
            val intent = Intent(context,EditPostActivity::class.java)
            intent.putExtra(IK_POSTER,Gson().toJson(posterModel))
            context.startActivity(intent)
        }
    }
    override fun onCreateView() {
        initStt()
        sessionLocal = UserSessionManager(this)

        posterModel = convertStringToObj(intent.getStringExtra(IK_POSTER)!!)
        initUI()
        setOnClickListener(binding?.btnTapToEdit, binding?.captionLayout?.etInput,
            binding?.ivCloseEditing, binding?.tvPreviewAndPost,binding?.ivVoiceOver)

    }

    private fun initUI() {
        SvgUtils.loadImage(posterModel?.url()!!,binding!!.ivTemplate,posterModel!!.keys,posterModel!!.isPurchased)
        binding!!.captionLayout.etInput.isFocusableInTouchMode =false
        addHashTagFunction()

    }

    fun saveKeyValue(){

        viewModel.saveKeyValue(sessionLocal?.fPID,
        sessionLocal?.fpTag, arrayListOf(posterModel?.id!!), hashMapOf("test" to "test")
        ).observe(this,{
            if (it.isSuccess()){

            }
        })
    }

    private fun initStt() {
        sttUtils = STTUtils(object : STTUtils.Callbacks{
            override fun onDone(text: String?) {
                binding?.captionLayout?.etInput?.setText(highlightHashTag(text))
            }
        })
        sttUtils?.init(this)
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

    override fun onClick(v: View?) {
        super.onClick(v)
        when (v) {
            binding?.btnTapToEdit -> {
                EditTemplateBottomSheet.newInstance(object :EditTemplateBottomSheet.Callbacks{
                    override fun onDone(header1: String, header2: String) {
                        saveKeyValue()
                    }
                }).show(supportFragmentManager, EditTemplateBottomSheet::class.java.name)
            }
            binding?.captionLayout?.etInput -> {

                CaptionBottomSheet.newInstance(binding?.captionLayout?.etInput?.text.toString()
                    ,object :CaptionBottomSheet.Callbacks{
                    override fun onDone(value: String) {
                        getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                        )
                        binding?.captionLayout?.etInput?.setText(highlightHashTag(value))
                    }
                }).show(supportFragmentManager, CaptionBottomSheet::class.java.name)
            }
            binding?.ivCloseEditing -> {
                DeleteDraftBottomSheet.newInstance(object :DeleteDraftBottomSheet.Callbacks{
                    override fun onDelete() {
                        finish()
                    }
                }).show(supportFragmentManager, DeleteDraftBottomSheet::class.java.name)
            }
            binding?.tvPreviewAndPost -> {
               // saveUpdatePost()
                posterModel?.let {
                    PostPreviewSocialActivity.launchActivity(this,binding?.captionLayout?.etInput?.text.toString(),
                        it
                    )
                }
            }
            binding?.ivVoiceOver->{
                sttUtils?.promptSpeechInput()
            }
        }
    }


//    private fun getRequestBizImage(): RequestBody {
//        SvgUtils
//        val responseBody = postImage?.readBytes()?.let { it.toRequestBody("image/png".toMediaTypeOrNull(), 0, it.size) }
//
//        return responseBody!!
//    }


    private fun addHashTagFunction() {
        mSpannable = binding?.captionLayout?.etInput?.text

        binding?.captionLayout?.etInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                var startChar: String? = null

                try {
                    startChar = text[start].toString()
                } catch (ex: Exception) {
                    startChar = ""
                }

                if (startChar == "#") {
                    changeTheColor(text.toString().substring(start), start, start + count)
                    hashTagIsComing++
                }

                if (startChar == " ") {
                    hashTagIsComing = 0
                }

                if (hashTagIsComing !== 0) {
                    changeTheColor(text.toString().substring(start), start, start + count)
                    hashTagIsComing++
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun changeTheColor(s: String, start: Int, end: Int) {
        mSpannable?.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.black_4a4a4a)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSpannable?.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}