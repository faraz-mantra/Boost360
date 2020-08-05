package com.nowfloats.AccrossVerticals.Testimonials;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thinksity.R;

public class TestimonialsFeedbackActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView saveButton, headerText;
    private ImageView deleteButton;
    private View dotLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testimonials_feedback);

        initialization();
    }


    void initialization(){
        toolbar = (Toolbar) findViewById(R.id.app_bar_site_appearance);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        saveButton = (TextView) findViewById(R.id.save_review);
        deleteButton = (ImageView) findViewById(R.id.ivDelete);
        dotLines = (View) findViewById(R.id.dot_lines);
        dotLines.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        setSupportActionBar(toolbar);

        headerText.setText(getResources().getString(R.string.testimonials));
        deleteButton.setVisibility(View.VISIBLE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Save Button is clicked", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Delete Button is clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}