package nowfloats.nfkeyboard.util;

import nowfloats.nfkeyboard.R;

/**
 * Created by Admin on 20-02-2018.
 */

public class KeyboardUtils {
    public enum KeyboardType{
        QWERTY_LETTERS, NUMBERS, SYMBOLS, SYMBOLS_SHIFT, EMOJIS;

        public static int getXml(KeyboardType type){
            switch (type){
                case NUMBERS:
                    return R.xml.keyboard_numbers;
                case SYMBOLS:
                    return  R.xml.keyboard_sym;
                case SYMBOLS_SHIFT:
                    return R.xml.keyboard_sym_shift;
                case EMOJIS:
                    return R.xml.keycodes_emojis;
                case QWERTY_LETTERS:
                default:
                    return  R.xml.keyboard_qwrty;
            }
        }
    }

    public enum CandidateType{
        TEXT_LIST, BOOST_SHARE, NULL;
        public static int getXml(CandidateType type){
            switch (type){
                case TEXT_LIST:
                    return R.layout.candidate_view;
                case BOOST_SHARE:
                default:
                    return R.layout.boost_share_candidate_view;
            }
        }
    }
}
