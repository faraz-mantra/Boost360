package com.nowfloats.manufacturing.projectandteams.ui.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.manufacturing.API.ManufacturingAPIInterfaces;
import com.nowfloats.manufacturing.API.model.DeleteProject.DeleteProjectData;
import com.nowfloats.manufacturing.API.model.GetProjects.Data;
import com.nowfloats.manufacturing.API.model.GetProjects.GetProjectsData;
import com.nowfloats.manufacturing.projectandteams.Interfaces.ProjectActivityListener;
import com.nowfloats.manufacturing.projectandteams.adapter.ProjectAdapter;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

public class ProjectActivity extends AppCompatActivity implements ProjectActivityListener {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    UserSessionManager session;
    List<Data> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_category);

        initView();
    }

    public void initView() {

        session = new UserSessionManager(this, this);
        recyclerView = findViewById(R.id.recycler);
        adapter = new ProjectAdapter(new ArrayList(), this, this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);

        //setHeader
        setHeader();

    }

    void loadData() {
        try {
            JSONObject query = new JSONObject();
            query.put("WebsiteId", session.getFpTag());
            ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(ManufacturingAPIInterfaces.class);

            APICalls.getProjectsList(query, 0, 1000, new Callback<GetProjectsData>() {
                @Override
                public void success(GetProjectsData getProjectsData, Response response) {
                    if (getProjectsData == null || response.getStatus() != 200) {
                        Toast.makeText(ProjectActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }


                    dataList = getProjectsData.getData();
                    if (dataList.size() > 0) {
                        updateRecyclerView();
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Methods.showSnackBarNegative(ProjectActivity.this, getString(R.string.something_went_wrong));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.isNetworkConnected(this)) {
            loadData();
        }else{
            Methods.showSnackBarNegative(ProjectActivity.this, getString(R.string.no_internet_connection));
        }
    }

    private void updateRecyclerView() {
        adapter.menuOption(-1, false);
        adapter.updateList(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void itemMenuOptionStatus(int pos, boolean status) {
        adapter.menuOption(pos, status);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void editOptionClicked(Data data) {
        adapter.menuOption(-1, false);
        adapter.notifyDataSetChanged();
        Intent projectIntent = new Intent(ProjectActivity.this, ProjectDetailsActivity.class);
        projectIntent.putExtra("ScreenState", "edit");
        projectIntent.putExtra("data", new Gson().toJson(data));
        startActivity(projectIntent);
    }

    @Override
    public void deleteOptionClicked(Data data) {
        adapter.menuOption(-1, false);
        adapter.notifyDataSetChanged();
        try {
            DeleteProjectData requestBody = new DeleteProjectData();
            requestBody.setQuery("{_id:'" + data.getId() + "'}");
            requestBody.setUpdateValue("{$set : {IsArchived: true }}");
            requestBody.setMulti(true);

            ManufacturingAPIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("https://webaction.api.boostkit.dev")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .setConverter(new GsonConverter(new GsonBuilder().setLenient().create()))
                    .build()
                    .create(ManufacturingAPIInterfaces.class);

            APICalls.deleteProjectData(requestBody, new Callback<String>() {
                @Override
                public void success(String data, Response response) {
                    if (response != null && response.getStatus() == 200) {
                        Log.d("deletePlacesAround ->", response.getBody().toString());
                        Methods.showSnackBarPositive(ProjectActivity.this,  getString(R.string.successfully_deleted_));
                        loadData();
                    } else {
                        Methods.showSnackBarNegative(ProjectActivity.this, getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if(error.getResponse().getStatus() == 200){
                        Methods.showSnackBarPositive(ProjectActivity.this,  getString(R.string.successfully_deleted_));
                        loadData();
                    }else {
                        Methods.showSnackBarNegative(ProjectActivity.this, getString(R.string.something_went_wrong));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setHeader() {
        LinearLayout rightButton, backButton;
        ImageView rightIcon;
        TextView title;

        title = findViewById(R.id.title);
        backButton = findViewById(R.id.back_button);
        rightButton = findViewById(R.id.right_icon_layout);
        rightIcon = findViewById(R.id.right_icon);
        title.setText("Projects");
        rightIcon.setImageResource(R.drawable.ic_add_white);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent projectIntent = new Intent(ProjectActivity.this, ProjectDetailsActivity.class);
                projectIntent.putExtra("ScreenState", "new");
                startActivity(projectIntent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}