package htw.de.schachapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class NewOnlineGameActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;
    private FirebaseFirestore db;

    // Elemente in View
    private RadioButton m5minRadioButton;
    private RadioButton m10minRadioButton;
    private RadioButton m15minRadioButton;
    private RadioButton m30minRadioButton;
    private RadioButton m60minRadioButton;
    private ToggleButton mColorToggleButton;
    private Button mEnterQueueButton;
    private Button mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_online_game);

        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        m5minRadioButton = (RadioButton)findViewById(R.id.onlineGame5minRadioButton);
        m10minRadioButton = (RadioButton)findViewById(R.id.onlineGame10minRadioButton);
        m15minRadioButton = (RadioButton)findViewById(R.id.onlineGame15minRadioButton);
        m30minRadioButton = (RadioButton)findViewById(R.id.onlineGame30minRadioButton);
        m60minRadioButton = (RadioButton)findViewById(R.id.onlineGame60minRadioButton);
        mColorToggleButton = (ToggleButton)findViewById(R.id.onlinePartieColorToggleButton);
        mEnterQueueButton = (Button)findViewById(R.id.onlinePartieEnterQueueButton);
        mReturnButton = (Button)findViewById(R.id.onlinePartieReturnButton);

        mEnterQueueButton.setOnClickListener(new View.OnClickListener() {
            String gameId;
            String favColor = "black";
            int time = 5;

            @Override
            public void onClick(View view) {
                //Lade username + partieID
                db.collection("user")
                        .document(mAuth.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    gameId = document.get("game").toString();

                                    if(m5minRadioButton.isChecked()){
                                        time = 5;
                                    }
                                    else if(m10minRadioButton.isChecked()){
                                        time = 10;
                                    }
                                    else if(m15minRadioButton.isChecked()){
                                        time = 15;
                                    }
                                    else if(m30minRadioButton.isChecked()){
                                        time = 30;
                                    }
                                    else if(m60minRadioButton.isChecked()){
                                        time = 60;
                                    }
                                    String queue_name = "queue" + time;

                                    boolean favColorIsWhite = mColorToggleButton.isChecked();
                                    if(favColorIsWhite){
                                        favColor = "white";
                                    }
                                    else{
                                        favColor = "black";
                                    }

                                    db.collection(queue_name)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        boolean found = false;

                                                        for (DocumentSnapshot document : task.getResult()) {
                                                            String id2 = document.getId();
                                                            String color = document.getData().get("color").toString();

                                                            if(color != favColor){
                                                                //Partie mit Gegner erstellen
                                                                Map<String, Object> data = new HashMap<>();
                                                                data.put("partieZeit", time);
                                                                data.put("bevorzugteFarbe", favColor);
                                                                data.put("id", id2);
                                                                mFunctions.getHttpsCallable("pairPlayers").call(data);

                                                                found = true;

                                                                Toast.makeText(NewOnlineGameActivity.this, "Gegener gefunden!",
                                                                        Toast.LENGTH_LONG).show();

                                                                Intent intent = new Intent(getApplicationContext(), Spielbrett.class);
                                                                startActivity(intent);
                                                            }

                                                        }

                                                        //Wenn kein Gegner gefunden, mache Eintrag in Queue und warte
                                                        if(!found){
                                                            //Warteschlange betreten
                                                            Map<String, Object> data = new HashMap<>();
                                                            data.put("partieZeit", time);
                                                            data.put("bevorzugteFarbe", favColor);
                                                            mFunctions.getHttpsCallable("enterQueue").call(data);

                                                            //Auf Gegner warten und dann weiter zu Spielbrett
                                                            final DocumentReference docRef = db.collection("user").document(mAuth.getUid());
                                                            docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                                                    @Nullable FirebaseFirestoreException e) {

                                                                    if (e != null) {
                                                                        Toast.makeText(NewOnlineGameActivity.this, R.string.error_get_data,
                                                                                Toast.LENGTH_LONG).show();
                                                                        return;
                                                                    }

                                                                    if (snapshot != null && snapshot.exists()) {
                                                                        String newGameId = snapshot.getData().get("game").toString();
                                                                        if(newGameId != gameId){
                                                                            Toast.makeText(NewOnlineGameActivity.this, "Gegener gefunden!",
                                                                                    Toast.LENGTH_LONG).show();

                                                                            Intent intent = new Intent(getApplicationContext(), Spielbrett.class);
                                                                            startActivity(intent);
                                                                        }
                                                                    }
                                                                    else{
                                                                        Toast.makeText(NewOnlineGameActivity.this, "Ups...!",
                                                                                Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    } else {
                                                        Toast.makeText(NewOnlineGameActivity.this, R.string.error_get_data,
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(NewOnlineGameActivity.this, R.string.error_get_data,
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }
                        });
            }
        });

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
