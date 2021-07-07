package com.nowfloats.manufacturing.projectandteams.Interfaces;


import com.nowfloats.manufacturing.API.model.GetTeams.Data;

public interface TeamsActivityListener {

    void itemMenuOptionStatus(int pos, boolean status);

    void editOptionClicked(Data data);

    void deleteOptionClicked(Data data);

}
