package com.nowfloats.Store.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SalesmanModel implements Serializable {

    @SerializedName("division")
    @Expose
    private String division;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("employeeId")
    @Expose
    private String employeeId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("fosHandle")
    @Expose
    private String fosHandle;

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFosHandle() {
        return fosHandle;
    }

    public void setFosHandle(String fosHandle) {
        this.fosHandle = fosHandle;
    }

    @Override
    public String toString() {
        return employeeId + "";
    }
}