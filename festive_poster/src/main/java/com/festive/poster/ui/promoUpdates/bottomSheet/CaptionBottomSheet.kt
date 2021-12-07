package com.festive.poster.ui.promoUpdates.bottomSheet

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.festive.poster.R
import com.festive.poster.databinding.BsheetAddCaptionBinding
import com.festive.poster.databinding.BsheetEditPostBinding
import com.framework.analytics.SentryController
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.STTUtils
import java.util.*

class CaptionBottomSheet : BaseBottomSheetDialog<BsheetAddCaptionBinding, BaseViewModel>() {


    private var sttResultLauncher :ActivityResultLauncher<Intent>?=null
    private var callbacks:CaptionBottomSheet.Callbacks?=null
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