package com.terry.samples.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.terry.samples.Config;
import com.terry.samples.R;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class GoogleServiceFragment extends BaseFragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int RC_SIGN_IN = 9001;

    private ImageView mProfileImage;
    private TextView mName, mEmail;
    private EditText mMessage;

    //Signing Options
    private GoogleSignInOptions mGso;

    //google api client
    private GoogleApiClient mGoogleApiClient;

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
        signInButton.setOnClickListener(this);
        view.findViewById(R.id.google_sign_out_button).setOnClickListener(this);

        mMessage = (EditText) view.findViewById(R.id.gcm_message);
        view.findViewById(R.id.gcm_send).setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        configureGoogleSinIn();
    }

    private void configureGoogleSinIn() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

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

        updateUI(acct);
    }

    private void updateUI(GoogleSignInAccount acct) {
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        LogUtils.print(TAG, "onConnectionFailed:" + connectionResult);
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
                        updateUI(null);
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
}
