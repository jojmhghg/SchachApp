package htw.de.schachapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RanglisteActivity extends Activity {

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //View Elemente
    private Button return_button;

    private TextView name1;
    private TextView name2;
    private TextView name3;
    private TextView name4;
    private TextView name5;
    private TextView nameX;

    private TextView punkte1;
    private TextView punkte2;
    private TextView punkte3;
    private TextView punkte4;
    private TextView punkte5;
    private TextView punkteX;

    private TextView ergebnise1;
    private TextView ergebnise2;
    private TextView ergebnise3;
    private TextView ergebnise4;
    private TextView ergebnise5;
    private TextView ergebniseX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rangliste);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        return_button = findViewById(R.id.rang_back);
        name1 = findViewById(R.id.rang_name_1);
        name2 = findViewById(R.id.rang_name_2);
        name3 = findViewById(R.id.rang_name_3);
        name4 = findViewById(R.id.rang_name_4);
        name5 = findViewById(R.id.rang_name_5);
        nameX = findViewById(R.id.rang_name_x);

        punkte1 = findViewById(R.id.rang_points_1);
        punkte2 = findViewById(R.id.rang_points_2);
        punkte3 = findViewById(R.id.rang_points_3);
        punkte4 = findViewById(R.id.rang_points_4);
        punkte5 = findViewById(R.id.rang_points_5);
        punkteX = findViewById(R.id.rang_points_x);

        ergebnise1 = findViewById(R.id.rang_wld_1);
        ergebnise2 = findViewById(R.id.rang_wld_2);
        ergebnise3 = findViewById(R.id.rang_wld_3);
        ergebnise4 = findViewById(R.id.rang_wld_4);
        ergebnise5 = findViewById(R.id.rang_wld_5);
        ergebniseX = findViewById(R.id.rang_wld_x);


        //TODO: über db die Daten der Rangliste laden.

        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
