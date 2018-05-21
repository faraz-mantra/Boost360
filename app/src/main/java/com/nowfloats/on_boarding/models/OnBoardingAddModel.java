package com.nowfloats.on_boarding.models;

/**
 * Created by Admin on 22-03-2018.
 */

public class OnBoardingAddModel {

    public OnBoardingStepsModel ActionData;

    public String WebsiteId;

    public String getWebsiteId() {
        return WebsiteId;
    }

    public void setWebsiteId(String websiteId) {
        WebsiteId = websiteId;
    }

    public OnBoardingStepsModel getActionData() {
        return ActionData;
    }

    public void setActionData(OnBoardingStepsModel actionData) {
        ActionData = actionData;
    }
}
