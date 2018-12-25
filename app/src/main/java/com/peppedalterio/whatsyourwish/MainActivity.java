package com.peppedalterio.whatsyourwish;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Identifier for the permission request
    private static final int APP_PERMISSIONS_REQUEST = 12345;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionsToReadUserContactsAndToReadUserPhoneNumbers();
        }

    }

    private void continueWithPermissionsGranted() {
        viewPager = findViewById(R.id.container);
        setupViewPage(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPage(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ContactsListFragment(), getString(R.string.contactslisttitletab));
        adapter.addFragment(new MyWishlistFragment(), getString(R.string.wishlisttitletab));
        viewPager.setAdapter(adapter);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getPermissionsToReadUserContactsAndToReadUserPhoneNumbers() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE},
                    APP_PERMISSIONS_REQUEST);

        } else {
            continueWithPermissionsGranted();
        }


    }

    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        if (requestCode == APP_PERMISSIONS_REQUEST) {
            if (hasAllPermissionsGranted(grantResults)) {
                continueWithPermissionsGranted();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
