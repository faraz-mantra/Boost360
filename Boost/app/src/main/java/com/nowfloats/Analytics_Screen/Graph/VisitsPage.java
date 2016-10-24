package com.nowfloats.Analytics_Screen.Graph;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.thinksity.R;

/**
 * Created by tushar on 18-05-2015.
 */
public class VisitsPage extends AppCompatActivity {

    Typeface customFont;
    TextView abTitle;
    ImageButton backButton;
    private Toolbar toolbar;
    public static TextView headerText;
    public static ImageView plusAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setTheme(R.style.AppTheme);

    setContentView(R.layout.graph_visits_page);

     //   android.support.v7.app.ActionBar ab = getSupportActionBar();

        //ab.setBackground(Color.rgb(255, 195, 38));
//        ab.setHomeButtonEnabled(true);
//        ab.setDisplayHomeAsUpEnabled(true);
//        ab.setTitle("Visits");
//        ab.setElevation(0);
//        ab.hide();


//        abTitle = (TextView) findViewById(R.id.abTitle);
//        backButton = (ImageButton) findViewById(R.id.homeButton);
//        abTitle.setTypeface(customFont,Typeface.BOLD);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText(getResources().getString(R.string.visits));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        customFont = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");


        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),this,viewPager));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

        LinearLayout view = (LinearLayout) tabsStrip.getChildAt(0);

        for(int i=1; i<3; i++)
        {
            TextView textView = (TextView) view.getChildAt(i);
            textView.setTextColor(Color.argb(150,255,255,255));
            textView.setTypeface(customFont,Typeface.BOLD);
        }
        TextView textView = (TextView) view.getChildAt(0);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(customFont,Typeface.BOLD);

        tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {


               PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip)findViewById(R.id.tabs);
               LinearLayout view = (LinearLayout) tabStrip.getChildAt(0);

                for(int i=0; i<3; i++)
                {
                    TextView textView = (TextView) view.getChildAt(i);
                    textView.setTextColor(Color.argb(150,255,255,255));
                }
                TextView textView = (TextView) view.getChildAt(position);
                textView.setTextColor(Color.WHITE);



            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

             @Override
            public void onPageScrollStateChanged(int state) {

             }
        });

        viewPager.setCurrentItem(0);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact__info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==android.R.id.home){


            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            //  NavUtils.navigateUpFromSameTask(this);
        }



        return super.onOptionsItemSelected(item);
    }

}
