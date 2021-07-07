package com.nowfloats.NavigationDrawer.model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RiaCardModel {

    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("HeaderText")
    @Expose
    private String headerText;
    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Emotion")
    @Expose
    private String emotion;
    @SerializedName("TimeoutInMs")
    @Expose
    private Long timeoutInMs;
    @SerializedName("NodeType")
    @Expose
    private String nodeType;
    @SerializedName("Sections")
    @Expose
    private List<Section> sections = null;
    @SerializedName("Buttons")
    @Expose
    private List<Button> buttons = null;
    @SerializedName("DisplayType")
    @Expose
    private String displayType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public Long getTimeoutInMs() {
        return timeoutInMs;
    }

    public void setTimeoutInMs(Long timeoutInMs) {
        this.timeoutInMs = timeoutInMs;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Button> getButtons() {
        return buttons;
    }

    public void setButtons(List<Button> buttons) {
        this.buttons = buttons;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

}