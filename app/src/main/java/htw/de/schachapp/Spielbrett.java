package htw.de.schachapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;

public class Spielbrett extends Activity {

    private FirebaseAuth mAuth;
    private FirebaseFunctions mFunctions;
    private FirebaseFirestore db;

    private Spinner menue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielbrett);

        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        db = FirebaseFirestore.getInstance();

        //Menüleiste füllen
        menue = (Spinner) findViewById(R.id.sbMenue);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.sbMenue, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menue.setAdapter(adapter);
    }

}
