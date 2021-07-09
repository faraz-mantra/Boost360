package nfkeyboard.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


public class TestMainActivity extends AppCompatActivity{

    // Used to load the 'native-lib' library on application startup.

    SpellCheckerManager spellCheckerManager;
    EditText query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(nowfloats.nfkeyboard.R.layout.activity_main_test);
//        query = findViewById(nowfloats.nfkeyboard.R.id.edit_query);
//        spellCheckerManager = new SpellCheckerManager(this,getLifecycle(), new SpellCheckerInterface() {
//            @Override
//            public void onSuggestion(String[] text) {
//
//            }
//        });

        getLifecycle().addObserver(spellCheckerManager);
    }

    public void goClick(View v){
        spellCheckerManager.getSuggestions(query.getText().toString());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getLifecycle().removeObserver(spellCheckerManager);
    }
}
