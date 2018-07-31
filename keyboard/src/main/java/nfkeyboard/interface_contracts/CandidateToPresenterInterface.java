package nfkeyboard.interface_contracts;

import nfkeyboard.keyboards.ImePresenterImpl;

/**
 * Created by Admin on 28-02-2018.
 */

public interface CandidateToPresenterInterface extends ItemClickListener{

    ImePresenterImpl.TabType getTabType();

    void onSpeechResult(String speech);

    boolean imagesSupported();

    String packageName();
}
