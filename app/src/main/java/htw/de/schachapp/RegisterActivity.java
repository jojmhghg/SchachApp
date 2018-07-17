package htw.de.schachapp;

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
import com.google.firebase.functions.FirebaseFunctions;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();

        Button registerButton = (Button)findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText email = (EditText) findViewById(R.id.emailInput);
                EditText password = (EditText) findViewById(R.id.passwordInput);
                EditText verifyPassword = (EditText) findViewById(R.id.verifyPasswordInput);

                if (!password.getText().toString().equals(verifyPassword.getText().toString())) {
                    //TODO: Fehlermeldung Passwort nicht gleich
                }
                else if(email.getText().toString() == null){

                }
                else{
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        EditText username = (EditText) findViewById(R.id.usernameInput);

                                        if(username.getText().toString().length() < 3){
                                            //TODO: Fehlermeldung -> Username zu kurz
                                        }

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("text", username.getText().toString());
                                        data.put("push", true);
                                        mFunctions.getHttpsCallable("createUser").call(data);

                                        //TODO: Bestätigung anzeigen
                                        finish();
                                    } else {
                                        //TODO: Fehler anzeigen
                                    }
                                }
                            });
                }
            }
        });

        Button returnButton = (Button)findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
