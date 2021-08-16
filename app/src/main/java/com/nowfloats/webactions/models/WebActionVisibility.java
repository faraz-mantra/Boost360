package com.nowfloats.webactions.models;

import android.util.SparseArray;


/**
 * Created by NowFloats on 09-04-2018.
 */

public enum WebActionVisibility {
    PUBLIC(0),
    PRIVATE(1),
    NONE(-1);

    private static SparseArray<WebActionVisibility> map = new SparseArray<>();

    static {
        for (WebActionVisibility visibility : WebActionVisibility.values()) {
            map.put(visibility.value, visibility);
        }
    }

    private int value;

    private WebActionVisibility(int value) {
        this.value = value;
    }

    public static WebActionVisibility valueOf(int pageType) {
        return map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
