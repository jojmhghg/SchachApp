package htw.de.schachapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

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

        mNewGameButton = (Button)findViewById(R.id.mainNewGameButton);
        mContinueGameButton = (Button)findViewById(R.id.mainContinueGameButton);
        mLoadGameButton = (Button)findViewById(R.id.mainLoadGameButton);
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
                //TODO
                //Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                //startActivity(intent);
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
