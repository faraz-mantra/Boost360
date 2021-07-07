package nfkeyboard.interface_contracts;

import android.os.Bundle;

/**
 * Created by Admin on 27-02-2018.
 */

public interface SpeechRecognitionResultInterface {

    void onResult(Bundle b);

    void onReadyToSpeech(Bundle b);

    void onError(int i);
}
