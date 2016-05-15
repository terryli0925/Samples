package com.terry.samples.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.terry.samples.Config;
import com.terry.samples.R;
import com.terry.samples.fragment.FacebookServiceFragment;
import com.terry.samples.fragment.GoogleServiceFragment;
import com.terry.samples.fragment.ImageExploreFragment;
import com.terry.samples.fragment.LayoutFragment;
import com.terry.samples.fragment.SQLiteFragment;
import com.terry.samples.fragment.ServiceFragment;
import com.terry.samples.fragment.ToolbarTestFragment;
import com.terry.samples.gcm.RegistrationIntentService;
import com.terry.samples.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private AppBarLayout mAppBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ViewGroup mCollapsedLayout;
    private TextView mToolbarCollapsedTitle;
    private ImageView mCollapsedImage;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        requestPermission();

        initGCM();

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsedLayout = (ViewGroup) findViewById(R.id.collapsed_layout);
        mCollapsedImage = (ImageView) findViewById(R.id.collapsed_image);
        mToolbarCollapsedTitle = (TextView) findViewById(R.id.toolbar_collapsed_title);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set init item in NavigationView
        if (null == savedInstanceState) {
            navigationView.getMenu().getItem(0).setChecked(true);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        }
    }

    private void requestPermission() {
        List<String> permissions = new ArrayList<String>();

        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (ContextCompat.checkSelfPermission(this, PERMISSIONS[i])
                    != PackageManager.PERMISSION_GRANTED) {
                permissions.add(PERMISSIONS[i]);
            }
        }

        if (permissions.size() > 0) {
            // It's use for activity
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]),
                    Config.REQUEST_PERMISSIONS);
        }

        // It's use for fragment
//        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                Config.REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Config.REQUEST_PERMISSIONS:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        finish();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void initGCM() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(Config.PREF_SENT_TOKEN_TO_SERVER, false);
                LogUtils.print(TAG, "sentToken= " + sentToken);
            }

        };
        // Registering BroadcastReceiver
        registerReceiver();

        startGCMService();
    }

    private void startGCMService() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    public void setToolbarExpanded(boolean expanded) {
        mAppBarLayout.setExpanded(expanded, false);
        if (expanded) {
            mCollapsedImage.setVisibility(View.VISIBLE);
            mCollapsedLayout.setVisibility(View.VISIBLE);
        } else {
            mCollapsedImage.setVisibility(View.GONE);
            mCollapsedLayout.setVisibility(View.GONE);
        }
    }

    public void setCollapsedImage(int resId) {
        mCollapsedImage.setImageResource(resId);
    }

    public void setCustomToolbarTitle(String s) {
        mToolbarCollapsedTitle.setText(s);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                LogUtils.print(TAG, "This device is not supported play services.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Config.PREF_REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    @Override
    public void onBackPressed() {
        if (getDrawerLayout().isDrawerOpen(GravityCompat.START)) {
            getDrawerLayout().closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sqlite) {
            replaceFragment(new SQLiteFragment(), false);
        } else if (id == R.id.nav_toolbar) {
            replaceFragment(new LayoutFragment(), false);
        } else if (id == R.id.nav_custom_toolbar) {
            replaceFragment(new ToolbarTestFragment(), false);
        } else if (id == R.id.nav_file_explore) {
            replaceFragment(new ImageExploreFragment(), false);
        } else if (id == R.id.nav_google_service) {
            replaceFragment(new GoogleServiceFragment(), false);
        } else if (id == R.id.nav_facebook_service) {
            replaceFragment(new FacebookServiceFragment(), false);
        } else if (id == R.id.nav_service) {
            replaceFragment(new ServiceFragment(), false);
        }

        getDrawerLayout().closeDrawer(GravityCompat.START);
        return true;
    }
}
