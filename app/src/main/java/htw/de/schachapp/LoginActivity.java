package htw.de.schachapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = (EditText)findViewById(R.id.emailInput);
                EditText password = (EditText)findViewById(R.id.passwordText);

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    //TODO: zu Startseite navigieren
                                } else {
                                    //TODO: Fehler anzeigen
                                }
                            }
                        });
            }
        });

        /*
        Button logoutButton = (Button)findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                TextView resultLabel = (TextView)findViewById(R.id.resultLabel);
                resultLabel.setText("ausgeloggt");
            }
        });
        */

        Button registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(startIntent);
            }
        });

        Button resetButton = (Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = (EditText)findViewById(R.id.emailInput);
                mAuth.sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //TODO: successful Nachricht anzeigen
                                } else {
                                    //TODO: Fehlernachricht anezeigen
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            //TODO: Zu Startseite navigieren
        }
    }
}
