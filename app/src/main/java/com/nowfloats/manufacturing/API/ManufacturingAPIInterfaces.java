package com.nowfloats.manufacturing.API;

import com.nowfloats.manufacturing.API.model.AddProject.AddProjectData;
import com.nowfloats.manufacturing.API.model.AddTeams.AddTeamsData;
import com.nowfloats.manufacturing.API.model.DeleteProject.DeleteProjectData;
import com.nowfloats.manufacturing.API.model.DeleteTeams.DeleteTeamsData;
import com.nowfloats.manufacturing.API.model.GetProjects.GetProjectsData;
import com.nowfloats.manufacturing.API.model.GetTeams.GetTeamsData;
import com.nowfloats.manufacturing.API.model.UpdateProject.UpdateProjectData;
import com.nowfloats.manufacturing.API.model.UpdateTeams.UpdateTeamsData;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;

public interface ManufacturingAPIInterfaces {

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1"})
    @GET("/api/v1/projects/get-data")
    void getProjectsList(@Query("query") JSONObject query, @Query("skip") int skip, @Query("limit") int limit, Callback<GetProjectsData> response);

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1","Content-Type: application/json"})
    @POST("/api/v1/projects/add-data")
    void addProjectData(@Body AddProjectData body, Callback<String> response);

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1","Content-Type: application/json"})
    @POST("/api/v1/projects/update-data")
    void updateProjectData(@Body UpdateProjectData body, Callback<String> response);

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1","Content-Type: application/json"})
    @POST("/api/v1/projects/update-data")
    void deleteProjectData(@Body DeleteProjectData body, Callback<String> response);

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1"})
    @GET("/api/v1/myteam/get-data")
    void getTeamsList(@Query("query") JSONObject query, @Query("skip") int skip, @Query("limit") int limit, Callback<GetTeamsData> response);

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1","Content-Type: application/json"})
    @POST("/api/v1/myteam/add-data")
    void addTeamsData(@Body AddTeamsData body, Callback<String> response);

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1","Content-Type: application/json"})
    @POST("/api/v1/myteam/update-data")
    void updateTeamsData(@Body UpdateTeamsData body, Callback<String> response);

    @Headers({"Authorization: 59c89bbb5d64370a04c9aea1","Content-Type: application/json"})
    @POST("/api/v1/myteam/update-data")
    void deleteTeamsData(@Body DeleteTeamsData body, Callback<String> response);

}
