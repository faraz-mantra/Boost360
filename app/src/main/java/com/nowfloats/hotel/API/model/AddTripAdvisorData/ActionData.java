package com.nowfloats.hotel.API.model.AddTripAdvisorData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActionData {

    @SerializedName("show_widget")
    @Expose
    private Boolean showWidget;
    @SerializedName("widget_snippet")
    @Expose
    private String widgetSnippet;

    public Boolean getShowWidget() {
        return showWidget;
    }

    public void setShowWidget(Boolean showWidget) {
        this.showWidget = showWidget;
    }

    public String getWidgetSnippet() {
        return widgetSnippet;
    }

    public void setWidgetSnippet(String widgetSnippet) {
        this.widgetSnippet = widgetSnippet;
    }

}