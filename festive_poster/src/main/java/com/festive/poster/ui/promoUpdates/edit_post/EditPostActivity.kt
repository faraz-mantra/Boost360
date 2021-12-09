package com.festive.poster.ui.promoUpdates.edit_post

import android.app.Activity
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
import androidx.lifecycle.lifecycleScope
import android.content.Intent
import android.view.View
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.databinding.ActivityEditPostBinding
import com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity
import com.festive.poster.databinding.BsheetEditPostBinding
import com.festive.poster.models.PostUpdateTaskRequest
import com.festive.poster.models.PosterModel
import com.festive.poster.ui.festivePoster.PosterHelpSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.CaptionBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.DeleteDraftBottomSheet
import com.festive.poster.ui.promoUpdates.bottomSheet.EditTemplateBottomSheet
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.FestivePosterViewModel
import com.festive.poster.viewmodels.UpdatesViewModel
import com.framework.extensions.afterTextChanged
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.utils.convertStringToObj
import com.framework.utils.fromHtml
import com.framework.utils.saveAsImageToAppFolder
import com.framework.utils.saveAsTempFile
import com.framework.webengageconstant.EVENT_LABEL_NULL
import com.framework.webengageconstant.POST_AN_UPDATE
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class EditPostActivity: AppBaseActivity<ActivityEditPostBinding, FestivePosterViewModel>() {


    private var sessionLocal: UserSessionManager?=null
    var posterModel:PosterModel?=null
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
        sessionLocal = UserSessionManager(this)

        posterModel = convertStringToObj(intent.getStringExtra(IK_POSTER)!!)
        initUI()
        setOnClickListener(binding?.btnTapToEdit, binding?.captionLayout?.etInput, binding?.ivCloseEditing, binding?.tvPreviewAndPost)

    }

    private fun initUI() {
        SvgUtils.loadImage(posterModel?.url()!!,binding!!.ivTemplate,posterModel!!.keys,posterModel!!.isPurchased)
        binding?.captionLayout?.etInput?.isEnabled = false
        binding?.btnTapToEdit?.setOnClickListener {
            EditTemplateBottomSheet.newInstance(object :EditTemplateBottomSheet.Callbacks{
                override fun onDone(header1: String, header2: String) {
                    saveKeyValue()
                }
            }).show(supportFragmentManager, EditTemplateBottomSheet::class.java.name)
        }
        binding?.captionLayout?.inputLayout?.setOnClickListener {
            CaptionBottomSheet.newInstance(object :CaptionBottomSheet.Callbacks{
                override fun onDone(value: String) {
                    binding?.captionLayout?.etInput?.setText(value)
                }
            }).show(supportFragmentManager, CaptionBottomSheet::class.java.name)
        }

        binding?.ivCloseEditing?.setOnClickListener {
            DeleteDraftBottomSheet.newInstance(object :DeleteDraftBottomSheet.Callbacks{
                override fun onDelete() {
                    finish()
                }
            }).show(supportFragmentManager, DeleteDraftBottomSheet::class.java.name)
        }

        binding?.tvPreviewAndPost?.setOnClickListener {
            saveUpdatePost()
        }
    }

    fun saveKeyValue(){

        viewModel.saveKeyValue(sessionLocal?.fPID,
        sessionLocal?.fpTag, arrayListOf(posterModel?.id!!), hashMapOf("test" to "test")
        ).observe(this,{
            if (it.isSuccess()){

            }
        })
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

    private fun saveUpdatePost() {
        showProgress()
        WebEngageController.trackEvent(POST_AN_UPDATE, EVENT_LABEL_NULL, sessionLocal?.fpTag)
        var socialShare = ""
     /*   if (fbStatusEnabled.value == true) socialShare += "FACEBOOK."
        if (fbPageStatusEnable.value == true) socialShare += "FACEBOOK_PAGE."
        if (twitterSharingEnabled.value == true) socialShare += "TWITTER."*/
        val merchantId = if (sessionLocal?.iSEnterprise == "true") null else sessionLocal?.fPID
        val parentId = if (sessionLocal?.iSEnterprise == "true") sessionLocal?.fPParentId else null
        val request = PostUpdateTaskRequest(
            clientId,
            binding!!.captionLayout.etInput.text.toString(),
            true,
            merchantId,
            parentId,
            true,
            socialShare
        )

//        viewModel.putBizMessageUpdate(request).observeOnce(this, {
//            if (it.isSuccess() && it.stringResponse.isNullOrEmpty().not()) {
//
//                lifecycleScope.launch {
//                    val bodyImage = SvgUtils.svgToBitmap(posterModel!!)?.saveAsTempFile()?.asRequestBody("image/*".toMediaTypeOrNull())
//                    val s_uuid = UUID.randomUUID().toString().replace("-", "")
//                    viewModel.putBizImageUpdate(
//                        clientId, "sequential", s_uuid, 1, 1,
//                        socialShare, it.stringResponse, true, bodyImage
//                    ).observeOnce(this@EditPostActivity, { it1 ->
//                        if (it1.isSuccess()) {
//                            // successResult()
//                        } else showShortToast("Image uploading error, please try again.")
//
//                        hideProgress()
//                    })
//                }
//
//                }
//             else {
//                 hideProgress()
//                showShortToast("Post updating error, please try again.")
//            }
//        })
    }

//    private fun getRequestBizImage(): RequestBody {
//        SvgUtils
//        val responseBody = postImage?.readBytes()?.let { it.toRequestBody("image/png".toMediaTypeOrNull(), 0, it.size) }
//
//        return responseBody!!
//    }

}