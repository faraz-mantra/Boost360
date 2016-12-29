package com.nowfloats.Analytics_Screen.Graph.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Abhi on 11/8/2016.
 */

public class DashboardResponse {

    @SerializedName("ErrorList")
    @Expose
    private List<Object> errorList = new ArrayList<Object>();
    @SerializedName("OperationStatus")
    @Expose
    private Boolean operationStatus;
    @SerializedName("Entity")
    @Expose
    private List<Entity> entity = new ArrayList<Entity>();
    @SerializedName("ReferenceId")
    @Expose
    private Object referenceId;

    /**
     *
     * @return
     * The errorList
     */
    public List<Object> getErrorList() {
        return errorList;
    }

    /**
     *
     * @param errorList
     * The ErrorList
     */
    public void setErrorList(List<Object> errorList) {
        this.errorList = errorList;
    }

    /**
     *
     * @return
     * The operationStatus
     */
    public Boolean getOperationStatus() {
        return operationStatus;
    }

    /**
     *
     * @param operationStatus
     * The OperationStatus
     */
    public void setOperationStatus(Boolean operationStatus) {
        this.operationStatus = operationStatus;
    }

    /**
     *
     * @return
     * The entity
     */
    public List<Entity> getEntity() {
        return entity;
    }

    /**
     *
     * @param entity
     * The Entity
     */
    public void setEntity(List<Entity> entity) {
        this.entity = entity;
    }

    /**
     *
     * @return
     * The referenceId
     */
    public Object getReferenceId() {
        return referenceId;
    }

    /**
     *
     * @param referenceId
     * The ReferenceId
     */
    public void setReferenceId(Object referenceId) {
        this.referenceId = referenceId;
    }

    public class Entity {

        @SerializedName("CreatedDate")
        @Expose
        private String createdDate;
        @SerializedName("DataCount")
        @Expose
        private Integer dataCount;

        /**
         *
         * @return
         * The createdDate
         */
        public String getCreatedDate() {
            return createdDate;
        }

        /**
         *
         * @param createdDate
         * The CreatedDate
         */
        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        /**
         *
         * @return
         * The dataCount
         */
        public Integer getDataCount() {
            return dataCount;
        }

        /**
         *
         * @param dataCount
         * The DataCount
         */
        public void setDataCount(Integer dataCount) {
            this.dataCount = dataCount;
        }

    }
}
