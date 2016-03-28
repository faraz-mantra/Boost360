package com.nowfloats.BusinessProfile.UI.UI.FAQ;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.thinksity.R;


public class FAQMainAcivity extends ActionBarActivity {

    private ListView lv_faq_main;
    private Toolbar toolbar;
    private TextView headerText;

    private String[] main_lv_faq_values;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqmain_acivity);


        toolbar = (Toolbar)findViewById(R.id.app_bar_faq_main);
        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText("FAQ");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_faq_main = (ListView)findViewById(R.id.lv_faq_main);
        main_lv_faq_values = getResources().getStringArray(R.array.faqmain);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, main_lv_faq_values);

        lv_faq_main.setAdapter(adapter);

        lv_faq_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), FAQ_QandA_Activity.class);
                i.putExtra("item_no", position);
                startActivity(i);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
