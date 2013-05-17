/*
 * Copyright (c) 2013. Alexander Martinz.
 */

package net.openfiresecurity.ofsdoser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Running a timer to display a "Splash Screen"
        @NotNull Thread timer = new Thread() {
            @Override
            public void run() {

                try {
                    final SharedPreferences getPrefs = PreferenceManager
                            .getDefaultSharedPreferences(getBaseContext());
                    boolean splash = getPrefs.getBoolean("splashScreen", true);
                    if (splash) {
                        Thread.sleep(3000);
                    }
                    startActivity(new Intent(MainActivity.this,
                            Hashdostester.class));
                    finish();
                } catch (InterruptedException ignored) {

                }
            }
        };

        timer.start();
    }

}
