package com.nowfloats.BusinessProfile.UI.UI.FAQ;

import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thinksity.R;


public class FAQ_QandA_Activity extends ActionBarActivity {

    private Toolbar toolbar;
    private TextView headerText;
    private ListView lv_q_and_a;

    private String[] q_and_a_faq_values;
    private String[] ans_values;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq__qand_a_);

        toolbar = (Toolbar)findViewById(R.id.app_bar_faq_qnada);
        setSupportActionBar(toolbar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText("FAQ");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_q_and_a = (ListView)findViewById(R.id.lv_qanda);

        int item_no = getIntent().getIntExtra("item_no", 0);
        Log.d("ILUD item_no", String.valueOf(item_no));
        try {
            int quest_resourceId = R.array.class.getField("questions_" + String.valueOf(item_no)).getInt(null);
            int ans_resourceId = R.array.class.getField("answer_" + String.valueOf(item_no)).getInt(null);
            q_and_a_faq_values = getResources().getStringArray(quest_resourceId);
            ans_values = getResources().getStringArray(ans_resourceId);
            adapter = new ArrayAdapter<String>(this, R.layout.tv_row_for_lv, R.id.tv_row_val, q_and_a_faq_values);
            lv_q_and_a.setAdapter(adapter);
            lv_q_and_a.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    InflateAnsView(ans_values[position]);
                }
            });
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
//        switch (item_no)
//        {
//            case 0:
//
//                break;
//            case 1:
//
//                break;
//            default:
//        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private void InflateAnsView(String ans)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

// 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(ans)
                .setTitle("Answer")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dialog!=null)
                            dialog.dismiss();
                    }
                });

// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
