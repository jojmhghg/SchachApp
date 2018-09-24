package htw.de.schachapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class PawnPromotionActivity extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;

    // Elemente in View
    private ImageView springer;
    private ImageView dame;
    private ImageView laeufer;
    private ImageView turm;

    private ImageView loading;
    private AnimationDrawable animationOfLoading;

    String ursprung;
    String ziel;
    long zufallszahl;
    boolean isOnline;
    String farbChar;
    String gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pawn_promotion);
        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();

        springer = findViewById(R.id.promote_image_springer);
        dame = findViewById(R.id.promote_image_queen);
        laeufer = findViewById(R.id.promote_image_laeufer);
        turm = findViewById(R.id.promote_image_turm);
        loading  = findViewById(R.id.loading);

        //Übergabeparameter entnehmen
        Intent myIntent = getIntent();
        ursprung = myIntent.getStringExtra("ursprung");
        ziel = myIntent.getStringExtra("ziel");
        zufallszahl = myIntent.getLongExtra("chk", 0);
        isOnline = myIntent.getBooleanExtra("isOnline", true);
        gameId = myIntent.getStringExtra("gameId");

        boolean weissAmZug = myIntent.getBooleanExtra("weissAmZug", true);
        farbChar = "w";
        if(!weissAmZug){
            springer.setImageResource(R.drawable.ss);
            dame.setImageResource(R.drawable.ds);
            laeufer.setImageResource(R.drawable.ls);
            turm.setImageResource(R.drawable.ts);

            farbChar = "s";
        }
        else{
            springer.setImageResource(R.drawable.sw);
            dame.setImageResource(R.drawable.dw);
            laeufer.setImageResource(R.drawable.lw);
            turm.setImageResource(R.drawable.tw);
        }

        springer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zieheFunction("s");
                finish();
            }
        });

        dame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zieheFunction("d");
                finish();
            }
        });

        turm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zieheFunction("t");
                finish();
            }
        });

        laeufer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zieheFunction("l");
                finish();
            }
        });

        // Setze Höhe und Breite des PopUps
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int breite = dm.widthPixels;
        int hoehe = (int)(dm.heightPixels * 0.5);
        getWindow().setLayout(breite, hoehe);
    }

    private Task<String> zieheFunction(String figure) {
        //Loading Animation Start
        loading = (ImageView) findViewById (R.id.loading);
        loading.setVisibility(View.VISIBLE);
        animationOfLoading = (AnimationDrawable) loading.getDrawable();
        animationOfLoading.start();

        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("chk", zufallszahl);
        data.put("src", ursprung);
        data.put("des", ziel);
        data.put("figure", figure + farbChar);
        data.put("gameId", gameId);

        if(isOnline){
            return mFunctions.getHttpsCallable("moveAndTransformOnline").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
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
            return mFunctions.getHttpsCallable("moveAndTransformOffline").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
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
    public void onBackPressed() {
        // your code.
    }

}
