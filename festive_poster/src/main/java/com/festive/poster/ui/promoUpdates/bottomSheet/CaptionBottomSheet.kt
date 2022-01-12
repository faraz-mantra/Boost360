package com.festive.poster.ui.promoUpdates.bottomSheet

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.*
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.DialogFragment
import com.festive.poster.R
import com.festive.poster.databinding.BsheetAddCaptionBinding
import com.festive.poster.databinding.BsheetEditPostBinding
import com.framework.analytics.SentryController
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.models.BaseViewModel
import com.framework.utils.STTUtils
import com.framework.utils.highlightHashTag
import com.framework.utils.spanColor
import java.util.*
import com.framework.views.customViews.CustomEditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.Exception
import java.lang.RuntimeException

class CaptionBottomSheet : BaseBottomSheetDialog<BsheetAddCaptionBinding, BaseViewModel>() {


    private var sttUtils:STTUtils?=null
    private val TAG = "CaptionBottomSheet"
    private var callbacks:CaptionBottomSheet.Callbacks?=null
    private var mSpannable: Spannable? = null
    private var hashTagIsComing = 0
    private var captionText:String?=null
    /*private val mTextWatcher: TextWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
            if (text.isNotEmpty()) {
                eraseAndColorizeAllText(text)
            }
        }
        override fun afterTextChanged(s: Editable?) {}
    }*/

    companion object {
        val BK_CAPTION="BK_CAPTION"
        @JvmStatic
        fun newInstance(text:String,callbacks: Callbacks): CaptionBottomSheet {
            val bundle = Bundle().apply {}
            bundle.putString(BK_CAPTION,text)
            val fragment = CaptionBottomSheet()
            fragment.arguments = bundle
            fragment.callbacks=callbacks
            return fragment
        }
    }

    interface Callbacks{
        fun onDone(value:String)
    }

    override fun getLayout(): Int {
        return R.layout.bsheet_add_caption
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog =super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return dialog

    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

    }

    override fun onDismiss(dialog: DialogInterface) {

        super.onDismiss(dialog)


    }

    override fun onCreateView() {

        captionText = arguments?.getString(BK_CAPTION)
        binding!!.tvHashtagSubtitle.text = spanColor(
            getString(R.string.type_in_the_caption_to_create_your_own_hashtags),R.color.color395996,
            "#"
        )
        initStt()
        binding?.ivVoiceOver?.setOnClickListener {
            sttUtils?.promptSpeechInput()
        }
        binding?.rivCloseBottomSheet?.setOnClickListener {
            dismiss()
        }

        binding?.tvDoneCaption?.setOnClickListener {

            callbacks?.onDone(binding!!.captionLayout.etInput.text.toString())
            dismiss()
        }

        binding?.ivCloseHashtag?.setOnClickListener {
            binding!!.linearHash.gone()
        }
        addHashTagFunction()

        //handle()
    }

    private fun initStt() {
        sttUtils = STTUtils(object :STTUtils.Callbacks{
            override fun onDone(text: String?) {
                binding?.captionLayout?.etInput?.append((text ?: "") + ". ")

            }
        })
        sttUtils?.init(this)
    }

    private fun addHashTagFunction() {
        binding?.captionLayout?.etInput?.setText(highlightHashTag(captionText,R.color.black_4a4a4a))


        Log.i(TAG, "addHashTagFunction: "+"hi hello")
        mSpannable = binding?.captionLayout?.etInput?.text

        binding?.captionLayout?.etInput?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(short_text: CharSequence, start: Int, before: Int, count: Int) {

                val text = binding?.captionLayout?.etInput?.text.toString()
                var last_index = 0
                text.trim().split(Regex("\\s+")).forEach {
                    Log.i(TAG, "addHashTagFunction: $it")
                    if (it.isNotEmpty() && it[0] == '#'){
                        val boldSpan = StyleSpan(Typeface
                            .BOLD)
                        val foregroundSpan = ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.black))
                        mSpannable?.setSpan(foregroundSpan, text.indexOf(it,startIndex = last_index), text.indexOf(it,startIndex = last_index)+it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        mSpannable?.setSpan(boldSpan, text.indexOf(it,startIndex = last_index), text.indexOf(it,startIndex = last_index)+it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    }

                    last_index+=it.length-1

                }

            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun setCaption() {
        val text = captionText
        if (text.isNullOrEmpty().not()){
            var last_index = 0
            val spannable = SpannableString(text)
            text?.trim()?.split(Regex("\\s+"))?.forEach {
                Log.i(TAG, "addHashTagFunction: $it")
                if (it.isNotEmpty() && it[0] == '#'){
                    val foregroundSpan = ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
                    spannable.setSpan(foregroundSpan, text.indexOf(it,startIndex = last_index), text.indexOf(it,startIndex = last_index)+it.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                }

                last_index+=it.length-1

            }
            binding!!.captionLayout.etInput.setText(spannable)
        }

    }






}