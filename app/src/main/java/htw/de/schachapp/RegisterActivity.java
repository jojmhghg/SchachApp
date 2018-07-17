package htw.de.schachapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

    // Elemente in View
    private EditText mEmail;
    private EditText mUsername;
    private EditText mPassword;
    private EditText mVerifyPassword;
    private Button mRegisterButton;
    private Button mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();

        mEmail = (EditText) findViewById(R.id.registerEmailInput);
        mUsername = (EditText) findViewById(R.id.registerUsernameInput);
        mPassword = (EditText) findViewById(R.id.registerPasswordInput);
        mVerifyPassword = (EditText) findViewById(R.id.registerVerifyPasswordInput);
        mRegisterButton = (Button)findViewById(R.id.registerButton);
        mReturnButton = (Button)findViewById(R.id.registerReturnButton);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void attemptRegister() {
        // Reset errors.
        mEmail.setError(null);
        mPassword.setError(null);
        mUsername.setError(null);
        mVerifyPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String username = mUsername.getText().toString();
        String verifyPassword = mVerifyPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid verify password.
        if (TextUtils.isEmpty(verifyPassword)) {
            mVerifyPassword.setError(getString(R.string.error_field_required));
            focusView = mVerifyPassword;
            cancel = true;
        }
        else if(!isVerifyPasswordValid(verifyPassword)){
            mVerifyPassword.setError(getString(R.string.error_short_password));
            focusView = mVerifyPassword;
            cancel = true;
        }
        else if(!verifyPassword.equals(password)){
            mVerifyPassword.setError(getString(R.string.error_different_password));
            focusView = mVerifyPassword;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        }
        else if(!isPasswordValid(password)){
            mPassword.setError(getString(R.string.error_short_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsername.setError(getString(R.string.error_field_required));
            focusView = mUsername;
            cancel = true;
        }
        else if(!isUsernameValid(username)){
            mUsername.setError(getString(R.string.error_invalid_username));
            focusView = mUsername;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //TODO: showProgress(true);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String username = mUsername.getText().toString();

                                Map<String, Object> data = new HashMap<>();
                                data.put("text", username);
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

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 8;
    }

    private boolean isVerifyPasswordValid(String password) {
        return password.length() >= 8;
    }

    private boolean isUsernameValid(String password) {
        return password.length() >= 3;
    }
}
