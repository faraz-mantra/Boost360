package com.catlogservice.views.toggle.interfaces;

import com.catlogservice.views.toggle.model.ToggleableView;

public interface OnToggledListener {

    void onSwitched(ToggleableView toggleableView, boolean isOn);
}
