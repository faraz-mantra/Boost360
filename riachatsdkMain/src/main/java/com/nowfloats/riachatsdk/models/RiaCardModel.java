package com.nowfloats.riachatsdk.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RiaCardModel implements Serializable{

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
    private Long timeoutInMs=-1L;
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
    @SerializedName("VariableName")
    @Expose
    private String variableName;
    @SerializedName("ApiMethod")
    @Expose
    private String apiMethod;
    @SerializedName("ApiUrl")
    @Expose
    private String apiUrl;
    @SerializedName("CardHeader")
    @Expose
    private String cardHeader;
    @SerializedName("CardFooter")
    @Expose
    private String cardFooter;
    @SerializedName("Placement")
    @Expose
    private String placement;
    @SerializedName("ApiType")
    @Expose
    private String apiType;
    @SerializedName("NextNodeId")
    @Expose
    private String nextNodeId;
    @SerializedName("RequiredVariables")
    @Expose
    private List<String> requiredVariables = null;

    private boolean isToShowInput = true;

    public boolean isToShowInput() {
        return isToShowInput;
    }

    public void setToShowInput(boolean toShowInput) {
        isToShowInput = toShowInput;
    }

    public String getNextNodeId() {
        return nextNodeId;
    }

    public void setNextNodeId(String nextNodeId) {
        this.nextNodeId = nextNodeId;
    }

    public String getCardHeader() {
        return cardHeader;
    }

    public void setCardHeader(String cardHeader) {
        this.cardHeader = cardHeader;
    }

    public String getCardFooter() {
        return cardFooter;
    }

    public void setCardFooter(String cardFooter) {
        this.cardFooter = cardFooter;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

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

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public List<String> getRequiredVariables() {
        return requiredVariables;
    }

    public void setRequiredVariables(List<String> requiredVariables) {
        this.requiredVariables = requiredVariables;
    }
}