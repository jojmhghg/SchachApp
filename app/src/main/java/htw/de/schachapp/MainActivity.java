package htw.de.schachapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
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
        mLoadGameButton = (Button)findViewById(R.id.mainRanglisteButton);
        mSettingsButton = (Button)findViewById(R.id.mainSettingsButton);
        mLogoutButton = (Button)findViewById(R.id.mainLogoutButton);

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
                //Lade gameID
                db.collection("user")
                        .document(mAuth.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                String gameId = document.get("game").toString();

                                //Lade zufallszahl
                                db.collection("games")
                                        .document(gameId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                long zufallszahl = (Long)document.get("chk");

                                                Intent intent = new Intent(getApplicationContext(), SpielbrettActivity.class);
                                                intent.putExtra("chk", zufallszahl);
                                                startActivity(intent);
                                            }
                                        });
                            }
                        });
            }
        });

        mLoadGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RanglisteActivity.class);
                startActivity(intent);
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
