package com.nowfloats.Analytics_Screen.Graph;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by tushar on 18-05-2015.
 */


public class VisitCountParser {

    public ArrayList<Integer> parse(JSONObject jObject,ArrayList<String> dateArray){

        ArrayList<Integer> valArray = new ArrayList<Integer>();
        for(int i=0,l=dateArray.size(); i<l; i++)
            valArray.add(0);

        ArrayList<String> datesFound = new ArrayList<String>();
        datesFound.add("Start");

        JSONArray entityArray = null;
        try {
            /** Retrieves all the elements in the 'places' array */
            entityArray = jObject.getJSONArray("Entity");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int noOfEntries = entityArray.length();



            for (int i = 0; i < noOfEntries; i++)
            {
                String dateString = "";
                String countString ="";
                try {

                dateString = getDateString((JSONObject)entityArray.get(i));
                countString = getCountString((JSONObject)entityArray.get(i));

                }catch(Exception e){
                    Log.d("PARSE",e.getMessage());
                }

                String actualDate = (processDate(dateString));

                datesFound.add(actualDate + " " + countString);

               int index = dateArray.indexOf(actualDate);
               if(index != -1)
                valArray.set(index ,Integer.parseInt(countString));


            }

        return valArray;
    }


    private String getNextDate(String ds)
    {
        int year = Integer.parseInt(ds.substring(0,4));
        int month = Integer.parseInt(ds.substring(5,7));
        int day = Integer.parseInt(ds.substring(8,10));
        return "";

    }

    private String getDateString(JSONObject jObject)
    {
        String ds = "-";
        try {

            ds = jObject.getString("CreatedDate");
        }catch(Exception e){
            Log.d("PARSE","ERROR GETTING DATE STRING");
        }
        return ds;
    }

    private String getCountString(JSONObject jObject)
    {
        String cs = "-";
        try {

            cs = jObject.getString("DataCount");
        }catch(Exception e){
            Log.d("PARSE","ERROR GETTING COUNT STRING");
        }
        return cs;
    }

    private String getFormattedDate(String ds)
    {
        int year,month,day;
        int len = ds.length();

        year = Integer.parseInt(ds.substring(len-4,len));

        int k;
        if(ds.charAt(2) == ' ')
            k = 2;
        else
            k = 1;
        month = getMonthNumber(ds.substring(k+1,ds.indexOf(",")));
        day = Integer.parseInt(ds.substring(0,k));

        return String.format("%04d-%02d-%02d",year,month,day);
    }


    private int getMonthNumber(String s)
    {

        if(s.equalsIgnoreCase("January"))
            return 1;
        if(s.equalsIgnoreCase("February"))
            return 2;
        if(s.equalsIgnoreCase("March"))
            return 3;
        if(s.equalsIgnoreCase("April"))
            return 4;
        if(s.equalsIgnoreCase("May"))
            return 5;
        if(s.equalsIgnoreCase("June"))
            return 6;
        if(s.equalsIgnoreCase("July"))
            return 7;
        if(s.equalsIgnoreCase("August"))
            return 8;
        if(s.equalsIgnoreCase("September"))
            return 9;
        if(s.equalsIgnoreCase("October"))
            return 10;
        if(s.equalsIgnoreCase("November"))
            return 11;
        if(s.equalsIgnoreCase("December"))
            return 12;

        return 0;
    }
    private String processDate(String tDate) {
        String formatted = "";
        String dateTime = "";
        String[] temp = null;
        String sDate = tDate.replace("/Date(", "").replace(")/", "");
        String[] splitDate = sDate.split("\\+");

        Long epochTime = Long.parseLong(splitDate[0]);

        Date date = new Date(epochTime);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");// dd/MM/yyyy
        // HH:mm:ss
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        //format.setTimeZone(TimeZone.getTimeZone("Etc/UTC+05:30"));
        format.setTimeZone(tz);

        if (date != null)
            dateTime = format.format(date);
        if (!isNullOrEmpty(dateTime)) {
            temp = dateTime.split(" ");
            temp = temp[0].split("-");
        }
        if (temp.length > 0) {
            int month = Integer.parseInt(temp[1]);
            switch (month) {
                case 01:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " January, " + temp[2];
                    break;
                case 2:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " February, " + temp[2];
                    break;
                case 3:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " March, " + temp[2];
                    break;
                case 4:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " April, " + temp[2];
                    break;
                case 5:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " May, " + temp[2];
                    break;
                case 6:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " June, " + temp[2];
                    break;
                case 7:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " July, " + temp[2];
                    break;
                case 8:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " August, " + temp[2];
                    break;
                case 9:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " September, " + temp[2];
                    break;
                case 10:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " October, " + temp[2];
                    break;
                case 11:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " November, " + temp[2];
                    break;
                case 12:
                    temp[0] = AddSuffixForDay(temp[0]);
                    formatted = temp[0] + " December, " + temp[2];
                    break;
            }
        }

        return getFormattedDate(formatted);
    }


    private String AddSuffixForDay(String originalDay) {
        String day = "";
        if (originalDay.startsWith("0"))
            originalDay = originalDay.replace("0", "");

        if (originalDay.equals("01") || originalDay.equals("1")
                || originalDay.equals("21") || originalDay.equals("31"))
            day = originalDay + "";
        else if (originalDay.equals("02") || originalDay.equals("2")
                || originalDay.equals("22"))
            day = originalDay + "";
        else if (originalDay.equals("03") || originalDay.equals("3")
                || originalDay.equals("23"))
            day = originalDay + "";

        else
            day = originalDay + "";

        return day;
    }

    private  boolean isNullOrEmpty(String value) {
        if (null == value) {
            return true;
        }

        value = value.trim();

        if ("null".equalsIgnoreCase(value) || value.length() == 0) {
            return true;
        }

        return false;
    }
}