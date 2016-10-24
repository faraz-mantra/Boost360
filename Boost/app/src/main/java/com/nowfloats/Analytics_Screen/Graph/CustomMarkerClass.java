package com.nowfloats.Analytics_Screen.Graph;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.thinksity.R;

import java.util.ArrayList;

/**
 * Created by tushar on 16-05-2015.
 */
public class CustomMarkerClass extends MarkerView {

    private TextView tvContent;
    private String[] values = getResources().getStringArray(R.array.num_values);
    String[] monthNameArray = getResources().getStringArray(R.array.months);

    int[] weekValues = {0,0,0,0};
    int[] monthValues = {0,0,0,0,0,0,0,0,0,0,0,0};
    ArrayList<String> dateArray;
    ArrayList<Integer> valueArray;
    int mode;

    public CustomMarkerClass (Context context, int layoutResource,ArrayList<String> dateArray,ArrayList<Integer> valueArray,int mode) {
        super(context, layoutResource);
        tvContent = (TextView) findViewById(R.id.tvContent);
        this.dateArray = dateArray;
        this.valueArray = valueArray;
        this.mode = mode;
        if(mode == 1)
        {
            for(int i=0; i<valueArray.size(); i++)
            {
                int k = i/7;
                if(k!=4)
                    weekValues[k] += valueArray.get(i);
                else
                    weekValues[3] += valueArray.get(i);
            }

        }
        else if(mode == 2)
        {
            for(int i=0; i<valueArray.size(); i++)
            {
                int k = Integer.parseInt(dateArray.get(i).substring(5,7)) - 1;
                monthValues[k] += valueArray.get(i);
            }
        }
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, int dataSetIndex) {

        int index = e.getXIndex();


        if(mode == 1)
        {
            tvContent.setText(weekValues[index] + "\n" + getResources().getString(R.string.week)+" " + (index+1));
        }
        else if(mode == 0)
        {
            tvContent.setText(valueArray.get(index) + "\n" + dateArray.get(index)); // set the entry-value as the display text
        }
        else if(mode == 2)
        {
            tvContent.setText(monthValues[index] + "\n" + monthNameArray[index]);
        }

    }

    @Override
    public int getXOffset() {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset() {
        // this will cause the marker-view to be above the selected value
        return -(getHeight()+5);
    }


}
