package htw.de.schachapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class NewGameActivity extends AppCompatActivity {

    // Elemente in View
    private Button mOnlineGameButton;
    private Button mKiGameButton;
    private Button mOfflineGameButton;
    private Button mReturnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        mOnlineGameButton = (Button)findViewById(R.id.goToNewOnlineGameButton);
        mKiGameButton = (Button)findViewById(R.id.goToNewVsKiGameButton);
        mOfflineGameButton = (Button)findViewById(R.id.goToNewOfflineGameButton);
        mReturnButton = (Button)findViewById(R.id.newGameReturnButton);

        mOnlineGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewOnlineGameActivity.class);
                startActivity(intent);
            }
        });

        mKiGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewKiGameActivity.class);
                startActivity(intent);
            }
        });

        mOfflineGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewOfflineGameActivity.class);
                startActivity(intent);
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
