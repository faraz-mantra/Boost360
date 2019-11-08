package com.nowfloats.PreSignUp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thinksity.R;

public class FragmentFive extends Fragment {
	

	Button	next,back;
	View root = null;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		try{
			//MixPanelController.track(EventKeys.TUTORIAL_SCREEN_DOMAIN, null);
			root	=	inflater.inflate(R.layout.ps_screen5, null, false);
			
						
		}
		catch(Exception e)
		{
			
		}
		
		return root;
		
	}
	
}
