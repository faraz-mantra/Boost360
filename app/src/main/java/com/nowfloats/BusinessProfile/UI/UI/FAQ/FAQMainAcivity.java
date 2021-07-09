package com.nowfloats.BusinessProfile.UI.UI.FAQ;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.thinksity.R;


public class FAQMainAcivity extends AppCompatActivity {

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
        headerText.setText(getResources().getString(R.string.faqs));

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        main_lv_faq_values = intent.getStringArrayExtra("array");
        lv_faq_main = (ListView)findViewById(R.id.lv_faq_main);

        adapter = new ArrayAdapter<String>(this, R.layout.faq_row_layout, main_lv_faq_values);

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
