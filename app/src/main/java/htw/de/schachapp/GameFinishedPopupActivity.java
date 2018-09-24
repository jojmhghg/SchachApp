package htw.de.schachapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class GameFinishedPopupActivity extends Activity {

    private FirebaseAuth mAuth;

    // Elemente in View
    private Button menue;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_finished_popup);
        mAuth = FirebaseAuth.getInstance();

        message = findViewById(R.id.finish_message);

        //Übergabeparameter entnehmen
        Intent myIntent = getIntent();
        String res = myIntent.getStringExtra("res");
        boolean isOnline = myIntent.getBooleanExtra("isOnline", false);

        if(res.equals("draw")){
            message.setText("Unentschieden!");
        }
        else if(res.equals(mAuth.getUid()) && isOnline){
            message.setText("Du hast gewonnen!");
        }
        else if(!res.equals(mAuth.getUid()) && isOnline){
            message.setText("Du hast verloren!");
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int breite = dm.widthPixels;
        int hoehe = (int)(dm.heightPixels * 0.5);

        getWindow().setLayout(breite, hoehe);

        menue = findViewById(R.id.finish_menue_button);
        menue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        // your code.
    }

}
