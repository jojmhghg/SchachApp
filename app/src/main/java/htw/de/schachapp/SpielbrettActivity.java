package htw.de.schachapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

public class SpielbrettActivity extends Activity implements AdapterView.OnItemSelectedListener{

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
    private ImageView sb_turn2;
    private ImageView sb_turn1;

    private ImageView loading;
    private AnimationDrawable animationOfLoading;

    //Für Züge:
    private Position clickedField;
    private int clickedColor;
    private Position redMarked;
    private int redMarkedColorBefor;

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

    private final int REQUEST_EXIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielbrett);

        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        username1 = findViewById(R.id.sbName1);
        username2 = findViewById(R.id.sbName2);
        sb_turn2 = findViewById(R.id.sb_turn2);
        sb_turn1 = findViewById(R.id.sb_turn1);
        loading  = findViewById(R.id.loading);

        init = true;
        clickedField = null;
        turn = 1;
        redMarked = null;

        //Übergabeparameter entnehmen
        Intent myIntent = getIntent();
        zufallszahl = myIntent.getLongExtra("chk", 0);
        if(zufallszahl == 0){
            Toast.makeText(SpielbrettActivity.this, "Keine Prüfzahl übergeben!",
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
                    Toast.makeText(SpielbrettActivity.this, R.string.error_get_data,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String newGameId = snapshot.getData().get("game").toString();
                    helpOn = Boolean.parseBoolean(snapshot.getData().get("help").toString());
                    username1.setText(snapshot.getData().get("name").toString());

                    if(gameId != null && !newGameId.equals(gameId)){
                        Toast.makeText(SpielbrettActivity.this, "Ohje... GameId hat sich geändert!",
                                Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(SpielbrettActivity.this, R.string.error_get_data,
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

                                        if(!isOnline){
                                            username2.setText(snapshot.getData().get("name").toString());
                                        }
                                        else{
                                            String idGegner;
                                            if(snapshot.getData().get("id1").toString().equals(mAuth.getUid())){
                                                idGegner = snapshot.getData().get("id2").toString();
                                            }
                                            else{
                                                idGegner = snapshot.getData().get("id1").toString();
                                            }
                                            //Listener auf die User-Daten des Gegners, falls Online(Username, Highlighting & GameId)
                                            final DocumentReference docRef2 = db.collection("user").document(idGegner);
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
                                            Toast.makeText(SpielbrettActivity.this, "Prüfzahl stimmt nicht mehr überein",
                                                    Toast.LENGTH_LONG).show();
                                        }

                                        if(snapshot.getData().get("res") != null){
                                            String res = snapshot.getData().get("res").toString();

                                            Intent intent = new Intent(getApplicationContext(), GameFinishedPopupActivity.class);
                                            intent.putExtra("res", res);
                                            intent.putExtra("isOnline", isOnline);
                                            startActivityForResult(intent, REQUEST_EXIT);
                                        }

                                        long newTurn;
                                        if(snapshot.getData().get("turn") == null){
                                            newTurn = 1;
                                            sb_turn2.setVisibility(View.INVISIBLE);
                                        }
                                        else {
                                            newTurn = ((Long) snapshot.getData().get("turn")).longValue();
                                            if(newTurn % 2 == 1){
                                                sb_turn1.setVisibility(View.VISIBLE);
                                                sb_turn2.setVisibility(View.INVISIBLE);
                                            }
                                            else{
                                                sb_turn1.setVisibility(View.INVISIBLE);
                                                sb_turn2.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        if(newTurn > turn){
                                            //Figuren ziehen:
                                            setzeFiguren(snapshot.getData().get("sb").toString());
                                            turn = newTurn;

                                            if(turn % 2 == 1 && mAuth.getUid().equals(snapshot.getData().get("id1")) ||  turn % 2 == 0 && mAuth.getUid().equals(snapshot.getData().get("id2"))){
                                                if(snapshot.getData().get("offer") != null){
                                                    Intent intent = new Intent(getApplicationContext(), RemisangebotPopupActivity.class);
                                                    intent.putExtra("chk", zufallszahl);
                                                    intent.putExtra("gameId", gameId);
                                                    startActivity(intent);
                                                }
                                            }



                                            if(redMarked != null){
                                                spielbrettImage[redMarked.ordinal()].setBackgroundColor(redMarkedColorBefor);
                                                redMarked = null;
                                            }

                                            if(snapshot.getData().get("schach") != null && (boolean) snapshot.getData().get("schach")){
                                                HashMap<String, String> array = (HashMap<String, String>) snapshot.getData().get("kings");
                                                if(turn % 2 == 1){
                                                    redMarked = Position.valueOf(array.get("kw"));
                                                }
                                                else{
                                                    redMarked = Position.valueOf(array.get("ks"));
                                                }

                                                redMarkedColorBefor = ((ColorDrawable) spielbrettImage[redMarked.ordinal()].getBackground()).getColor();
                                                spielbrettImage[redMarked.ordinal()].setBackgroundColor(getResources().getColor(R.color.colorRed));
                                            }
                                        }
                                    }
                                }
                                else{
                                    Toast.makeText(SpielbrettActivity.this, "Ups...!",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(SpielbrettActivity.this, "Ups...!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setzeFiguren(String sb){
        sb = sb.substring(1, sb.length() - 1);

        String[] items = sb.split(", ");
        String[] tmp;

        for(String item : items){
            tmp = item.split("=");
            spielbrettImage[Position.valueOf(tmp[0]).ordinal()].setImageResource(getResourceId(tmp[1]));
            spielbrettFiguren[Position.valueOf(tmp[0]).ordinal()] = tmp[1];
        }
    }

    private void init(){
        //Menüleiste füllen
        menue = findViewById(R.id.sbMenue);
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
            spielbrettImage[0] = findViewById(R.id.a1);
            spielbrettImage[1] = findViewById(R.id.b1);
            spielbrettImage[2] = findViewById(R.id.c1);
            spielbrettImage[3] = findViewById(R.id.d1);
            spielbrettImage[4] = findViewById(R.id.e1);
            spielbrettImage[5] = findViewById(R.id.f1);
            spielbrettImage[6] = findViewById(R.id.g1);
            spielbrettImage[7] = findViewById(R.id.h1);
            spielbrettImage[8] = findViewById(R.id.a2);
            spielbrettImage[9] = findViewById(R.id.b2);
            spielbrettImage[10] = findViewById(R.id.c2);
            spielbrettImage[11] = findViewById(R.id.d2);
            spielbrettImage[12] = findViewById(R.id.e2);
            spielbrettImage[13] = findViewById(R.id.f2);
            spielbrettImage[14] = findViewById(R.id.g2);
            spielbrettImage[15] = findViewById(R.id.h2);
            spielbrettImage[16] = findViewById(R.id.a3);
            spielbrettImage[17] = findViewById(R.id.b3);
            spielbrettImage[18] = findViewById(R.id.c3);
            spielbrettImage[19] = findViewById(R.id.d3);
            spielbrettImage[20] = findViewById(R.id.e3);
            spielbrettImage[21] = findViewById(R.id.f3);
            spielbrettImage[22] = findViewById(R.id.g3);
            spielbrettImage[23] = findViewById(R.id.h3);
            spielbrettImage[24] = findViewById(R.id.a4);
            spielbrettImage[25] = findViewById(R.id.b4);
            spielbrettImage[26] = findViewById(R.id.c4);
            spielbrettImage[27] = findViewById(R.id.d4);
            spielbrettImage[28] = findViewById(R.id.e4);
            spielbrettImage[29] = findViewById(R.id.f4);
            spielbrettImage[30] = findViewById(R.id.g4);
            spielbrettImage[31] = findViewById(R.id.h4);
            spielbrettImage[32] = findViewById(R.id.a5);
            spielbrettImage[33] = findViewById(R.id.b5);
            spielbrettImage[34] = findViewById(R.id.c5);
            spielbrettImage[35] = findViewById(R.id.d5);
            spielbrettImage[36] = findViewById(R.id.e5);
            spielbrettImage[37] = findViewById(R.id.f5);
            spielbrettImage[38] = findViewById(R.id.g5);
            spielbrettImage[39] = findViewById(R.id.h5);
            spielbrettImage[40] = findViewById(R.id.a6);
            spielbrettImage[41] = findViewById(R.id.b6);
            spielbrettImage[42] = findViewById(R.id.c6);
            spielbrettImage[43] = findViewById(R.id.d6);
            spielbrettImage[44] = findViewById(R.id.e6);
            spielbrettImage[45] = findViewById(R.id.f6);
            spielbrettImage[46] = findViewById(R.id.g6);
            spielbrettImage[47] = findViewById(R.id.h6);
            spielbrettImage[48] = findViewById(R.id.a7);
            spielbrettImage[49] = findViewById(R.id.b7);
            spielbrettImage[50] = findViewById(R.id.c7);
            spielbrettImage[51] = findViewById(R.id.d7);
            spielbrettImage[52] = findViewById(R.id.e7);
            spielbrettImage[53] = findViewById(R.id.f7);
            spielbrettImage[54] = findViewById(R.id.g7);
            spielbrettImage[55] = findViewById(R.id.h7);
            spielbrettImage[56] = findViewById(R.id.a8);
            spielbrettImage[57] = findViewById(R.id.b8);
            spielbrettImage[58] = findViewById(R.id.c8);
            spielbrettImage[59] = findViewById(R.id.d8);
            spielbrettImage[60] = findViewById(R.id.e8);
            spielbrettImage[61] = findViewById(R.id.f8);
            spielbrettImage[62] = findViewById(R.id.g8);
            spielbrettImage[63] = findViewById(R.id.h8);
        }
        else{
            spielbrettImage[63] = findViewById(R.id.a1);
            spielbrettImage[62] = findViewById(R.id.b1);
            spielbrettImage[61] = findViewById(R.id.c1);
            spielbrettImage[60] = findViewById(R.id.d1);
            spielbrettImage[59] = findViewById(R.id.e1);
            spielbrettImage[58] = findViewById(R.id.f1);
            spielbrettImage[57] = findViewById(R.id.g1);
            spielbrettImage[56] = findViewById(R.id.h1);
            spielbrettImage[55] = findViewById(R.id.a2);
            spielbrettImage[54] = findViewById(R.id.b2);
            spielbrettImage[53] = findViewById(R.id.c2);
            spielbrettImage[52] = findViewById(R.id.d2);
            spielbrettImage[51] = findViewById(R.id.e2);
            spielbrettImage[50] = findViewById(R.id.f2);
            spielbrettImage[49] = findViewById(R.id.g2);
            spielbrettImage[48] = findViewById(R.id.h2);
            spielbrettImage[47] = findViewById(R.id.a3);
            spielbrettImage[46] = findViewById(R.id.b3);
            spielbrettImage[45] = findViewById(R.id.c3);
            spielbrettImage[44] = findViewById(R.id.d3);
            spielbrettImage[43] = findViewById(R.id.e3);
            spielbrettImage[42] = findViewById(R.id.f3);
            spielbrettImage[41] = findViewById(R.id.g3);
            spielbrettImage[40] = findViewById(R.id.h3);
            spielbrettImage[39] = findViewById(R.id.a4);
            spielbrettImage[38] = findViewById(R.id.b4);
            spielbrettImage[37] = findViewById(R.id.c4);
            spielbrettImage[36] = findViewById(R.id.d4);
            spielbrettImage[35] = findViewById(R.id.e4);
            spielbrettImage[34] = findViewById(R.id.f4);
            spielbrettImage[33] = findViewById(R.id.g4);
            spielbrettImage[32] = findViewById(R.id.h4);
            spielbrettImage[31] = findViewById(R.id.a5);
            spielbrettImage[30] = findViewById(R.id.b5);
            spielbrettImage[29] = findViewById(R.id.c5);
            spielbrettImage[28] = findViewById(R.id.d5);
            spielbrettImage[27] = findViewById(R.id.e5);
            spielbrettImage[26] = findViewById(R.id.f5);
            spielbrettImage[25] = findViewById(R.id.g5);
            spielbrettImage[24] = findViewById(R.id.h5);
            spielbrettImage[23] = findViewById(R.id.a6);
            spielbrettImage[22] = findViewById(R.id.b6);
            spielbrettImage[21] = findViewById(R.id.c6);
            spielbrettImage[20] = findViewById(R.id.d6);
            spielbrettImage[19] = findViewById(R.id.e6);
            spielbrettImage[18] = findViewById(R.id.f6);
            spielbrettImage[17] = findViewById(R.id.g6);
            spielbrettImage[16] = findViewById(R.id.h6);
            spielbrettImage[15] = findViewById(R.id.a7);
            spielbrettImage[14] = findViewById(R.id.b7);
            spielbrettImage[13] = findViewById(R.id.c7);
            spielbrettImage[12] = findViewById(R.id.d7);
            spielbrettImage[11] = findViewById(R.id.e7);
            spielbrettImage[10] = findViewById(R.id.f7);
            spielbrettImage[9] = findViewById(R.id.g7);
            spielbrettImage[8] = findViewById(R.id.h7);
            spielbrettImage[7] = findViewById(R.id.a8);
            spielbrettImage[6] = findViewById(R.id.b8);
            spielbrettImage[5] = findViewById(R.id.c8);
            spielbrettImage[4] = findViewById(R.id.d8);
            spielbrettImage[3] = findViewById(R.id.e8);
            spielbrettImage[2] = findViewById(R.id.f8);
            spielbrettImage[1] = findViewById(R.id.g8);
            spielbrettImage[0] = findViewById(R.id.h8);

            ImageView tmp = sb_turn1;
            sb_turn1 = sb_turn2;
            sb_turn2 = tmp;

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
                        //Test, ob eigene Figur bzw. Figur am Zug
                        Drawable image = spielbrettImage[pos.ordinal()].getDrawable();
                        if(image != null){
                            if(isOnline && ((ownColorIsWhite && compare_drawble(image, true)) || (!ownColorIsWhite && compare_drawble(image, false)))){
                                if(spielbrettFiguren[pos.ordinal()] != null){
                                    clickedField = pos;
                                    ColorDrawable drawable = (ColorDrawable) clickedView.getBackground();
                                    clickedColor = drawable.getColor();
                                    clickedView.setBackgroundColor(getResources().getColor(R.color.selected));
                                }
                            }
                            else if(turn % 2 == 1 && compare_drawble(image, true) || turn % 2 == 0 && compare_drawble(image, false)){
                                if(spielbrettFiguren[pos.ordinal()] != null){
                                    clickedField = pos;
                                    ColorDrawable drawable = (ColorDrawable) clickedView.getBackground();
                                    clickedColor = drawable.getColor();
                                    clickedView.setBackgroundColor(getResources().getColor(R.color.selected));
                                }
                            }
                        }
                    }
                    else if(clickedField.ordinal() == pos.ordinal()){
                        spielbrettImage[pos.ordinal()].setBackgroundColor(clickedColor);
                        clickedField = null;
                    }
                    else {
                        spielbrettImage[clickedField.ordinal()].setBackgroundColor(clickedColor);

                        if(turn % 2 == 1 && pos.istGundreiheAndereSeite(Farbe.WEISS)
                            && spielbrettImage[clickedField.ordinal()].getDrawable().getConstantState() == getResources().getDrawable(R.drawable.bw).getConstantState()){

                            Intent intent = new Intent(getApplicationContext(), PawnPromotionActivity.class);
                            intent.putExtra("ursprung", clickedField.toString());
                            intent.putExtra("ziel", pos.toString());
                            intent.putExtra("chk", zufallszahl);
                            intent.putExtra("isOnline", isOnline);
                            intent.putExtra("gameId", gameId);
                            intent.putExtra("weissAmZug", true);
                            startActivity(intent);
                        }
                        else if(turn % 2 == 0 && pos.istGundreiheAndereSeite(Farbe.SCHWARZ)
                            && spielbrettImage[clickedField.ordinal()].getDrawable().getConstantState() == getResources().getDrawable(R.drawable.bs).getConstantState()){

                            Intent intent = new Intent(getApplicationContext(), PawnPromotionActivity.class);
                            intent.putExtra("ursprung", clickedField.toString());
                            intent.putExtra("ziel", pos.toString());
                            intent.putExtra("chk", zufallszahl);
                            intent.putExtra("isOnline", isOnline);
                            intent.putExtra("gameId", gameId);
                            intent.putExtra("weissAmZug", false);
                            startActivity(intent);
                        }
                        else {
                            zieheFunction(clickedField, pos);
                        }
                        clickedField = null;
                    }
                }
            });
        }
    }

    private Task<String> zieheFunction(Position ursprung, Position ziel) {
        //Loading Animation Start
        loading = (ImageView) findViewById (R.id.loading);
        loading.setVisibility(View.VISIBLE);
        animationOfLoading = (AnimationDrawable) loading.getDrawable();
        animationOfLoading.start();

        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("chk", zufallszahl);
        data.put("src", ursprung.toString());
        data.put("des", ziel.toString());
        data.put("gameId", gameId);

        if(isOnline){
            return mFunctions.getHttpsCallable("moveOnline").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
                @Override
                public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                    //Loading Animation Stop
                    loading.setVisibility(View.INVISIBLE);
                    animationOfLoading.stop();

                    String result = (String) task.getResult().getData();
                    return result;
                }
            });
        }
        else{
            return mFunctions.getHttpsCallable("moveOffline").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
                @Override
                public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                    //Loading Animation Stop
                    loading.setVisibility(View.INVISIBLE);
                    animationOfLoading.stop();

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
                if(!isOnline){
                    finish();
                }
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
                    Toast.makeText(SpielbrettActivity.this, "Du hast das Spiel verlassen!",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void surrenderClicked(){
        surrenderFunction();
    }

    private Task<String> surrenderFunction() {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("gameId", gameId);
        data.put("chk", zufallszahl);

        return mFunctions.getHttpsCallable("surrender").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
            @Override
            public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                String result = (String) task.getResult().getData();
                return result;
            }
        });
    }

    private void remisClicked(){
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("gameId", gameId);
        data.put("chk", zufallszahl);

        mFunctions.getHttpsCallable("offerDraw").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
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

    private boolean compare_drawble(Drawable image, boolean isWhite){
        if(isWhite){
            if(image.getConstantState() == getResources().getDrawable(R.drawable.bw).getConstantState()){
                return true;
            }
            if(image.getConstantState() == getResources().getDrawable(R.drawable.tw).getConstantState()){
                return true;
            }
            if(image.getConstantState() == getResources().getDrawable(R.drawable.sw).getConstantState()){
                return true;
            }
            if(image.getConstantState() == getResources().getDrawable(R.drawable.lw).getConstantState()){
                return true;
            }
            if(image.getConstantState() == getResources().getDrawable(R.drawable.dw).getConstantState()){
                return true;
            }
            if(image.getConstantState() == getResources().getDrawable(R.drawable.kw).getConstantState()){
                return true;
            }
        }
        else {
            if(image.getConstantState() == getResources().getDrawable(R.drawable.bs).getConstantState()){
                return true;
            }
            if(image.getConstantState() == getResources().getDrawable(R.drawable.ts).getConstantState()){
                return true;
            }
            if(image.getConstantState() == getResources().getDrawable(R.drawable.ss).getConstantState()){
                return true;
            }
            if(image.getConstantState() == getResources().getDrawable(R.drawable.ls).getConstantState()){
                return true;
            }
            if(image.getConstantState() == getResources().getDrawable(R.drawable.ds).getConstantState()){
                return true;
            }
            if(image.getConstantState() == getResources().getDrawable(R.drawable.ks).getConstantState()){
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXIT) {
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        // your code.
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
