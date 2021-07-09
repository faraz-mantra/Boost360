package nfkeyboard.util;

import android.view.inputmethod.EditorInfo;

/**
 * Created by Admin on 20-02-2018.
 */

public class KeyboardUtils {
    public static final int IME_ACTION_CUSTOM_LABEL = EditorInfo.IME_MASK_ACTION + 1;

    public enum KeyboardType {
        QWERTY_LETTERS, EMAIL_ADDRESS, NUMBERS, SYMBOLS, SYMBOLS_SHIFT;

        public static int getXml(KeyboardType type) {
//            switch (type){
//                case NUMBERS:
//                    return R.xml.keyboard_numbers;
//                case SYMBOLS:
//                    return  nowfloats.nfkeyboard.R.xml.keyboard_sym;
//                case SYMBOLS_SHIFT:
//                    return nowfloats.nfkeyboard.R.xml.keyboard_sym_shift;
//                case EMAIL_ADDRESS:
//                    return nowfloats.nfkeyboard.R.xml.keyboard_email;
//                case QWERTY_LETTERS:
//                default:
//                    return  nowfloats.nfkeyboard.R.xml.keyboard_qwrty;
//            }
            return 0;
        }
    }

    public static int getImeOptionsActionIdFromEditorInfo(final EditorInfo editorInfo) {
        if ((editorInfo.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0) {
            //IME_FLAG_NO_ENTER_ACTION:
            // Flag of imeOptions: used in conjunction with one of the actions masked by IME_MASK_ACTION.
            // If this flag is not set, IMEs will normally replace the "enter" key with the action supplied.
            // This flag indicates that the action should not be available in-line as a replacement for the "enter" key.
            // Typically this is because the action has such a significant impact or is not recoverable enough
            // that accidentally hitting it should be avoided, such as sending a message.
            // Note that TextView will automatically set this flag for you on multi-line text views.
            return EditorInfo.IME_ACTION_NONE;
        } else if (editorInfo.actionLabel != null) {
            return IME_ACTION_CUSTOM_LABEL;
        } else {
            // Note: this is different from editorInfo.actionId, hence "ImeOptionsActionId"
            return editorInfo.imeOptions & EditorInfo.IME_MASK_ACTION;
        }
    }

    public enum CandidateType {
        TEXT_LIST, BOOST_SHARE, BOOST_SHARE1, NULL;

        public static int getXml(CandidateType type) {
            switch (type) {
//                case TEXT_LIST:
//                    return nowfloats.nfkeyboard.R.layout.text_suggestions_candidate_view;
//                case BOOST_SHARE1:
//                    return nowfloats.nfkeyboard.R.layout.boost_share_candidate_view1;
//                case BOOST_SHARE:
//                default:
//                    return nowfloats.nfkeyboard.R.layout.boost_share_candidate_view;
            }
            return 0;
        }
    }
}
