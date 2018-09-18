package htw.de.schachapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;
    private FirebaseFirestore db;

    // Elemente in View
    private Button mNewGameButton;
    private Button mContinueGameButton;
    private Button mLoadGameButton;
    private Button mSettingsButton;
    private Button mLogoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        mNewGameButton = (Button)findViewById(R.id.mainNewGameButton);
        mContinueGameButton = (Button)findViewById(R.id.mainContinueGameButton);
        mLoadGameButton = (Button)findViewById(R.id.mainLoadGameButton);
        mSettingsButton = (Button)findViewById(R.id.mainSettingsButton);
        mLogoutButton = (Button)findViewById(R.id.mainLogoutButton);

        /* Test zum Lesen aus der DB & Toasts
        // Username in Feld eintragen & Highlighing-Switch einstellen
        final DocumentReference docRef = db.collection("games").document("vAPAmc2HvRUT5FyTdE8w");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Toast.makeText(MainActivity.this, R.string.error_get_data,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Toast.makeText(MainActivity.this, "winner",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "test",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        */

        mNewGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewGameActivity.class);
                startActivity(intent);
            }
        });

        mContinueGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            /*Test für Surrender
                Map<String, Object> data = new HashMap<>();
                data.put("gameId", "vAPAmc2HvRUT5FyTdE8w");

                mFunctions.getHttpsCallable("surrender").call(data);
                //TODO
                //Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                //startActivity(intent);
            */
            }
        });

        mLoadGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                //Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                //startActivity(intent);
            }
        });
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                finish();
            }
        });

    }

}
