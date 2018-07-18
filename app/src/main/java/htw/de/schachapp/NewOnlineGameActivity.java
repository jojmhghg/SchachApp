package htw.de.schachapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.ToggleButton;

public class NewOnlineGameActivity extends AppCompatActivity {

    // Elemente in View
    private RadioButton m5minRadioButton;
    private RadioButton m10minRadioButton;
    private RadioButton m15minRadioButton;
    private RadioButton m30minRadioButton;
    private RadioButton m60minRadioButton;
    private ToggleButton mColorToggleButton;
    private Button mEnterQueueButton;
    private Button mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_online_game);

        m5minRadioButton = (RadioButton)findViewById(R.id.onlineGame5minRadioButton);
        m10minRadioButton = (RadioButton)findViewById(R.id.onlineGame10minRadioButton);
        m15minRadioButton = (RadioButton)findViewById(R.id.onlineGame15minRadioButton);
        m30minRadioButton = (RadioButton)findViewById(R.id.onlineGame30minRadioButton);
        m60minRadioButton = (RadioButton)findViewById(R.id.onlineGame60minRadioButton);
        mColorToggleButton = (ToggleButton)findViewById(R.id.onlinePartieColorToggleButton);
        mEnterQueueButton = (Button)findViewById(R.id.onlinePartieEnterQueueButton);
        mReturnButton = (Button)findViewById(R.id.onlinePartieReturnButton);

        mEnterQueueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int time;
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

                boolean favColorIsWhite = mColorToggleButton.isChecked();

                //TODO: Daten auswerten und Warteschlange betreten
            }
        });

        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
