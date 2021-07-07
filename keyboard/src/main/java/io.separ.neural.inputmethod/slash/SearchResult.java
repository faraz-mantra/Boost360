package io.separ.neural.inputmethod.slash;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by sepehr on 3/2/17.
 */
public class SearchResult {
    @SerializedName("results")
    public ArrayList<RSearchItem> results;
    @SerializedName("meta")
    private Meta meta;

    public SearchResult() {
    }

    public SearchResult(boolean x) {
        meta = new Meta();
        meta.status = null;
        this.results = new ArrayList<>();
    }

    public ArrayList<RSearchItem> getItems() {
        return this.results;
    }

    public String getMetaStatus() {
        if (this.meta != null) {
            return this.meta.status;
        }
        return null;
    }

    public static class Meta {
        public String status;
    }
}
