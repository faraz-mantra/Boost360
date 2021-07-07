package com.android.inputmethod.keyboard.top.services.tenor;

import java.util.ArrayList;

import io.separ.neural.inputmethod.slash.SearchResult;

/**
 * Created by sepehr on 3/4/17.
 */
public class TenorResponse {
    public ArrayList<TenorResult> results;
    private String next;

    public SearchResult toSearchResult() {
        SearchResult x = new SearchResult(true);
        for (TenorResult res : results)
            x.results.add(res.toSearchItem());
        return x;
    }
}
