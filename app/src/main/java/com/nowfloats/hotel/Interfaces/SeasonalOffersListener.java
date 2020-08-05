package com.nowfloats.hotel.Interfaces;


import com.nowfloats.hotel.API.model.GetOffers.Data;

public interface SeasonalOffersListener {
    void itemMenuOptionStatus(int pos, boolean status);

    void editOptionClicked(Data data);

    void deleteOptionClicked(Data data);
}
