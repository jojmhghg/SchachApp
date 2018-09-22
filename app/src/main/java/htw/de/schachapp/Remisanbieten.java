package htw.de.schachapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

public class Remisanbieten extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.popupfuergewonnen);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int breite = (int)(dm.widthPixels * 0.8);
        int hoehe = (int)(dm.heightPixels * 0.8);

        getWindow().setLayout(breite, hoehe);

    }
}
