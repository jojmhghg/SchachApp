package htw.de.schachapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;

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
                String usernamePlayer1 = mAuth.getCurrentUser().getDisplayName();
                String usernamePlayer2 = mUsernamePlayer2.getText().toString();

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
                    Map<String, Object> data = new HashMap<>();
                    data.put("username1", usernamePlayer1);
                    data.put("username2", usernamePlayer2);
                    data.put("farbe1", colorPlayer1);

                    mFunctions.getHttpsCallable("newOfflineGame").call(data);

                    //TODO: zu neuer Activity gehen & Toast entfernen
                    Toast.makeText(NewOfflineGameActivity.this, "Offline Game erstellt",
                            Toast.LENGTH_LONG).show();
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
}
