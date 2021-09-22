package io.separ.neural.inputmethod.slash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sepehr on 3/2/17.
 */
public class SearchResultsEvent {
    public final String query;
    public String authorizedStatus;
    private List<RSearchItem> items;
    private String source;

    public SearchResultsEvent(String query, String source, RSearchItem item) {
        this.items = new ArrayList();
        this.items.add(item);
        this.source = source;
        this.query = query;
    }

    public SearchResultsEvent(String query, String source, List<RSearchItem> list, String authorizedStatus) {
        this.items = list;
        this.source = source;
        this.query = query;
        this.authorizedStatus = authorizedStatus;
    }

    public List<RSearchItem> getItems() {
        return this.items;
    }

    public String getSource() {
        return this.source;
    }

    public String getAuthorizedStatus() {
        return this.authorizedStatus;
    }
}