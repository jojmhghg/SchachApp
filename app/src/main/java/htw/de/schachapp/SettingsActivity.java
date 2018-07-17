package htw.de.schachapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;
    private FirebaseFirestore db;

    // Elemente in View
    private EditText mEmailView;
    private EditText mUsernameView;
    private Switch mHighlightingView;
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

        mEmailView = (EditText)findViewById(R.id.settingsEmailInput);
        mHighlightingView = (Switch)findViewById(R.id.settingsHighlightingToggle);
        mUsernameView = (EditText)findViewById(R.id.settingsUsernameInput);
        mSaveButton = (Button)findViewById(R.id.settingsSaveButton);
        mSaveAndReturnButton = (Button)findViewById(R.id.settingsSaveAndReturnButton);
        mReturnButton = (Button)findViewById(R.id.settingsReturnButton);

        // Email in Feld eintragen
        mEmailView.setText(mAuth.getCurrentUser().getEmail());

        // Username in Feld eintragen & Highlighing-Switch einstellen
        db.collection("user").document(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                mUsernameView.setText(document.getData().get("name").toString());
                                mHighlightingView.setChecked((Boolean) document.getData().get("help"));
                            } else {
                                //TODO: Fehlerbehandlung wenn document nicht exisitert
                            }
                        } else {
                            //TODO: Fehlerbehandlung wenn lesen nicht geht
                        }
                    }
                });

        /*final DocumentReference docRef = db.collection("user").document(mAuth.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                EditText email = (EditText)findViewById(R.id.emailInput);
                if (e != null) {
                    //TODO: Fehlerbehandlung
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    //TODO: nächste Zeile ist nur ein Test
                    email.setText("Username: " + snapshot.getData().get("name"));
                } else {
                    //TODO: Fehlerbehandlung
                }
            }
        });*/

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
        String username = mUsernameView.getText().toString();
        Boolean highlighting = mHighlightingView.isChecked();

        if(username.length() < 3){
            //TODO: Fehlermeldung -> Username zu kurz
        }

        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        data.put("highlighting", highlighting);

        mFunctions.getHttpsCallable("updateSettings").call(data);
    }

}
