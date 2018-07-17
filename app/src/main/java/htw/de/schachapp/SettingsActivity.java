package htw.de.schachapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mFunctions = FirebaseFunctions.getInstance();

        Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: save highlighting

                EditText username = (EditText)findViewById(R.id.usernameInput);
                Map<String, Object> data = new HashMap<>();
                data.put("text", username);
                data.put("push", true);
                mFunctions.getHttpsCallable("changeUsername").call(data);
            }
        });

        Button saveAndReturnButton = (Button)findViewById(R.id.saveAndReturnButton);
        saveAndReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: save highlighting

                EditText username = (EditText)findViewById(R.id.usernameInput);
                Map<String, Object> data = new HashMap<>();
                data.put("text", username);
                data.put("push", true);
                mFunctions.getHttpsCallable("changeUsername").call(data);

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
