package htw.de.schachapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

public class NewOfflineGameActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;

    // Elemente in View
    private ToggleButton mColorToggleButton;
    private TextView mUsernamePlayer2;
    private Button mEnterGameButton;
    private Button mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_offline_game);
        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();

        mColorToggleButton = (ToggleButton)findViewById(R.id.offlineGameColorPlayer1ToggleButton);
        mUsernamePlayer2 = (TextView)findViewById(R.id.offlineGameUsernamePlayer2Input);
        mEnterGameButton = (Button)findViewById(R.id.offlineGameStartButton);
        mReturnButton = (Button)findViewById(R.id.offlineGameReturnButton);



        mEnterGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean colorPlayer1 = mColorToggleButton.isChecked();
                String usernamePlayer2 = mUsernamePlayer2.getText().toString();

                String color = "black";
                if(colorPlayer1){
                    color = "white";
                }

                boolean cancel = false;
                View focusView = null;
                // Check for a valid username.
                if (TextUtils.isEmpty(usernamePlayer2)) {
                    mUsernamePlayer2.setError(getString(R.string.error_field_required));
                    focusView = mUsernamePlayer2;
                    cancel = true;
                } else if (!isUsernameValid(usernamePlayer2)) {
                    mUsernamePlayer2.setError(getString(R.string.error_invalid_username));
                    focusView = mUsernamePlayer2;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    newOfflineGame(usernamePlayer2, color).addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Exception e = task.getException();
                                if (e instanceof FirebaseFunctionsException) {
                                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                    FirebaseFunctionsException.Code code = ffe.getCode();
                                    Object details = ffe.getDetails();
                                }

                                // ...
                            }
                            // ...
                            else {
                                Toast.makeText(NewOfflineGameActivity.this, "Offline Game erstellt!",
                                        Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), Spielbrett.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 3;
    }

    private Task<String> newOfflineGame(String usernamePlayer2, String colorPlayer1) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("namePlayer2", usernamePlayer2);
        data.put("farbe1", colorPlayer1);

        return mFunctions.getHttpsCallable("newOfflineGame").call(data).continueWith(new Continuation<HttpsCallableResult, String>() {
            @Override
            public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                // This continuation runs on either success or failure, but if the task
                // has failed then getResult() will throw an Exception which will be
                // propagated down.

                String result = (String) task.getResult().getData();
                return result;
            }
        });
    }
}
