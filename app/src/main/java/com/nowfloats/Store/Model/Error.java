package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Error {

    @SerializedName("ErrorCode")
    @Expose
    private Integer errorCode;
    @SerializedName("ErrorList")
    @Expose
    private List<OPCErrorModel> errorList = null;

    /**
     * @return The errorCode
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode The ErrorCode
     */
    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return The errorList
     */
    public List<OPCErrorModel> getErrorList() {
        return errorList;
    }

    /**
     * @param errorList The ErrorList
     */
    public void setErrorList(List<OPCErrorModel> errorList) {
        this.errorList = errorList;
    }

}