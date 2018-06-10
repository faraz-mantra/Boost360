package nfkeyboard.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import nfkeyboard.interface_contracts.SpeechRecognitionResultInterface;

/**
 * Created by Admin on 27-02-2018.
 */

public class SpeechRecognitionManager implements RecognitionListener {
    private static final String LOG_TAG = "ggg";
    private final Context mContext;
    private SpeechRecognizer speechRecognizer;
    private Intent recognizerIntent;
    private SpeechRecognitionResultInterface mSpeechListener;
    private boolean isListening;

    public SpeechRecognitionManager(Context context, SpeechRecognitionResultInterface listener){
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);
        mContext = context;
        mSpeechListener = listener;
    }

    public void startListening(){
        if (recognizerIntent == null) {
            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, mContext.getPackageName());
            //recognizerIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
            recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        }
        if (isListening){
            stopListening();
        }
        isListening = true;
        speechRecognizer.startListening(recognizerIntent);
    }

    public boolean isListening(){
        return isListening;
    }
    public void stopListening(){
        if (isListening){
            speechRecognizer.stopListening();
        }
        isListening = false;

    }
    public void onCancel(){
        isListening = false;
        speechRecognizer.destroy();
    }
    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech");
        mSpeechListener.onReadyToSpeech(bundle);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        mSpeechListener.onError(errorCode);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }


    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        mSpeechListener.onResult(results);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }
}
