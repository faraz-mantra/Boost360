package com.nowfloats.sam.models;

import java.util.List;

/**
 * Created by NowFloats on 4/28/2017.
 */

public class SMSSuggestions {
    public List<SuggestionsDO> suggestion = null;

    public List<SuggestionsDO> getSuggestionList() {
        return suggestion;
    }

    public void setSuggestionList(List<SuggestionsDO> suggestionList) {
        this.suggestion = suggestionList;
    }
}
