package com.nowfloats.swipecard.models;

import java.util.List;

/**
 * Created by NowFloats on 4/28/2017.
 */

public class SMSSuggestions {
    public List<SuggestionsDO> suggestion = null;

    public List<SuggestionsDO> getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(List<SuggestionsDO> suggestion) {
        this.suggestion = suggestion;
    }
}
