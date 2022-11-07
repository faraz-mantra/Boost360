package com.framework.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.framework.BaseApplication
import com.framework.R
import com.framework.analytics.SentryController
import java.util.*

class STTUtils(val callback:Callbacks?) {

    interface Callbacks{
        fun onDone(text:String?)
    }

    private var sttResultLauncher : ActivityResultLauncher<Intent>?=null

    fun init(fragment: Fragment){
        sttResultLauncher= fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            val data = result?.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            callback?.onDone(data?.firstOrNull()?.toString())
        }
    }

    fun init(activity: ComponentActivity){
        sttResultLauncher= activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
            val data = result?.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            callback?.onDone(data?.firstOrNull()?.toString())
        }
    }
    fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, BaseApplication.instance.getString(R.string.speech_prompt))
        try {
            sttResultLauncher?.launch(intent)
        } catch (a: ActivityNotFoundException) {
            showToast(BaseApplication.instance.getString(R.string.speech_not_supported))
            SentryController.captureException(a)
        }
    }


}