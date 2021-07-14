package com.nowfloats.customerassistant.callbacks;

import java.util.ArrayList;

/**
 * Created by Admin on 11-10-2017.
 */

public interface ThirdPartyCallbacks {

    void addSuggestions(int type, ArrayList<Integer> positions);
}
