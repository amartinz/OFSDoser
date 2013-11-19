/*
 * Copyright (c) 2013. Alexander Martinz.
 */

package net.openfiresecurity.ofsdoser;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.openfiresecurity.ofsdoser.fragments.DosFragment;
import net.openfiresecurity.ofsdoser.fragments.InformationFragment;
import net.openfiresecurity.ofsdoser.fragments.PrefFragment;
import net.openfiresecurity.ofsdoser.util.PreferenceStorage;
import net.openfiresecurity.ofsdoser.widgets.adapters.ScreenSlidePagerAdapter;
import net.openfiresecurity.ofsdoser.widgets.transformers.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    //====================
    // Fragments
    //====================
    public static DosFragment mDosFragment;
    public static InformationFragment mInformationFragment;
    public static PrefFragment mPrefFragment;
    //====================
    // Fields
    //====================
    private boolean mDebug = false;
    private static long back_pressed;
    //====================
    // Elements
    //====================
    private ViewPager mPager;
    private List<String> mTitleList;
    //====================
    // Others
    //====================
    private Toast mToast;
    public ProgressBar mProgress;


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
        getMenuInflater().inflate(R.menu.main, menu);
        mProgress = (ProgressBar) MenuItemCompat.getActionView(menu.findItem(R.id.action_progress))
                .findViewById(R.id.cbpHash);
        mProgress.setVisibility(View.INVISIBLE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_settings:
                mPager.setCurrentItem(2, true);
                /*startActivity(new Intent(MainActivity.this, PrefFragment.class));*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Get hold of our SingleTon
        PreferenceStorage.getInstance(this);

        mDebug = PreferenceStorage.EXTENSIVE_LOGGING;
        logDebug("Extensive Logging: " + (mDebug ? "enabled" : "disabled"));

        enableActionBar();

        mPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), getFragmentList(), getTitleList());
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
        mPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin));
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

        // Get instance and add DosFragment
        mDosFragment = new DosFragment();
        mList.add(mDosFragment);

        // Get instance and add InformationFragment
        mInformationFragment = new InformationFragment();
        mList.add(mInformationFragment);

        // Get instance and add PrefFragment
        mPrefFragment = new PrefFragment();
        mList.add(mPrefFragment);

        return mList;
    }

    private List<String> getTitleList() {
        mTitleList = new ArrayList<>();

        mTitleList.add(getString(R.string.activity_doser));
        mTitleList.add(getString(R.string.activity_information));
        mTitleList.add(getString(R.string.activity_preferences));

        return mTitleList;
    }

    private void enableActionBar() {
        // TODO Set to true later
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            getActionBar().setDisplayHomeAsUpEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            getActionBar().setHomeButtonEnabled(false);
    }

    public void logDebug(String msg) {
        if (mDebug) {
            Log.e("OFSDOSER", msg);
        }
    }

    public void updateInformation() {
        mInformationFragment.update();
    }

}