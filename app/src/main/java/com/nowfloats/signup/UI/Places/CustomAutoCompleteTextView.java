package com.nowfloats.signup.UI.Places;

import android.content.Context;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;

import com.nowfloats.signup.UI.Model.PlacesModel;

import java.util.ArrayList;

/** Customizing AutoCompleteTextView to return Place Description   
 *  corresponding to the selected item
 */
public class CustomAutoCompleteTextView extends AppCompatAutoCompleteTextView {
    ArrayList<PlacesModel> Places = new ArrayList<>();
	public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	/** Returns the place description corresponding to the selected item */
	/*@Override
	protected CharSequence convertSelectionToString(Object selectedItem) {
		*//** Each item in the autocompetetextview suggestionList list is a hashmap object *//*
        Places.clear();
        String result = " ";
        try {
            PlacesMainModel mainModel = (PlacesMainModel)selectedItem;
            Places = mainModel.terms;
            if(Places !=null && Places.size()>0){
                result = Places.get(0).value  + "," + Places.get(Places.size()-1).value;
            }else{
                if(mainModel.description!=null && mainModel.description.trim().length()>0)
                result = mainModel.description;
            }
        }catch(Exception e){e.printStackTrace();}
		return result;
	}*/

   /* @Override
    protected CharSequence convertSelectionToString(Object selectedItem) {
        *//** Each item in the autocompetetextview suggestionList list is a hashmap object *//*
        HashMap<String, ArrayList<PlacesModel>> hm = (HashMap<String, ArrayList<PlacesModel>>) selectedItem;
        String result = " ";
        try {
            result =    hm.get("terms").get(0).value +","+ hm.get("terms").get(hm.size()-1).value;
        }catch(Exception e){e.printStackTrace();}

        return result;
    }*/



    public CustomAutoCompleteTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    // this is how to disable AutoCompleteTextView filter
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }
}