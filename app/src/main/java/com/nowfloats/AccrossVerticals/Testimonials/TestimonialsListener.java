package com.nowfloats.AccrossVerticals.Testimonials;


import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.TestimonialData;

public interface TestimonialsListener {

    void itemMenuOptionStatus(int pos, boolean status);

    void editOptionClicked(TestimonialData data);

    void deleteOptionClicked(TestimonialData data);

    void shareOptionClicked(TestimonialData data);
}
