package htw.de.schachapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

    private ImageView[] spielbrett;

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
    private long turn;

    private ListenerRegistration lr1;
    private ListenerRegistration lr2;
    private boolean init;
    private long zufallszahl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielbrett);

        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        username1 = (TextView) findViewById(R.id.sbName1);
        username2 = (TextView) findViewById(R.id.sbName2);

        spielbrett = new ImageView[64];
        spielbrett[0] = (ImageView) findViewById(R.id.a1);
        spielbrett[1] = (ImageView) findViewById(R.id.b1);
        spielbrett[2] = (ImageView) findViewById(R.id.c1);
        spielbrett[3] = (ImageView) findViewById(R.id.d1);
        spielbrett[4] = (ImageView) findViewById(R.id.e1);
        spielbrett[5] = (ImageView) findViewById(R.id.f1);
        spielbrett[6] = (ImageView) findViewById(R.id.g1);
        spielbrett[7] = (ImageView) findViewById(R.id.h1);
        spielbrett[8] = (ImageView) findViewById(R.id.a2);
        spielbrett[9] = (ImageView) findViewById(R.id.b2);
        spielbrett[10] = (ImageView) findViewById(R.id.c2);
        spielbrett[11] = (ImageView) findViewById(R.id.d2);
        spielbrett[12] = (ImageView) findViewById(R.id.e2);
        spielbrett[13] = (ImageView) findViewById(R.id.f2);
        spielbrett[14] = (ImageView) findViewById(R.id.g2);
        spielbrett[15] = (ImageView) findViewById(R.id.h2);
        spielbrett[16] = (ImageView) findViewById(R.id.a3);
        spielbrett[17] = (ImageView) findViewById(R.id.b3);
        spielbrett[18] = (ImageView) findViewById(R.id.c3);
        spielbrett[19] = (ImageView) findViewById(R.id.d3);
        spielbrett[20] = (ImageView) findViewById(R.id.e3);
        spielbrett[21] = (ImageView) findViewById(R.id.f3);
        spielbrett[22] = (ImageView) findViewById(R.id.g3);
        spielbrett[23] = (ImageView) findViewById(R.id.h3);
        spielbrett[24] = (ImageView) findViewById(R.id.a4);
        spielbrett[25] = (ImageView) findViewById(R.id.b4);
        spielbrett[26] = (ImageView) findViewById(R.id.c4);
        spielbrett[27] = (ImageView) findViewById(R.id.d4);
        spielbrett[28] = (ImageView) findViewById(R.id.e4);
        spielbrett[29] = (ImageView) findViewById(R.id.f4);
        spielbrett[30] = (ImageView) findViewById(R.id.g4);
        spielbrett[31] = (ImageView) findViewById(R.id.h4);
        spielbrett[32] = (ImageView) findViewById(R.id.a5);
        spielbrett[33] = (ImageView) findViewById(R.id.b5);
        spielbrett[34] = (ImageView) findViewById(R.id.c5);
        spielbrett[35] = (ImageView) findViewById(R.id.d5);
        spielbrett[36] = (ImageView) findViewById(R.id.e5);
        spielbrett[37] = (ImageView) findViewById(R.id.f5);
        spielbrett[38] = (ImageView) findViewById(R.id.g5);
        spielbrett[39] = (ImageView) findViewById(R.id.h5);
        spielbrett[40] = (ImageView) findViewById(R.id.a6);
        spielbrett[41] = (ImageView) findViewById(R.id.b6);
        spielbrett[42] = (ImageView) findViewById(R.id.c6);
        spielbrett[43] = (ImageView) findViewById(R.id.d6);
        spielbrett[44] = (ImageView) findViewById(R.id.e6);
        spielbrett[45] = (ImageView) findViewById(R.id.f6);
        spielbrett[46] = (ImageView) findViewById(R.id.g6);
        spielbrett[47] = (ImageView) findViewById(R.id.h6);
        spielbrett[48] = (ImageView) findViewById(R.id.a7);
        spielbrett[49] = (ImageView) findViewById(R.id.b7);
        spielbrett[50] = (ImageView) findViewById(R.id.c7);
        spielbrett[51] = (ImageView) findViewById(R.id.d7);
        spielbrett[52] = (ImageView) findViewById(R.id.e7);
        spielbrett[53] = (ImageView) findViewById(R.id.f7);
        spielbrett[54] = (ImageView) findViewById(R.id.g7);
        spielbrett[55] = (ImageView) findViewById(R.id.h7);
        spielbrett[56] = (ImageView) findViewById(R.id.a8);
        spielbrett[57] = (ImageView) findViewById(R.id.b8);
        spielbrett[58] = (ImageView) findViewById(R.id.c8);
        spielbrett[59] = (ImageView) findViewById(R.id.d8);
        spielbrett[60] = (ImageView) findViewById(R.id.e8);
        spielbrett[61] = (ImageView) findViewById(R.id.f8);
        spielbrett[62] = (ImageView) findViewById(R.id.g8);
        spielbrett[63] = (ImageView) findViewById(R.id.h8);

        init = true;

        //Übergabeparameter entnehmen
        Intent myIntent = getIntent();
        zufallszahl = myIntent.getLongExtra("chk", 0);
        if(zufallszahl == 0){
            Toast.makeText(Spielbrett.this, "Keine Prüfzahl übergeben!",
                    Toast.LENGTH_LONG).show();
            finish();
        }

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

                        //Listener auf die Game-Daten
                        DocumentReference docRef2 = db.collection("games").document(gameId);
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

                                    // Einmaliges lesen von Fix-Daten der Partie ()
                                    if(init && snapshot.getData().get("chk") != null && ((Long)snapshot.getData().get("chk")).longValue() == zufallszahl){

                                        //Figuren setzen:
                                        setzeFiguren(snapshot.getData().get("sb").toString());

                                        //Eigene Farbe bestimmen
                                        ownColorIsWhite = false;
                                        if(snapshot.getData().get("id1") != null && snapshot.getData().get("id1").toString().equals(mAuth.getUid())){
                                            ownColorIsWhite = true;
                                        }

                                        if(snapshot.getData().get("type").toString().equals("off")){
                                            isOnline = false;
                                        }
                                        else if(snapshot.getData().get("type").toString().equals("on")){
                                            isOnline = true;
                                        }
                                        else{
                                            //TODO: Fehler
                                        }

                                        if(!isOnline){
                                            username2.setText(snapshot.getData().get("name").toString());
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
                                        init = false;
                                    }

                                    //Listener auf die Game-Daten (res, turn)
                                    if(!init){
                                        if(((Long)snapshot.getData().get("chk")).longValue() != zufallszahl){
                                            Toast.makeText(Spielbrett.this, "Prüfzahl stimmt nicht mehr überein",
                                                    Toast.LENGTH_LONG).show();
                                        }

                                        String res = "null";
                                        if(snapshot.getData().get("res") != null){
                                            res = snapshot.getData().get("res").toString();
                                        }

                                        long newTurn;
                                        if(snapshot.getData().get("turn") == null){
                                            newTurn = 1;

                                        }
                                        else {
                                            newTurn = ((Long) snapshot.getData().get("turn")).longValue();
                                        }

                                        if(!res.equals("null")){
                                            Toast.makeText(Spielbrett.this, "TODO: result: " + res,
                                                    Toast.LENGTH_LONG).show();
                                            //TODO: Spiel beendet, Ergebnis anzeigen
                                        }

                                        if(newTurn > turn){
                                            //Figuren ziehen:
                                            //TODO: Map<String, String> map = (Map<String, String>) snapshot.getData().get("sb");
                                            //TODO: setzeFiguren(map);

                                            if(ownColorIsWhite && newTurn % 2 == 1 || !ownColorIsWhite && newTurn % 2 == 0){
                                                //TODO: man ist nun am Zug
                                            }
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
                }
                else{
                    Toast.makeText(Spielbrett.this, "Ups...!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setzeFiguren(String sb){
        sb = sb.substring(1, sb.length() - 1);

        String[] items = sb.split(", ");
        String[] tmp;
        Position pos;

        for(String item : items){
            tmp = item.split("=");
            spielbrett[Position.valueOf(tmp[0]).ordinal()].setImageResource(getResourceId(tmp[1]));
        }
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
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

            default:
                //Macht einfach nichts, gilt für "Menü", "Zurück zum Spiel" und alle anderen unbelegten Felder
                break;
        }
        //Wähle Menü aus, damit die anderen Felder wieder anklickbar sind. Menü hat eh keine Funktion
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

    /**
     * Wichtig für Menü. Muss nichts tun
     *
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //keine Aktion nötig
    }

    private int getResourceId(String name){
        int result = 0;

        switch(name){
            case "bw":
                result = R.drawable.bw;
                break;
            case "bs":
                result = R.drawable.bs;
                break;
            case "tw":
                result = R.drawable.tw;
                break;
            case "ts":
                result = R.drawable.ts;
                break;
            case "lw":
                result = R.drawable.lw;
                break;
            case "ls":
                result = R.drawable.ls;
                break;
            case "sw":
                result = R.drawable.sw;
                break;
            case "ss":
                result = R.drawable.ss;
                break;
            case "dw":
                result = R.drawable.dw;
                break;
            case "ds":
                result = R.drawable.ds;
                break;
            case "kw":
                result = R.drawable.kw;
                break;
            case "ks":
                result = R.drawable.ks;
                break;
            default:
                result = 0;
                break;
        }

        return result;
    }

    /**
     * Beendet die Listener, wenn man die Aktivität verlässt. Sonst laufen sie immer weiter.
     */
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
