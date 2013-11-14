/*
 * Copyright (c) 2013. Alexander Martinz.
 */

package net.openfiresecurity.ofsdoser.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import net.openfiresecurity.ofsdoser.MainActivity;
import net.openfiresecurity.ofsdoser.R;

public class PrefActivity extends PreferenceActivity {

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

        enableActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If home icon is clicked return to main Activity
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    private void enableActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            getActionBar().setHomeButtonEnabled(true);
    }

}
