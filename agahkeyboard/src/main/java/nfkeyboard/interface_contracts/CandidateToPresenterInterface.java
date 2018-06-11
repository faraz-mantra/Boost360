package nfkeyboard.interface_contracts;

import hani.momanii.supernova_emoji_library.Helper.EmojiconGridView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconsPopup;
import nfkeyboard.keyboards.ImePresenterImpl;

/**
 * Created by Admin on 28-02-2018.
 */

public interface CandidateToPresenterInterface extends ItemClickListener, EmojiconGridView.OnEmojiconClickedListener,
        EmojiconsPopup.OnEmojiconBackspaceClickedListener {

    ImePresenterImpl.TabType getTabType();

    void onSpeechResult(String speech);
}
