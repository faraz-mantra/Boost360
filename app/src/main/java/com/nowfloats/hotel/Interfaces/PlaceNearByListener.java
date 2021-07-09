package com.nowfloats.hotel.Interfaces;

import com.nowfloats.hotel.API.model.GetPlacesAround.Data;

public interface PlaceNearByListener {
    void itemMenuOptionStatus(int pos,boolean status);

    void editOptionClicked(Data data);

    void deleteOptionClicked(Data data);
}
