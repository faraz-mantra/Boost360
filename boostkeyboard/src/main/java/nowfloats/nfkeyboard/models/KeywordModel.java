package nowfloats.nfkeyboard.models;

import java.io.Serializable;

public class KeywordModel implements Serializable {
    public static final String DICTIONARY_WORD = "DICTIONARY_WORD";
    public static final String NEW_WORD = "NEW_WORD";
    public static final String CORRECTED_WORD = "CORRECTED_WORD";
    private String word;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
