package com.nowfloats.manufacturing.API.model.AddProject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActionData {

    @SerializedName("project_title")
    @Expose
    private String projectTitle;
    @SerializedName("project_description")
    @Expose
    private String projectDescription;
    @SerializedName("project_clientName")
    @Expose
    private String projectClientName;
    @SerializedName("project_clientCategory")
    @Expose
    private String projectClientCategory;
    @SerializedName("project_completedOn")
    @Expose
    private String projectCompletedOn;
    @SerializedName("project_budget")
    @Expose
    private String projectBudget;
    @SerializedName("project_clientRequirement")
    @Expose
    private String projectClientRequirement;
    @SerializedName("project_whatWeDid")
    @Expose
    private String projectWhatWeDid;
    @SerializedName("project_Result")
    @Expose
    private String projectResult;
    @SerializedName("featuredImage")
    @Expose
    private FeaturedImage featuredImage;
    @SerializedName("project_image2")
    @Expose
    private ProjectImage2 projectImage2;
    @SerializedName("project_image3")
    @Expose
    private ProjectImage3 projectImage3;

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getProjectClientName() {
        return projectClientName;
    }

    public void setProjectClientName(String projectClientName) {
        this.projectClientName = projectClientName;
    }

    public String getProjectClientCategory() {
        return projectClientCategory;
    }

    public void setProjectClientCategory(String projectClientCategory) {
        this.projectClientCategory = projectClientCategory;
    }

    public String getProjectCompletedOn() {
        return projectCompletedOn;
    }

    public void setProjectCompletedOn(String projectCompletedOn) {
        this.projectCompletedOn = projectCompletedOn;
    }

    public String getProjectBudget() {
        return projectBudget;
    }

    public void setProjectBudget(String projectBudget) {
        this.projectBudget = projectBudget;
    }

    public String getProjectClientRequirement() {
        return projectClientRequirement;
    }

    public void setProjectClientRequirement(String projectClientRequirement) {
        this.projectClientRequirement = projectClientRequirement;
    }

    public String getProjectWhatWeDid() {
        return projectWhatWeDid;
    }

    public void setProjectWhatWeDid(String projectWhatWeDid) {
        this.projectWhatWeDid = projectWhatWeDid;
    }

    public String getProjectResult() {
        return projectResult;
    }

    public void setProjectResult(String projectResult) {
        this.projectResult = projectResult;
    }

    public FeaturedImage getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(FeaturedImage featuredImage) {
        this.featuredImage = featuredImage;
    }

    public ProjectImage2 getProjectImage2() {
        return projectImage2;
    }

    public void setProjectImage2(ProjectImage2 projectImage2) {
        this.projectImage2 = projectImage2;
    }

    public ProjectImage3 getProjectImage3() {
        return projectImage3;
    }

    public void setProjectImage3(ProjectImage3 projectImage3) {
        this.projectImage3 = projectImage3;
    }

}