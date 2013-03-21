package net.openfiresecurity.ofsdoser;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Running a timer to display a "Splash Screen"
		Thread timer = new Thread() {
			@Override
			public void run() {

				try {
					final SharedPreferences getPrefs = PreferenceManager
							.getDefaultSharedPreferences(getBaseContext());
					boolean splash = getPrefs.getBoolean("splashScreen", true);
					if (splash == true) {
						Thread.sleep(3000);
					}
					startActivity(new Intent(MainActivity.this,
							Hashdostester.class));
					finish();
				} catch (InterruptedException e) {

				}
			}
		};

		timer.start();
	}

}
