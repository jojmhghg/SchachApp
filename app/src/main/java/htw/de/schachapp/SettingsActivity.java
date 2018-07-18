package htw.de.schachapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;
    private FirebaseFirestore db;

    // Elemente in View
    private EditText mEmail;
    private EditText mUsername;
    private Switch mHighlighting;
    private Button mSaveButton;
    private Button mSaveAndReturnButton;
    private Button mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        mEmail = (EditText)findViewById(R.id.settingsEmailInput);
        mHighlighting = (Switch)findViewById(R.id.settingsHighlightingToggle);
        mUsername = (EditText)findViewById(R.id.settingsUsernameInput);
        mSaveButton = (Button)findViewById(R.id.settingsSaveButton);
        mSaveAndReturnButton = (Button)findViewById(R.id.settingsSaveAndReturnButton);
        mReturnButton = (Button)findViewById(R.id.settingsReturnButton);

        // Email in Feld eintragen
        mEmail.setText(mAuth.getCurrentUser().getEmail());

        // Username in Feld eintragen & Highlighing-Switch einstellen
        final DocumentReference docRef = db.collection("user").document(mAuth.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(SettingsActivity.this, R.string.error_get_data,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    mUsername.setText(snapshot.getData().get("name").toString());
                    mHighlighting.setChecked((Boolean) snapshot.getData().get("help"));
                } else {
                    mUsername.setText("");
                    mHighlighting.setChecked(true);
                }
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.this.updateSettings();
            }
        });

        mSaveAndReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.this.updateSettings();
                finish();
            }
        });

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void updateSettings(){
        String username = mUsername.getText().toString();
        Boolean highlighting = mHighlighting.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_field_required));
            focusView = mUsername;
            cancel = true;
        }
        else if(!isUsernameValid(username)){
            mUsername.setError(getString(R.string.error_invalid_username));
            focusView = mUsername;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Map<String, Object> data = new HashMap<>();
            data.put("username", username);
            data.put("highlighting", highlighting);

            mFunctions.getHttpsCallable("updateSettings").call(data);
        }
    }

    private boolean isUsernameValid(String password) {
        return password.length() >= 3;
    }

}
