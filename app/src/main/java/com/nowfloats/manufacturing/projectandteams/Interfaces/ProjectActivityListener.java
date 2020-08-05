package com.nowfloats.manufacturing.projectandteams.Interfaces;


import com.nowfloats.manufacturing.API.model.GetProjects.Data;

public interface ProjectActivityListener {

    void itemMenuOptionStatus(int pos, boolean status);

    void editOptionClicked(Data data);

    void deleteOptionClicked(Data data);

}
