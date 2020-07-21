package com.appservice.views.toggle.interfaces;

import com.appservice.views.toggle.model.ToggleableView;

public interface OnToggledListener {

    void onSwitched(ToggleableView toggleableView, boolean isOn);
}
