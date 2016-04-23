package com.terry.samples.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.terry.samples.R;
import com.terry.samples.activity.MainActivity;
import com.terry.samples.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookServiceFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String REQUEST_FIELDS = TextUtils.join(",",
            new String[]{"id", "name", "picture", "email"});
    private static final String FB_PERMISSIONS = TextUtils.join(",",
            new String[]{"public_profile", "user_friends", "email"});

    private ImageView mProfileImage;
    private TextView mName, mEmail;

    private CallbackManager mCallbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();
    }

    private void fetchUserInfo(AccessToken accessToken) {
        if (accessToken != null) {
            GraphRequest request = GraphRequest.newMeRequest(accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            updateUI(object);
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", REQUEST_FIELDS);
            request.setParameters(parameters);
            request.executeAsync();
        } else {
            updateUI(null);
        }
    }

    private void updateUI(JSONObject jsonUserInfo) {
        if (jsonUserInfo != null) {
            LogUtils.print(TAG, jsonUserInfo.toString());
            // Get photo
            //Method one
//            Glide.with(getActivity()).load("https://graph.facebook.com/"
//                    + jsonUserInfo.optString("id") + "/picture?type=large").into(mProfileImage);
            // Method two
            try {
                String photoUrl = jsonUserInfo.getJSONObject("picture").getJSONObject("data")
                        .getString("url");
                Glide.with(getActivity()).load(photoUrl).into(mProfileImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mName.setText(jsonUserInfo.optString("name"));
            mEmail.setText(jsonUserInfo.optString("email"));
        } else {
            mProfileImage.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
            mName.setText("");
            mEmail.setText("");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_facebook_service, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mProfileImage = (ImageView) view.findViewById(R.id.fb_profile_picture);
        mName = (TextView) view.findViewById(R.id.fb_profile_name);
        mEmail = (TextView) view.findViewById(R.id.fb_profile_mail);

        view.findViewById(R.id.fb_sign_in_button).setOnClickListener(this);
        view.findViewById(R.id.fb_sign_out_button).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchUserInfo(AccessToken.getCurrentAccessToken());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fb_sign_in_button:
                signIn();
                break;
            case R.id.fb_sign_out_button:
                signOut();
                break;
        }
    }

    private void signIn() {
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        LogUtils.print(TAG, "FB onSuccess");
                        fetchUserInfo(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        LogUtils.print(TAG, "FB onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        LogUtils.print(TAG, "FB onError");
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList(FB_PERMISSIONS));
    }

    private void signOut() {
        LoginManager.getInstance().logOut();
        updateUI(null);
    }
}
