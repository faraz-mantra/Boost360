package com.appservice.staffs.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
public class ToggleableView extends View {
    protected int width;
    protected int height;
   
    protected boolean isOn;

    protected boolean enabled;

    protected LabeledToggle.OnToggledListener onToggledListener;

    public ToggleableView(Context context) {
        super(context);
    }

    public ToggleableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

 
    public ToggleableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * <p>Returns the boolean state of this Switch.</p>
     *
     * @return true if the switch is on, false if it is off.
     */
    public boolean isOn() {
        return isOn;
    }

    /**
     * <p>Changes the boolean state of this Switch.</p>
     *
     * @param on true to turn switch on, false to turn it off.
     */
    public void setOn(boolean on) {
        isOn = on;
    }

    /**
     * Returns the enabled status for this switch. The interpretation of the
     * enabled state varies by subclass.
     *
     * @return True if this switch is enabled, false otherwise.
     */
    @ViewDebug.ExportedProperty
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the enabled state of this switch. The interpretation of the enabled
     * state varies by subclass.
     *
     * @param enabled True if this view is enabled, false otherwise.
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Register a callback to be invoked when the boolean state of switch is changed. If this switch is not
     * enabled, there won't be any event.
     *
     * @param onToggledListener The callback that will run
     *
     * @see #setEnabled(boolean)
     */
    public void setOnToggledListener(LabeledToggle.OnToggledListener onToggledListener) {
        this.onToggledListener = onToggledListener;
    }
}
