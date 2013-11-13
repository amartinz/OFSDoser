/*
 * Copyright (c) 2013. Alexander Martinz.
 */

package net.openfiresecurity.ofsdoser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.openfiresecurity.ofsdoser.activities.PrefActivity;
import net.openfiresecurity.ofsdoser.fragments.DosFragment;
import net.openfiresecurity.ofsdoser.widgets.adapters.ScreenSlidePagerAdapter;
import net.openfiresecurity.ofsdoser.widgets.transformers.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    //====================
    // Fields
    //====================
    private boolean mDebug = false;
    private static long back_pressed;
    //====================
    // Elements
    //====================
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private List<String> mTitleList;
    //====================
    // Others
    //====================
    private SharedPreferences mPrefs;
    private Toast mToast;


    /**
     * Override backbutton presses to exit
     */
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                if (mToast != null)
                    mToast.cancel();
                finish();
            } else {
                mToast = Toast.makeText(getBaseContext(),
                        getString(R.string.action_press_again),
                        Toast.LENGTH_SHORT);
                mToast.show();
            }
            back_pressed = System.currentTimeMillis();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, PrefActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        mDebug = mPrefs.getBoolean("pref_extensive_logging", false);
        logDebug("Extensive Logging: " + (mDebug ? "enabled" : "disabled"));

        enableActionBar();

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getFragmentList(), getTitleList());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(0);
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                getSupportActionBar().setTitle(mTitleList.get(i));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            Runtime.getRuntime().exit(0);
        }
    }

    private List<Fragment> getFragmentList() {
        List<Fragment> mList = new ArrayList<>();

        mList.add(new DosFragment());

        return mList;
    }

    private List<String> getTitleList() {
        mTitleList = new ArrayList<>();

        mTitleList.add(getString(R.string.activity_doser));

        return mTitleList;
    }

    private void enableActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            getActionBar().setHomeButtonEnabled(true);
    }

    private void logDebug(String msg) {
        if (mDebug) {
            Log.e("OFSDOSER", msg);
        }
    }

}