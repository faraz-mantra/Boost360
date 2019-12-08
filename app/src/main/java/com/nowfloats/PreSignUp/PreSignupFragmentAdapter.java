package com.nowfloats.PreSignUp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.viewpagerindicator.IconPagerAdapter;

class PreSignupFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
	FragmentOne one;
	FragmentTwo two;
	FragmentThree three;
	FragmentFour	four;
	FragmentFive five;
	FragmentSix six;
	FragmentSeven seven;

	int mCount = 7;

    public PreSignupFragmentAdapter(FragmentManager fm) {
        super(fm);
        one		=	new FragmentOne();
        two 	=	new FragmentTwo();
        three	=	new FragmentThree();
        four	=	new FragmentFour();	
        five	=	new FragmentFive();
        six		=	new FragmentSix();
        seven	=	new FragmentSeven();
    }

    @Override
    public Fragment getItem(int position) {
    	Fragment	frag	=	null;
    	switch (position) {
		case 0:
			frag = one;
			break;
		case 1:
			frag = two;
			break;
		case 2:
			frag = three;
			break;
		case 3:
			frag = four;
			break;
		case 4:
			frag = five;
			break;
		case 5:
			frag = six;
			
			break;
		case 6:
			frag = seven;
			
			break;


		default:
			frag = one;
		}
        return frag;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    

    @Override
    public int getIconResId(int index) {
      return 0;
    }

}