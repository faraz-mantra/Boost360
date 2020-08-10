package com.nowfloats.manufacturing.digitalbrochures.Interfaces;


import com.nowfloats.manufacturing.API.model.GetBrochures.Data;

public interface DigitalBrochuresListener {

    void itemMenuOptionStatus(int pos,boolean status);

    void editOptionClicked(Data data);

    void deleteOptionClicked(Data data);
}
