package com.nowfloats.CustomPage.Model;

public class UploadImageToS3Model {
    private String fileName;
    private String fileData;
    private int fileCategory;

    public int getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(int fileCategory) {
        this.fileCategory = fileCategory;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
