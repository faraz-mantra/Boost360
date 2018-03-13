package nowfloats.nfkeyboard.interface_contracts;

import hani.momanii.supernova_emoji_library.Helper.EmojiconGridView;
import hani.momanii.supernova_emoji_library.Helper.EmojiconsPopup;
import nowfloats.nfkeyboard.keyboards.ImePresenterImpl;

/**
 * Created by Admin on 28-02-2018.
 */

public interface CandidateToPresenterInterface extends ItemClickListener, EmojiconGridView.OnEmojiconClickedListener,
        EmojiconsPopup.OnEmojiconBackspaceClickedListener {

    void onScrollItems(int totalItemCount, int lastVisiblePos, ImePresenterImpl.TabType type);
    ImePresenterImpl.TabType getTabType();
    void onSpeechResult(String speech);
}
