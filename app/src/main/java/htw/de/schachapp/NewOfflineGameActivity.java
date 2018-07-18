package htw.de.schachapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public class NewOfflineGameActivity extends AppCompatActivity {

    // Elemente in View
    private RadioButton mInfiniteRadioButton;
    private RadioButton m5minRadioButton;
    private RadioButton m10minRadioButton;
    private RadioButton m15minRadioButton;
    private RadioButton m30minRadioButton;
    private RadioButton m60minRadioButton;
    private ToggleButton mColorToggleButton;
    private TextView mUsernamePlayer2;
    private Button mEnterQueueButton;
    private Button mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_offline_game);

        mInfiniteRadioButton = (RadioButton)findViewById(R.id.offlineGameInfiniteRadioButton);
        m5minRadioButton = (RadioButton)findViewById(R.id.offlineGame5minRadioButton);
        m10minRadioButton = (RadioButton)findViewById(R.id.offlineGame10minRadioButton);
        m15minRadioButton = (RadioButton)findViewById(R.id.offlineGame15minRadioButton);
        m30minRadioButton = (RadioButton)findViewById(R.id.offlineGame30minRadioButton);
        m60minRadioButton = (RadioButton)findViewById(R.id.offlineGame60minRadioButton);
        mColorToggleButton = (ToggleButton)findViewById(R.id.offlineGameColorPlayer1ToggleButton);
        mUsernamePlayer2 = (TextView)findViewById(R.id.offlineGameUsernamePlayer2Input);
        mEnterQueueButton = (Button)findViewById(R.id.offlineGameStartButton);
        mReturnButton = (Button)findViewById(R.id.offlineGameReturnButton);

        mEnterQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int time;
                if(mInfiniteRadioButton.isChecked()){
                    time = 0;
                }
                if(m5minRadioButton.isChecked()){
                    time = 5;
                }
                else if(m10minRadioButton.isChecked()){
                    time = 10;
                }
                else if(m15minRadioButton.isChecked()){
                    time = 15;
                }
                else if(m30minRadioButton.isChecked()){
                    time = 30;
                }
                else if(m60minRadioButton.isChecked()){
                    time = 60;
                }

                boolean colorPlayer1 = mColorToggleButton.isChecked();

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
                    //TODO: Daten auswerten und Warteschlange betreten
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
