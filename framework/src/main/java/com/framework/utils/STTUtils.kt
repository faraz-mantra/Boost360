package com.framework.utils

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.framework.BaseApplication

object STTUtils {


    var intent= Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    var recognizer: SpeechRecognizer = SpeechRecognizer
        .createSpeechRecognizer(BaseApplication.instance)
    var listener: RecognitionListener = object : RecognitionListener {

        override fun onResults(results: Bundle) {

            val voiceResults= results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (voiceResults == null) {
                println("No voice results")
            } else {
                println("Printing matches: ")
                for (match in voiceResults) {
                    println(match)
                }
            }
        }

        override fun onReadyForSpeech(params: Bundle?) {
            println("Ready for speech")
        }

        /**
         * ERROR_NETWORK_TIMEOUT = 1;
         * ERROR_NETWORK = 2;
         * ERROR_AUDIO = 3;
         * ERROR_SERVER = 4;
         * ERROR_CLIENT = 5;
         * ERROR_SPEECH_TIMEOUT = 6;
         * ERROR_NO_MATCH = 7;
         * ERROR_RECOGNIZER_BUSY = 8;
         * ERROR_INSUFFICIENT_PERMISSIONS = 9;
         *
         * @param error code is defined in SpeechRecognizer
         */
        override fun onError(error: Int) {
            System.err.println("Error listening for speech: $error")
        }

        override fun onBeginningOfSpeech() {
            println("Speech starting")
        }

        override fun onBufferReceived(buffer: ByteArray?) {
        }

        override fun onEndOfSpeech() {
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
        }

        override fun onPartialResults(partialResults: Bundle?) {
        }

        override fun onRmsChanged(rmsdB: Float) {
        }
    }

    fun startListing(){
        recognizer.setRecognitionListener(listener);
        recognizer.startListening(intent);
    }
}