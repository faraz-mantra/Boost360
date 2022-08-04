package com.festive.poster.ui.promoUpdates.edit_post

import android.content.*
import android.content.Intent
import android.graphics.Typeface
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityEditPostBinding
import com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity
import com.festive.poster.models.TemplateUi
import com.festive.poster.ui.promoUpdates.bottomSheet.DeleteDraftBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.EditTemplateBottomSheet
import com.festive.poster.utils.SvgUtils
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.framework.analytics.SentryController
import com.framework.constants.IntentConstants
import com.framework.constants.UPDATE_PIC_FILE_NAME
import com.framework.extensions.gone
import com.framework.pref.UserSessionManager
import com.framework.utils.STTUtils
import com.framework.utils.convertStringToObj
import com.framework.utils.highlightHashTag
import com.framework.utils.saveAsImageToAppFolder
import com.framework.utils.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.io.File
import java.lang.Exception



class EditPostActivity: AppBaseActivity<ActivityEditPostBinding, FestivePosterViewModel>() {


    private var sttUtils:STTUtils?=null

    private var sessionLocal: UserSessionManager?=null
    var posterModel:TemplateUi?=null
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
        fun launchActivity(context: Context,posterModel: TemplateUi){
            val intent = Intent(context,EditPostActivity::class.java)
            intent.putExtra(IK_POSTER,Gson().toJson(posterModel))
            context.startActivity(intent)
        }
    }
    override fun onCreateView() {
        initStt()
        sessionLocal = UserSessionManager(this)

        try {
            posterModel = convertStringToObj(intent.getStringExtra(IK_POSTER)!!)
            initUI()
            setOnClickListener(binding?.btnTapToEdit, binding?.captionLayout?.etInput,
                binding?.ivCloseEditing, binding?.tvPreviewAndPost,binding?.ivVoiceOver,binding?.ivCloseHashtag)
        }catch (e:Exception){
            SentryController.captureException(e)
        }


    }

    private fun initUI() {
        lifecycleScope.launch {
            binding!!.ivTemplate.setImageBitmap(SvgUtils.svgToBitmap(posterModel?.primarySvgUrl))
        }
        binding?.captionLayout?.etInput?.setText(highlightHashTag(posterModel?.primaryText,R.color.black_4a4a4a,R.font.bold))
        binding?.captionLayout?.etInput?.requestFocus()
        val imm =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding?.captionLayout?.etInput, InputMethodManager.SHOW_IMPLICIT)

        addHashTagFunction()
        binding!!.tvHashtagSubtitle.text = spanColor(
            getString(R.string.type_in_the_caption_to_create_your_own_hashtags),R.color.color395996,
            "#"
        )
        KeyboardVisibilityEvent.setEventListener(
            this,
            KeyboardVisibilityEventListener {
                // Ah... at last. do your thing :)
                if (it){
                    binding?.captionLayout?.inputLayout?.strokeColor = ContextCompat.getColor(this,R.color.black_4a4a4a)
                }else{
                    binding?.captionLayout?.inputLayout?.strokeColor = ContextCompat.getColor(this,R.color.colorAFAFAF)
                }
            })

    }

    override fun onBackPressed() {
        DeleteDraftBottomSheet.newInstance(object :DeleteDraftBottomSheet.Callbacks{
            override fun onDelete() {
                finish()
            }
        }).show(
            supportFragmentManager,DeleteDraftBottomSheet::class.java.name
        )
    }
    fun saveKeyValue(){

        viewModel.saveKeyValue(sessionLocal?.fPID,
        sessionLocal?.fpTag, arrayListOf(posterModel?.id!!), hashMapOf("test" to "test")
        ).observe(this,{
            if (it.isSuccess()){

            }
        })
    }

    override fun onResume() {
        super.onResume()
        setStatusBarColor(R.color.white)
    }
    private fun initStt() {
        sttUtils = STTUtils(object : STTUtils.Callbacks{
            override fun onDone(text: String?) {
                binding?.captionLayout?.etInput?.setText(highlightHashTag(text,R.color.black_4a4a4a,R.font.bold))
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
            binding?.ivCloseHashtag->{
                binding?.layoutHashtag?.gone()
            }
            binding?.btnTapToEdit -> {
                EditTemplateBottomSheet.newInstance(object :EditTemplateBottomSheet.Callbacks{
                    override fun onDone(header1: String, header2: String) {
                        saveKeyValue()
                    }
                }).show(supportFragmentManager, EditTemplateBottomSheet::class.java.name)
            }
            binding?.captionLayout?.etInput -> {

               /* CaptionBottomSheet.newInstance(binding?.captionLayout?.etInput?.text.toString()
                    ,object :CaptionBottomSheet.Callbacks{
                    override fun onDone(value: String) {
                        getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                        )
                        binding?.captionLayout?.etInput?.setText(highlightHashTag(value,R.color.black_4a4a4a))
                    }
                }).show(supportFragmentManager, CaptionBottomSheet::class.java.name)*/
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
                    lifecycleScope.launch {
                        withContext(Dispatchers.Default){
                           val file =  SvgUtils.svgToBitmap(it.primarySvgUrl)
                                ?.saveAsImageToAppFolder(getExternalFilesDir(null)?.path+File.separator+UPDATE_PIC_FILE_NAME)
                            if (file?.exists() == true){
                                PostPreviewSocialActivity.launchActivity(
                                    this@EditPostActivity,binding?.captionLayout?.etInput?.text.toString(),
                                    file.path,
                                posterModel?.tags,
                                    IntentConstants.UpdateType.UPDATE_PROMO_POST.name
                                )
                            }

                        }

                    }

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




    private fun changeTheColor(s: String, start: Int, end: Int) {
        mSpannable?.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.black_4a4a4a)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSpannable?.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun addHashTagFunction() {


        Log.i(TAG, "addHashTagFunction: "+"hi hello")
        mSpannable = binding?.captionLayout?.etInput?.text

        binding?.captionLayout?.etInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(short_text: CharSequence, start: Int, before: Int, count: Int) {

                val text = binding?.captionLayout?.etInput?.text.toString()
                var last_index = 0
                text.trim().split(Regex("\\s+")).forEach {
                    Log.i(TAG, "addHashTagFunction: $it")
                    if (it.isNotEmpty() && it[0] == '#'){
                        val boldSpan = StyleSpan(Typeface
                            .BOLD)
                        val foregroundSpan = ForegroundColorSpan(ContextCompat.getColor(this@EditPostActivity, R.color.black))
                        mSpannable?.setSpan(foregroundSpan, text.indexOf(it,startIndex = last_index), text.indexOf(it,startIndex = last_index)+it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        mSpannable?.setSpan(boldSpan, text.indexOf(it,startIndex = last_index), text.indexOf(it,startIndex = last_index)+it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    }

                    last_index+=it.length-1

                }

            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }
}