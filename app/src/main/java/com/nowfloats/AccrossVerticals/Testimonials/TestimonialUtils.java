package com.nowfloats.AccrossVerticals.Testimonials;

import android.view.View;

import com.nowfloats.AccrossVerticals.API.model.GetTestimonials.TestimonialData;

public class TestimonialUtils {

    public static int isReviewSecondValue(String experienceCode) {
        switch (experienceCode) {
            case "DOC":
            case "HOS":
            case "CAF":
            case "EDU":
            case "SVC":
            case "RTL":
                return View.GONE;
            case "HOT":
            case "MFG":
            case "SPA":
            case "SAL":
            default:
                return View.VISIBLE;
        }
    }

    public static String getReviewSecondValue(TestimonialData data, String experienceCode) {
        switch (experienceCode) {
            case "HOT":
                return data.getCity();
            case "MFG":
            case "SPA":
            case "SAL":
                return data.getTitle();
            case "DOC":
            case "HOS":
            case "CAF":
            case "EDU":
            case "SVC":
            case "RTL":
            default:
                return "";
        }
    }

    public static String getReviewSecondTitle(String experienceCode) {
        switch (experienceCode) {
            case "HOT":
                return "Review City";
            case "MFG":
            case "SPA":
            case "SAL":
            default:
                return "Review Title";
        }
    }

    public static String getDescTitle(String experienceCode) {
        switch (experienceCode) {
            case "DOC":
            case "HOS":
                return "Your Review";
            case "SAL":
                return "Review Story";
            case "HOT":
                return "Review Testimonial";
            case "CAF":
                return "Review Text";
            case "MFG":
            case "EDU":
            case "SPA":
            case "SVC":
            case "RTL":
            default:
                return "Review Description";
        }
    }

    public static String getProfileDescValue(TestimonialData data, String experienceCode) {
        switch (experienceCode) {
            default:
                return data.getProfileimage().getDescription();

        }
    }

    public static int isProfileDescShow(String experienceCode) {
        switch (experienceCode) {
            default:
                return View.VISIBLE;
        }
    }

    public static String getTitleProfileDesc(String experienceCode) {
        switch (experienceCode) {
            case "DOC":
            case "HOS":
                return "Occupation";
            default:
                return "Description";
        }
    }

    public static int isProfileDescFill(String experienceCode) {
        switch (experienceCode) {
            case "DOC":
            case "HOS":
                return View.VISIBLE;
            default:
                return View.GONE;
        }
    }
}
