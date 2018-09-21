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

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;
    private FirebaseFirestore db;

    //Android-Objekte
    private Spinner menue;
    private TextView username1;
    private TextView username2;
    private ImageView[] spielbrettImage;
    private String[] spielbrettFiguren;
    private ImageView boxSpieler1;
    private ImageView boxSpieler2;

    //Für Züge:
    private Position clickedField;

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
        boxSpieler1 = (ImageView) findViewById(R.id.imageView2);
        boxSpieler2 = (ImageView) findViewById(R.id.imageView);

        init = true;
        clickedField = null;
        turn = 1;

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

                                        //Eigene Farbe bestimmen
                                        if(snapshot.getData().get("id1") != null && snapshot.getData().get("id1").toString().equals(mAuth.getUid())){
                                            ownColorIsWhite = true;
                                        }
                                        else{
                                            ownColorIsWhite = false;

                                        }
                                        //Felder in Array speichern
                                        initSpielbrett(ownColorIsWhite);
                                        //Figuren setzen:
                                        setzeFiguren(snapshot.getData().get("sb").toString());

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
                                            setzeFiguren(snapshot.getData().get("sb").toString());

                                            if(ownColorIsWhite && newTurn % 2 == 1 || !ownColorIsWhite && newTurn % 2 == 0){
                                                //TODO: man ist nun am Zug
                                            }

                                            turn = newTurn;
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
            spielbrettImage[Position.valueOf(tmp[0]).ordinal()].setImageResource(getResourceId(tmp[1]));
            spielbrettFiguren[Position.valueOf(tmp[0]).ordinal()] = tmp[1];
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

    private void initSpielbrett(boolean isWhite){
        spielbrettImage = new ImageView[64];
        if(isWhite) {
            spielbrettImage[0] = (ImageView) findViewById(R.id.a1);
            spielbrettImage[1] = (ImageView) findViewById(R.id.b1);
            spielbrettImage[2] = (ImageView) findViewById(R.id.c1);
            spielbrettImage[3] = (ImageView) findViewById(R.id.d1);
            spielbrettImage[4] = (ImageView) findViewById(R.id.e1);
            spielbrettImage[5] = (ImageView) findViewById(R.id.f1);
            spielbrettImage[6] = (ImageView) findViewById(R.id.g1);
            spielbrettImage[7] = (ImageView) findViewById(R.id.h1);
            spielbrettImage[8] = (ImageView) findViewById(R.id.a2);
            spielbrettImage[9] = (ImageView) findViewById(R.id.b2);
            spielbrettImage[10] = (ImageView) findViewById(R.id.c2);
            spielbrettImage[11] = (ImageView) findViewById(R.id.d2);
            spielbrettImage[12] = (ImageView) findViewById(R.id.e2);
            spielbrettImage[13] = (ImageView) findViewById(R.id.f2);
            spielbrettImage[14] = (ImageView) findViewById(R.id.g2);
            spielbrettImage[15] = (ImageView) findViewById(R.id.h2);
            spielbrettImage[16] = (ImageView) findViewById(R.id.a3);
            spielbrettImage[17] = (ImageView) findViewById(R.id.b3);
            spielbrettImage[18] = (ImageView) findViewById(R.id.c3);
            spielbrettImage[19] = (ImageView) findViewById(R.id.d3);
            spielbrettImage[20] = (ImageView) findViewById(R.id.e3);
            spielbrettImage[21] = (ImageView) findViewById(R.id.f3);
            spielbrettImage[22] = (ImageView) findViewById(R.id.g3);
            spielbrettImage[23] = (ImageView) findViewById(R.id.h3);
            spielbrettImage[24] = (ImageView) findViewById(R.id.a4);
            spielbrettImage[25] = (ImageView) findViewById(R.id.b4);
            spielbrettImage[26] = (ImageView) findViewById(R.id.c4);
            spielbrettImage[27] = (ImageView) findViewById(R.id.d4);
            spielbrettImage[28] = (ImageView) findViewById(R.id.e4);
            spielbrettImage[29] = (ImageView) findViewById(R.id.f4);
            spielbrettImage[30] = (ImageView) findViewById(R.id.g4);
            spielbrettImage[31] = (ImageView) findViewById(R.id.h4);
            spielbrettImage[32] = (ImageView) findViewById(R.id.a5);
            spielbrettImage[33] = (ImageView) findViewById(R.id.b5);
            spielbrettImage[34] = (ImageView) findViewById(R.id.c5);
            spielbrettImage[35] = (ImageView) findViewById(R.id.d5);
            spielbrettImage[36] = (ImageView) findViewById(R.id.e5);
            spielbrettImage[37] = (ImageView) findViewById(R.id.f5);
            spielbrettImage[38] = (ImageView) findViewById(R.id.g5);
            spielbrettImage[39] = (ImageView) findViewById(R.id.h5);
            spielbrettImage[40] = (ImageView) findViewById(R.id.a6);
            spielbrettImage[41] = (ImageView) findViewById(R.id.b6);
            spielbrettImage[42] = (ImageView) findViewById(R.id.c6);
            spielbrettImage[43] = (ImageView) findViewById(R.id.d6);
            spielbrettImage[44] = (ImageView) findViewById(R.id.e6);
            spielbrettImage[45] = (ImageView) findViewById(R.id.f6);
            spielbrettImage[46] = (ImageView) findViewById(R.id.g6);
            spielbrettImage[47] = (ImageView) findViewById(R.id.h6);
            spielbrettImage[48] = (ImageView) findViewById(R.id.a7);
            spielbrettImage[49] = (ImageView) findViewById(R.id.b7);
            spielbrettImage[50] = (ImageView) findViewById(R.id.c7);
            spielbrettImage[51] = (ImageView) findViewById(R.id.d7);
            spielbrettImage[52] = (ImageView) findViewById(R.id.e7);
            spielbrettImage[53] = (ImageView) findViewById(R.id.f7);
            spielbrettImage[54] = (ImageView) findViewById(R.id.g7);
            spielbrettImage[55] = (ImageView) findViewById(R.id.h7);
            spielbrettImage[56] = (ImageView) findViewById(R.id.a8);
            spielbrettImage[57] = (ImageView) findViewById(R.id.b8);
            spielbrettImage[58] = (ImageView) findViewById(R.id.c8);
            spielbrettImage[59] = (ImageView) findViewById(R.id.d8);
            spielbrettImage[60] = (ImageView) findViewById(R.id.e8);
            spielbrettImage[61] = (ImageView) findViewById(R.id.f8);
            spielbrettImage[62] = (ImageView) findViewById(R.id.g8);
            spielbrettImage[63] = (ImageView) findViewById(R.id.h8);
        }
        else{
            spielbrettImage[63] = (ImageView) findViewById(R.id.a1);
            spielbrettImage[62] = (ImageView) findViewById(R.id.b1);
            spielbrettImage[61] = (ImageView) findViewById(R.id.c1);
            spielbrettImage[60] = (ImageView) findViewById(R.id.d1);
            spielbrettImage[59] = (ImageView) findViewById(R.id.e1);
            spielbrettImage[58] = (ImageView) findViewById(R.id.f1);
            spielbrettImage[57] = (ImageView) findViewById(R.id.g1);
            spielbrettImage[56] = (ImageView) findViewById(R.id.h1);
            spielbrettImage[55] = (ImageView) findViewById(R.id.a2);
            spielbrettImage[54] = (ImageView) findViewById(R.id.b2);
            spielbrettImage[53] = (ImageView) findViewById(R.id.c2);
            spielbrettImage[52] = (ImageView) findViewById(R.id.d2);
            spielbrettImage[51] = (ImageView) findViewById(R.id.e2);
            spielbrettImage[50] = (ImageView) findViewById(R.id.f2);
            spielbrettImage[49] = (ImageView) findViewById(R.id.g2);
            spielbrettImage[48] = (ImageView) findViewById(R.id.h2);
            spielbrettImage[47] = (ImageView) findViewById(R.id.a3);
            spielbrettImage[46] = (ImageView) findViewById(R.id.b3);
            spielbrettImage[45] = (ImageView) findViewById(R.id.c3);
            spielbrettImage[44] = (ImageView) findViewById(R.id.d3);
            spielbrettImage[43] = (ImageView) findViewById(R.id.e3);
            spielbrettImage[42] = (ImageView) findViewById(R.id.f3);
            spielbrettImage[41] = (ImageView) findViewById(R.id.g3);
            spielbrettImage[40] = (ImageView) findViewById(R.id.h3);
            spielbrettImage[39] = (ImageView) findViewById(R.id.a4);
            spielbrettImage[38] = (ImageView) findViewById(R.id.b4);
            spielbrettImage[37] = (ImageView) findViewById(R.id.c4);
            spielbrettImage[36] = (ImageView) findViewById(R.id.d4);
            spielbrettImage[35] = (ImageView) findViewById(R.id.e4);
            spielbrettImage[34] = (ImageView) findViewById(R.id.f4);
            spielbrettImage[33] = (ImageView) findViewById(R.id.g4);
            spielbrettImage[32] = (ImageView) findViewById(R.id.h4);
            spielbrettImage[31] = (ImageView) findViewById(R.id.a5);
            spielbrettImage[30] = (ImageView) findViewById(R.id.b5);
            spielbrettImage[29] = (ImageView) findViewById(R.id.c5);
            spielbrettImage[28] = (ImageView) findViewById(R.id.d5);
            spielbrettImage[27] = (ImageView) findViewById(R.id.e5);
            spielbrettImage[26] = (ImageView) findViewById(R.id.f5);
            spielbrettImage[25] = (ImageView) findViewById(R.id.g5);
            spielbrettImage[24] = (ImageView) findViewById(R.id.h5);
            spielbrettImage[23] = (ImageView) findViewById(R.id.a6);
            spielbrettImage[22] = (ImageView) findViewById(R.id.b6);
            spielbrettImage[21] = (ImageView) findViewById(R.id.c6);
            spielbrettImage[20] = (ImageView) findViewById(R.id.d6);
            spielbrettImage[19] = (ImageView) findViewById(R.id.e6);
            spielbrettImage[18] = (ImageView) findViewById(R.id.f6);
            spielbrettImage[17] = (ImageView) findViewById(R.id.g6);
            spielbrettImage[16] = (ImageView) findViewById(R.id.h6);
            spielbrettImage[15] = (ImageView) findViewById(R.id.a7);
            spielbrettImage[14] = (ImageView) findViewById(R.id.b7);
            spielbrettImage[13] = (ImageView) findViewById(R.id.c7);
            spielbrettImage[12] = (ImageView) findViewById(R.id.d7);
            spielbrettImage[11] = (ImageView) findViewById(R.id.e7);
            spielbrettImage[10] = (ImageView) findViewById(R.id.f7);
            spielbrettImage[9] = (ImageView) findViewById(R.id.g7);
            spielbrettImage[8] = (ImageView) findViewById(R.id.h7);
            spielbrettImage[7] = (ImageView) findViewById(R.id.a8);
            spielbrettImage[6] = (ImageView) findViewById(R.id.b8);
            spielbrettImage[5] = (ImageView) findViewById(R.id.c8);
            spielbrettImage[4] = (ImageView) findViewById(R.id.d8);
            spielbrettImage[3] = (ImageView) findViewById(R.id.e8);
            spielbrettImage[2] = (ImageView) findViewById(R.id.f8);
            spielbrettImage[1] = (ImageView) findViewById(R.id.g8);
            spielbrettImage[0] = (ImageView) findViewById(R.id.h8);

            for(int i = 0; i < 64; i++){
                spielbrettImage[i].setContentDescription(Position.values()[i].toString());
            }
        }

        spielbrettFiguren = new String[64];

        for(ImageView iv : spielbrettImage){
            iv.setClickable(true);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView clickedView = (ImageView) v;
                    Position pos = Position.valueOf(clickedView.getContentDescription().toString());

                    if(clickedField == null){
                        if(spielbrettFiguren[pos.ordinal()] != null){
                            clickedField = pos;
                        }
                    }
                    else if(clickedField.ordinal() == pos.ordinal()){
                        //TODO: selbes Feld nochmal angeklickt
                    }
                    else{
                        //TODO: ziehe Figur
                        zieheFunction(clickedField, pos).addOnCompleteListener(new OnCompleteListener<String>() {
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
                                    Toast.makeText(Spielbrett.this, "gezogen",
                                            Toast.LENGTH_SHORT).show();

                                    //TODO: alles was nach ziehen passieren muss
                                }
                            }
                        });
                        clickedField = null;
                    }
                }
            });
        }
    }

    private Task<String> zieheFunction(Position ursprung, Position ziel) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("gameId", gameId);
        data.put("src", ursprung.toString());
        data.put("des", ziel.toString());

        if(isOnline){
            return mFunctions.getHttpsCallable("moveOnline").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
                @Override
                public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                    String result = (String) task.getResult().getData();
                    return result;
                }
            });
        }
        else{
            return mFunctions.getHttpsCallable("moveOffline").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
                @Override
                public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                    String result = (String) task.getResult().getData();
                    return result;
                }
            });
        }

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
