package com.terry.samples.fragment;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.terry.samples.Config;
import com.terry.samples.R;
import com.terry.samples.SamplesApplication;
import com.terry.samples.activity.MainActivity;
import com.terry.samples.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleServiceFragment extends BaseFragment implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * The desired interval for location updates.
     * Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates.
     * Exact. Updates will never be more frequent than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private static final int RC_SIGN_IN = 9001;
    public static final int REQUEST_LOCATION = 1000;

    //Signing Options
    private GoogleSignInOptions mGso;

    /**
     * Provides the entry point to Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;

    private ImageView mProfileImage;
    private TextView mName, mEmail;
    private EditText mMessage, mScreenName;
    private TextView mAddress, mLastUpdateTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_google_service, container, false);
        intiView(view);
        return view;
    }

    private void intiView(View view) {
        mProfileImage = (ImageView) view.findViewById(R.id.google_profile_image);
        mName = (TextView) view.findViewById(R.id.google_profile_name);
        mEmail = (TextView) view.findViewById(R.id.google_profile_mail);
        SignInButton signInButton = (SignInButton) view.findViewById(R.id.google_sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
//        signInButton.setScopes(mGso.getScopeArray());
        mMessage = (EditText) view.findViewById(R.id.gcm_message);
        mScreenName = (EditText) view.findViewById(R.id.screen_name);
        mAddress = (TextView) view.findViewById(R.id.address);
        mLastUpdateTime = (TextView) view.findViewById(R.id.location_update_time);

        signInButton.setOnClickListener(this);
        view.findViewById(R.id.google_sign_out_button).setOnClickListener(this);
        view.findViewById(R.id.gcm_send).setOnClickListener(this);
        view.findViewById(R.id.analytics_send).setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        verifyPermission();
    }

    private void buildGoogleApiClient() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .enableAutoManage(getActivity(), this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Uses a {@link com.google.android.gms.location.LocationSettingsRequest.Builder} to build
     * a {@link com.google.android.gms.location.LocationSettingsRequest} that is used for checking
     * if a device has the needed location settings.
     */
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(),
                                    REQUEST_LOCATION);
                            //TODO: Get activity result in Activity
                        } catch (IntentSender.SendIntentException e) {
                            LogUtils.print(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        LogUtils.print(TAG, "Location settings are inadequate, and cannot be fixed here." +
                                " Dialog not created.");
                        break;
                }
            }
        });
    }

    private void verifyPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Config.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Config.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mGoogleApiClient.isConnected()) {
                        getLastLocation();
                    }
                } else {
                    getActivity().finish();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        LogUtils.print(TAG, "handleSignInResult:" + result.isSuccess());

        GoogleSignInAccount acct = null;
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            acct = result.getSignInAccount();
//            String personName = acct.getDisplayName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();
        }

        updateSignInUI(acct);
    }

    private void updateSignInUI(GoogleSignInAccount acct) {
        if (acct != null) {
            if (acct.getPhotoUrl() != null) {
                Glide.with(getActivity()).load(acct.getPhotoUrl()).into(mProfileImage);
            }
            mName.setText(acct.getDisplayName());
            mEmail.setText(acct.getEmail());
        } else {
            mProfileImage.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            mName.setText("");
            mEmail.setText("");
        }
    }

    private void getLastLocation() {
        if (mCurrentLocation != null) {
            LogUtils.print(TAG, String.valueOf(mCurrentLocation.getLatitude()) + "," +
                    String.valueOf(mCurrentLocation.getLongitude()));
            getAddressList(mCurrentLocation);
        }
    }

    private void getAddressList(Location location) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            ioException.printStackTrace();
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            LogUtils.print(TAG, "Latitude = " + location.getLatitude() + ", Longitude = " +
                    location.getLongitude());
            illegalArgumentException.printStackTrace();
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            LogUtils.print(TAG, "No address found");
        } else {
            Address address = addresses.get(0);
//            ArrayList<String> addressFragments = new ArrayList<String>();
//
//            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                addressFragments.add(address.getAddressLine(i));
//            }
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NUL
            mAddress.setText(address.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        getCachedSignIn();
    }

    private void getCachedSignIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            LogUtils.print(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgress(getString(R.string.loading), null);
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    dismissProgress();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        LogUtils.print(TAG, "onConnected");

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mLastUpdateTime.setText(DateFormat.getTimeInstance().format(new Date()));
        getLastLocation();

        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        LogUtils.print(TAG, "onLocationChanged");

        mCurrentLocation = location;
        mLastUpdateTime.setText(DateFormat.getTimeInstance().format(new Date()));
        getLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        LogUtils.print(TAG, "onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        LogUtils.print(TAG, "onConnectionFailed:" + result.getErrorMessage());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signIn();
                break;
            case R.id.google_sign_out_button:
                signOut();
                break;
            case R.id.gcm_send:
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String token = sharedPreferences.getString(Config.PREF_GCM_REGISTRATION_TOKEN, null);
                sendGCM(token, mMessage.getText().toString());
                mMessage.setText("");
                break;
            case R.id.analytics_send:
                sendTrackScreenNameToGA(mScreenName.getText().toString());
                mScreenName.setText("");
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateSignInUI(null);
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }

    private void sendGCM(String token, String msg) {
        if (token == null || msg == null || msg.equals("")) return;

        new AsyncTask<String, Void, Void>() {

            @Override
            protected Void doInBackground(String... params) {
                try {
                    // Create connection to send GCM Message request.
                    URL url = new URL("https://android.googleapis.com/gcm/send");
                    URLConnection connection = url.openConnection();
                    connection.setRequestProperty("Accept-Charset", "UTF-8");
                    connection.setRequestProperty("Authorization", "key=" + Config.GOOGLE_API_KEY);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    // encode output
                    JSONObject custom_msg = new JSONObject();
                    custom_msg.put("message", params[1]);
                    JSONObject data = new JSONObject();
                    data.put("to", params[0]); // 這裏放receiver的token
                    data.put("data", custom_msg);
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data.toString().getBytes("UTF-8"));

                    String resp = readStream(connection.getInputStream());
                    LogUtils.print(TAG, "response= " + resp);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            public String readStream(InputStream in) {
                BufferedReader reader = null;
                StringBuilder builder = new StringBuilder();
                try {
                    reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return builder.toString();
            }
        }.execute(token, msg);
    }

    private void sendTrackScreenNameToGA(String name) {
        if (name == null || name.equals("")) return;

        LogUtils.print(TAG, "Send screen name: " + name);
        // Obtain the shared Tracker instance.
        Tracker tracker = ((SamplesApplication) getActivity().getApplication()).getDefaultTracker();

        tracker.setScreenName(name);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
