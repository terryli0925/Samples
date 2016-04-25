package com.terry.samples.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import com.terry.samples.Config;
import com.terry.samples.utils.LogUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class FetchAddressIntentService extends IntentService {
    private static final String TAG = "FetchAddressIntentService";

    /**
     * The receiver where results are forwarded from this service.
     */
    protected ResultReceiver mReceiver;

    public FetchAddressIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            mReceiver = intent.getParcelableExtra(Config.LOCATION_RECEIVER);
            // Check if receiver was properly registered.
            if (mReceiver == null) {
                LogUtils.print(TAG, "No receiver received. There is nowhere to send the results.");
                return;
            }

            // Get the location passed to this service through an extra.
            Location location = intent.getParcelableExtra(Config.LOCATION_DATA_EXTRA);

            // Make sure that the location data was really sent over through an extra. If it wasn't,
            // send an error error message and return.
            if (location == null) {
                LogUtils.print(TAG, "Location is null");
                return;
            }

            getAddressList(location);
        }
    }

    private void getAddressList(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
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
//            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                addressFragments.add(address.getAddressLine(i));
//            }
            // Fetch the address lines using {@code getAddressLine},
            // join them, and send them to the thread. The {@link android.location.address}
            // class provides other options for fetching address details that you may prefer
            // to use. Here are some examples:
            // getLocality() ("Mountain View", for example)
            // getAdminArea() ("CA", for example)
            // getPostalCode() ("94043", for example)
            // getCountryCode() ("US", for example)
            // getCountryName() ("United States", for example)
            deliverResultToReceiver(Config.SUCCESS_RESULT, address.toString());
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Config.LOCATION_RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

}
