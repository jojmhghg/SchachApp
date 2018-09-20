package htw.de.schachapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.functions.FirebaseFunctions;

public class Spielbrett extends Activity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;
    private FirebaseFirestore db;

    private Spinner menue;
    private TextView username1;
    private TextView username2;

    //Userdaten
    private boolean helpOn;
    private String gameId;

    //Fixe GameDaten
    private boolean isOnline;
    private boolean ownColorIsWhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielbrett);

        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        //Menüleiste füllen
        menue = (Spinner) findViewById(R.id.sbMenue);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.sbMenue, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menue.setAdapter(adapter);

        username1 = (TextView) findViewById(R.id.sbName1);
        username2 = (TextView) findViewById(R.id.sbName2);

        menue.setOnItemSelectedListener(this);

        //Listener auf die User-Daten (Username, Highlighting & GameId)
        final DocumentReference docRef = db.collection("user").document(mAuth.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(Spielbrett.this, R.string.error_get_data,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String newGameId = snapshot.getData().get("game").toString();
                    helpOn = Boolean.parseBoolean(snapshot.getData().get("help").toString());
                    username1.setText(snapshot.getData().get("name").toString());

                    if(gameId != null && newGameId != gameId){
                        Toast.makeText(Spielbrett.this, "Ohje... GameId hat sich geändert!",
                                Toast.LENGTH_LONG).show();
                        //TODO: was tun?
                    }

                    if(gameId == null){
                        gameId = newGameId;

                        // Einmaliges lesen von Fix-Daten der Partie ()
                        db.collection("games")
                                .document(gameId)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();

                                            //Eigene Farbe bestimmen
                                            ownColorIsWhite = false;
                                            if(document.get("id1").toString().equals(mAuth.getUid())){
                                                ownColorIsWhite = true;
                                            }

                                            if(ownColorIsWhite){
                                                username2.setText(document.get("name2").toString());
                                            }
                                            else{
                                                username2.setText(document.get("name1").toString());
                                            }

                                        } else {
                                            Toast.makeText(Spielbrett.this, R.string.error_get_data,
                                                    Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                });
                    }
                }
                else{
                    Toast.makeText(Spielbrett.this, "Ups...!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });





    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        switch (parent.getItemAtPosition(pos).toString()){
            case "Aufgeben":
                Toast.makeText(Spielbrett.this, "TODO: " + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();
                break;

            case "Remis anbieten":
                Toast.makeText(Spielbrett.this, "TODO: " + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();
                break;

            case "Einstellungen":
                Toast.makeText(Spielbrett.this, "TODO: " + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();
                break;

            case "Spiel verlassen":
                Toast.makeText(Spielbrett.this, "TODO: " + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();
                break;

            case "Zurück zum Spiel":

                break;

            default:

                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
