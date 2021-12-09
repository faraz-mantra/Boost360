package com.festive.poster.ui.promoUpdates.bottomSheet

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.festive.poster.R
import com.festive.poster.databinding.BsheetAddCaptionBinding
import com.festive.poster.databinding.BsheetEditPostBinding
import com.framework.analytics.SentryController
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.STTUtils
import java.util.*
import com.framework.views.customViews.CustomEditText
import java.lang.Exception
import java.lang.RuntimeException

class CaptionBottomSheet : BaseBottomSheetDialog<BsheetAddCaptionBinding, BaseViewModel>() {


    private var sttResultLauncher :ActivityResultLauncher<Intent>?=null
    private var callbacks:CaptionBottomSheet.Callbacks?=null
    private var mSpannable: Spannable? = null
    private var hashTagIsComing = 0
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
        @JvmStatic
        fun newInstance(callbacks: Callbacks): CaptionBottomSheet {
            val bundle = Bundle().apply {}
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

    override fun onCreateView() {

        sttResultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            val data = result?.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding?.captionLayout?.etInput?.append((data?.get(0)?.toString() ?: "") + ". ")
        }
        binding?.ivVoiceOver?.setOnClickListener {
            promptSpeechInput()
        }
        binding?.rivCloseBottomSheet?.setOnClickListener {
            dismiss()
        }

        binding?.tvDoneCaption?.setOnClickListener {
            callbacks?.onDone(binding!!.captionLayout.etInput.text.toString())
            dismiss()
        }
        addHashTagFunction()
        //handle()
    }

    private fun addHashTagFunction() {
        mSpannable = binding?.captionLayout?.etInput?.text

        binding?.captionLayout?.etInput?.addTextChangedListener(object : TextWatcher{
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
        mSpannable?.setSpan(ForegroundColorSpan(ContextCompat.getColor(baseActivity, R.color.black_4a4a4a)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        mSpannable?.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt))
        try {
            sttResultLauncher?.launch(intent)
        } catch (a: ActivityNotFoundException) {
            showShortToast(getString(R.string.speech_not_supported))
            SentryController.captureException(a)
        }
    }


}