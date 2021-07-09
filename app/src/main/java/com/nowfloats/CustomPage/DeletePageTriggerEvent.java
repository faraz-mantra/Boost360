package com.nowfloats.CustomPage;

import android.view.View;

/**
 * Created by NowFloatsDev on 23/10/2015.
 */
public class DeletePageTriggerEvent {
    public int position;
    public boolean b;
    View v;
    public DeletePageTriggerEvent(int pOs, boolean b, View v) {
        this.position = pOs;
        this.b = b;
        this.v = v;
    }
}
