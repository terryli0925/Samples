package com.terry.samples.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.terry.samples.R;

/**
 * Created by terry on 2016/4/18.
 */
public class BaseActivity extends AppCompatActivity {

    public void setContentFragment(Fragment fragment, boolean addBackStack){
        setContentFragment(R.id.content_none, fragment, addBackStack);
    }

    public void setContentFragment(int containerViewId, Fragment fragment, boolean addBackStack){
        if(fragment != null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(containerViewId, fragment);
            if (addBackStack)
                fragmentTransaction.addToBackStack("tag");
            fragmentTransaction.commit();
        }
    }
}
