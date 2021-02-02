package com.newfloats.staffs.ui.details.timing.model;

import java.util.ArrayList;

public class StaffTimingModel {
    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTurnedOn() {
        return isTurnedOn;
    }

    public void setTurnedOn(boolean turnedOn) {
        isTurnedOn = turnedOn;
    }

    public boolean isAppliedOnAllDays() {
        return isAppliedOnAllDays;
    }

    public void setAppliedOnAllDays(boolean appliedOnAllDays) {
        isAppliedOnAllDays = appliedOnAllDays;
    }

    public boolean isAppliedOnAllDaysViewVisible() {
        return isAppliedOnAllDaysViewVisible;
    }

    public void setAppliedOnAllDaysViewVisible(boolean appliedOnAllDaysViewVisible) {
        isAppliedOnAllDaysViewVisible = appliedOnAllDaysViewVisible;
    }

    public ArrayList<SessionModel> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<SessionModel> sessions) {
        this.sessions = sessions;
    }

    boolean isTurnedOn;
    boolean isAppliedOnAllDays;
    boolean isAppliedOnAllDaysViewVisible;
    ArrayList<SessionModel> sessions = new ArrayList<>();

    public static ArrayList<StaffTimingModel> getDefaultTimings() {
        ArrayList<StaffTimingModel> list = new ArrayList<>();
        list.add(getDefaultModel("Monday", false, true));
        list.add(getDefaultModel("Tuesday", false, false));
        list.add(getDefaultModel("Wednesday", false, false));
        list.add(getDefaultModel("Thursday", false, false));
        list.add(getDefaultModel("Friday", false, false));
        list.add(getDefaultModel("Saturday", false, false));
        list.add(getDefaultModel("Sunday", false, false));
        return list;
    }
    public void onDayTurnedOn() {
        this.isTurnedOn = true;
        this.sessions = new ArrayList<>();
        this.sessions.add(new SessionModel());
    }

    public void onDayTurnedOff() {
        this.isTurnedOn = false;
        this.sessions.clear();
    }

    public void addSession() {
        this.sessions.add(new SessionModel());
    }

    public void deleteSession(int sessionIndex) {
        this.sessions.remove(sessionIndex);
    }

    public static StaffTimingModel getDefaultModel(String title, boolean isAppliedOnAllDays, boolean isAppliedOnAllDaysVisible) {
        StaffTimingModel m = new StaffTimingModel();
        m.title = title;
        m.isTurnedOn = false;
        m.isAppliedOnAllDays = isAppliedOnAllDays;
        m.isAppliedOnAllDaysViewVisible = isAppliedOnAllDaysVisible;
        return m;
    }

}

class SessionModel {
    String fromTime;
    String toTime;
}