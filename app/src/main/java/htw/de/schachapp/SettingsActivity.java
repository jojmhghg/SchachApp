package htw.de.schachapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseFunctions mFunctions;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText username = (EditText)findViewById(R.id.usernameInput);
                Switch highlighting = (Switch)findViewById(R.id.highlightingToggle);

                Map<String, Object> data = new HashMap<>();
                data.put("username", username.getText().toString());
                data.put("highlighting", highlighting.isChecked());

                mFunctions.getHttpsCallable("updateSettings").call(data);
            }
        });

        Button saveAndReturnButton = (Button)findViewById(R.id.saveAndReturnButton);
        saveAndReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText username = (EditText)findViewById(R.id.usernameInput);
                Switch highlighting = (Switch)findViewById(R.id.highlightingToggle);

                Map<String, Object> data = new HashMap<>();
                data.put("username", username.getText().toString());
                data.put("highlighting", highlighting.isChecked());

                mFunctions.getHttpsCallable("updateSettings").call(data);

                finish();
            }
        });

        Button returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
