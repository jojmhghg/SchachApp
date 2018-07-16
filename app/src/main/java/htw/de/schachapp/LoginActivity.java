package htw.de.schachapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);

        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = (EditText)findViewById(R.id.emailText);
                EditText password = (EditText)findViewById(R.id.passwordText);

                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                TextView resultLabel = (TextView)findViewById(R.id.resultLabel);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    resultLabel.setText(user.getUid());
                                } else {
                                    resultLabel.setText("failure");
                                }
                            }
                        });
            }
        });

        Button registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = (EditText)findViewById(R.id.emailText);
                EditText password = (EditText)findViewById(R.id.passwordText);

                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                TextView resultLabel = (TextView)findViewById(R.id.resultLabel);
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    resultLabel.setText(user.getUid());
                                } else {
                                    // If sign in fails, display a message to the user.
                                    /*Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();*/
                                    resultLabel.setText("failure");
                                }
                            }
                        });
            }
        });

        Button logoutButton = (Button)findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                TextView resultLabel = (TextView)findViewById(R.id.resultLabel);
                resultLabel.setText("ausgeloggt");
            }
        });

        Button resetButton = (Button)findViewById(R.id.resetButton);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = (EditText)findViewById(R.id.emailText);
                mAuth.sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                TextView resultLabel = (TextView)findViewById(R.id.resultLabel);
                                if (task.isSuccessful()) {
                                    resultLabel.setText("reset successful");
                                } else {
                                    resultLabel.setText("reset failure");
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

        TextView resultLabel = (TextView)findViewById(R.id.resultLabel);
        if(currentUser != null){
            resultLabel.setText(currentUser.getUid());
        }
        else{
            resultLabel.setText("niemand angemeldet!");
        }
    }
}
