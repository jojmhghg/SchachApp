package htw.de.schachapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class RemisangebotPopupActivity extends Activity{

    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;

    private Button confirmButton;
    private Button rejectButton;

    private String gameId;
    private long zufallszahl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remisangebot_popup);
        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();

        //Übergabeparameter entnehmen
        Intent myIntent = getIntent();
        zufallszahl = myIntent.getLongExtra("chk", 0);
        if(zufallszahl == 0){
            Toast.makeText(RemisangebotPopupActivity.this, "Keine Prüfzahl übergeben!",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        gameId = myIntent.getStringExtra("gameId");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int breite = (int)(dm.widthPixels);
        int hoehe = (int)(dm.heightPixels * 0.5);
        getWindow().setLayout(breite, hoehe);

        confirmButton = findViewById(R.id.remis_confirm_button);
        rejectButton = findViewById(R.id.remis_reject_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the arguments to the callable function.
                Map<String, Object> data = new HashMap<>();
                data.put("chk", zufallszahl);
                data.put("gameId", gameId);

                mFunctions.getHttpsCallable("confirmDraw").call(data);
                finish();
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the arguments to the callable function.
                Map<String, Object> data = new HashMap<>();
                data.put("chk", zufallszahl);
                data.put("gameId", gameId);

                mFunctions.getHttpsCallable("rejectDraw").call(data);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        // your code.
    }

}
