package htw.de.schachapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

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

    //Variable GameDaten
    private int turn;

    private ListenerRegistration lr1;
    private ListenerRegistration lr2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielbrett);

        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        username1 = (TextView) findViewById(R.id.sbName1);
        username2 = (TextView) findViewById(R.id.sbName2);

        //Listener auf die User-Daten (Username, Highlighting & GameId)
        DocumentReference docRef = db.collection("user").document(mAuth.getUid());
        lr1 = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

                    if(gameId != null && !newGameId.equals(gameId)){
                        Toast.makeText(Spielbrett.this, "Ohje... GameId hat sich geändert!",
                                Toast.LENGTH_LONG).show();
                        //TODO: was tun?
                    }

                    if(gameId == null && newGameId!= null && !newGameId.equals("null")){
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
                                            if(document.get("id1") != null && document.get("id1").toString().equals(mAuth.getUid())){
                                                ownColorIsWhite = true;
                                            }

                                            if(document.get("type").toString().equals("off")){
                                                isOnline = false;
                                            }
                                            else if(document.get("type").toString().equals("on")){
                                                isOnline = true;
                                            }
                                            else{
                                                //TODO: Fehler
                                            }

                                            if(!isOnline){
                                                username2.setText(document.get("name").toString());
                                            }
                                            else{
                                                //Listener auf die User-Daten des Gegners, falls Online(Username, Highlighting & GameId)
                                                final DocumentReference docRef2 = db.collection("user").document(mAuth.getUid());
                                                docRef2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                                        @Nullable FirebaseFirestoreException e) {
                                                        if (snapshot != null && snapshot.exists()) {
                                                            username2.setText(snapshot.getData().get("name").toString());
                                                        }
                                                    }
                                                });
                                            }

                                            init();
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

    private void init(){
        //Menüleiste füllen
        menue = (Spinner) findViewById(R.id.sbMenue);
        ArrayAdapter<CharSequence> adapter;
        if(this.isOnline){
            adapter = ArrayAdapter.createFromResource(this,
                    R.array.sbMenueOn, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            menue.setAdapter(adapter);
        }
        else{
            adapter = ArrayAdapter.createFromResource(this,
                    R.array.sbMenueOff, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            menue.setAdapter(adapter);
        }
        menue.setOnItemSelectedListener(this);

        //Listener auf die Game-Daten (res, turn)
        DocumentReference docRef2 = db.collection("user").document(mAuth.getUid());
        lr2 = docRef2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(Spielbrett.this, R.string.error_get_data,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String res = "null";
                    if(snapshot.getData().get("res") != null){
                        res = snapshot.getData().get("res").toString();
                    }

                    Toast.makeText(Spielbrett.this, snapshot.getData().get("turn").toString(),
                            Toast.LENGTH_LONG).show();

                    int newTurn;
                    if(snapshot.getData().get("turn") == null){
                        newTurn = 1;

                    }
                    else {
                        newTurn = (Integer) snapshot.getData().get("turn");
                    }

                    if(!res.equals("null")){
                        Toast.makeText(Spielbrett.this, res,
                                Toast.LENGTH_LONG).show();
                        //TODO: Spiel beendet, Ergebnis anzeigen
                    }

                    if(newTurn > turn){
                        if(ownColorIsWhite && newTurn % 2 == 1 || !ownColorIsWhite && newTurn % 2 == 0){
                            //TODO: man ist nun am Zug
                        }
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
                this.surrenderClicked();
                break;

            case "Remis anbieten":
                remisClicked();
                break;

            case "Einstellungen":
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;

            case "Spiel verlassen":
                leaveClicked();
                break;

            case "Zurück zum Spiel":

                break;

            default:

                break;
        }

        parent.setSelection(0);

    }

    private void leaveClicked(){
        surrenderFunction().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                    }
                }
                else {
                    Toast.makeText(Spielbrett.this, "Du hast das Spiel verlassen!",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void surrenderClicked(){
        surrenderFunction().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                    }
                }
                else {
                    //TODO: Surrender Screen
                    Toast.makeText(Spielbrett.this, "TODO: Surrender Screen",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private Task<String> surrenderFunction() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("gameId", gameId);

        return mFunctions.getHttpsCallable("surrender").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
            @Override
            public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                String result = (String) task.getResult().getData();
                return result;
            }
        });
    }

    private void remisClicked(){
        remisFunction().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                    }
                }
                else {
                    turn++;
                    //TODO: Nicht mehr am Zug anzeigen
                    Toast.makeText(Spielbrett.this, "TODO: Zug gemacht",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private Task<String> remisFunction() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("gameId", gameId);

        return mFunctions.getHttpsCallable("offerDraw").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
            @Override
            public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                String result = (String) task.getResult().getData();
                return result;
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void onStop () {
        if(lr1 != null){
            lr1.remove();
        }
        if(lr2 != null){
            lr2.remove();
        }
        super.onStop();
    }

}
