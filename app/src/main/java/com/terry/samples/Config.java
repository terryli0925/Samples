package com.terry.samples;

/**
 * Created by terry on 2016/4/23.
 */
public class Config {

    public static final String PACKAGE_NAME = "com.terry.samples";

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    //GCM
    public static final String PREF_SENT_TOKEN_TO_SERVER = "pref_sent_token_to_server";
    public static final String PREF_REGISTRATION_COMPLETE = "pref_registration_complete";
    public static final String PREF_GCM_REGISTRATION_TOKEN = "pref_gcm_registration_token";

    public static final String GOOGLE_API_KEY = "";

    // Permission
    public static final int REQUEST_PERMISSIONS = 100;

    // Service
    public static final String LOCATION_RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String LOCATION_RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
}
