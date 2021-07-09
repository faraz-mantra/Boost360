package com.nowfloats.PreSignUp;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thinksity.R;

public class FragmentEight extends Fragment {
	

	Button	next,back;
	View root = null;
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		try{
			root	=	inflater.inflate(R.layout.ps_screen8, null, false);
			
						
		}
		catch(Exception e)
		{
			
		}
		
		return root;
		
	}
	
}
