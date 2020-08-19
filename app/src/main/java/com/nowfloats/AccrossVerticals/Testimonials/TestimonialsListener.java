package com.nowfloats.AccrossVerticals.Testimonials;


import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.Data;

public interface TestimonialsListener {

    void itemMenuOptionStatus(int pos,boolean status);

    void editOptionClicked(Data data);

    void deleteOptionClicked(Data data);
}
