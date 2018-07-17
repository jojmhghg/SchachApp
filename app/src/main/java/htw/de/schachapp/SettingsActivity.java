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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        // Email in Feld eintragen
        EditText email = (EditText)findViewById(R.id.emailInput);
        email.setText(mAuth.getCurrentUser().getEmail());

        // Username in Feld eintragen & Highlighing-Switch einstellen
        db.collection("user").document(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            Switch highlighting = (Switch)findViewById(R.id.highlightingToggle);
                            EditText username = (EditText)findViewById(R.id.usernameInput);

                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                username.setText(document.getData().get("name").toString());
                                highlighting.setChecked((Boolean) document.getData().get("help"));
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

        Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.this.updateSettings();
            }
        });

        Button saveAndReturnButton = (Button)findViewById(R.id.saveAndReturnButton);
        saveAndReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.this.updateSettings();
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

    public void updateSettings(){
        EditText username = (EditText)findViewById(R.id.usernameInput);
        Switch highlighting = (Switch)findViewById(R.id.highlightingToggle);

        if(username.getText().toString().length() < 3){
            //TODO: Fehlermeldung -> Username zu kurz
        }

        Map<String, Object> data = new HashMap<>();
        data.put("username", username.getText().toString());
        data.put("highlighting", highlighting.isChecked());

        mFunctions.getHttpsCallable("updateSettings").call(data);
    }

}
