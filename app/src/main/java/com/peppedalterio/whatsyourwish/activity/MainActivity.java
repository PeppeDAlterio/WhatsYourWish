package com.peppedalterio.whatsyourwish.activity;

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

import com.peppedalterio.whatsyourwish.activity.fragment.ContactsListFragment;
import com.peppedalterio.whatsyourwish.activity.fragment.MyWishlistFragment;
import com.peppedalterio.whatsyourwish.R;
import com.peppedalterio.whatsyourwish.activity.adapter.SectionsPageAdapter;

public class MainActivity extends AppCompatActivity {

    // Identifier for the permission request
    private static final int APP_PERMISSIONS_REQUEST = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermissionsToReadUserContactsAndToReadUserPhoneNumbers();
        }

    }

    /**
     * Setup the UI with a tablayout using a viewpager.
     *
     * <p>
     * This method has to be invoked after that all permissions are granted.
     * </p>
     *
     */
    private void continueWithPermissionsGranted() {
        ViewPager viewPager = findViewById(R.id.container);
        setupViewPage(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /**
     * Setup the ViewPage with 2 Fragments:
     * <br>
     * - Conctacts list fragment
     * <br>
     * - My wishlist fragment
     *
     * <p>
     * This method has to be invoked after that all permissions are granted.
     * </p>
     *
     */
    private void setupViewPage(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ContactsListFragment(), getString(R.string.contactslisttitletab));
        adapter.addFragment(new MyWishlistFragment(), getString(R.string.wishlisttitletab));
        viewPager.setAdapter(adapter);
    }


    /**
     * Request user's permissions to READ_CONTACTS (contact list)
     * and to READ_PHONE STATE (phone number).
     */
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

    /**
     * Check if all permissions requested are granted
     * <p>
     * This method is invoked by onRequestPermissionsResult callback
     * </p>
     *
     * @param grantResults array on int with requested permission results
     * @return true if all permissions are granted, false otherwise
     */
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Callback with the request from calling requestPermissions.
     * <br>
     * This also invokes another method to check if all requested permissions are granted.
     * <br>
     * If so, invokes continueWithPermissionsGranted to display the UI
     *
     */
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
