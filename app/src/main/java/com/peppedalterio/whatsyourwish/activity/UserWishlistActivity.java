package com.peppedalterio.whatsyourwish.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.peppedalterio.whatsyourwish.R;
import com.peppedalterio.whatsyourwish.model.UserWishlistModel;
import com.peppedalterio.whatsyourwish.util.Contact;
import com.peppedalterio.whatsyourwish.util.InternetConnection;
import com.peppedalterio.whatsyourwish.util.WishStrings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserWishlistActivity extends AppCompatActivity {

    private Contact contact;

    private String simNumber;

    /*
     * This method check if internet connection is available
     */
    private boolean checkInternetConnection() {

        boolean isConnected = InternetConnection.checkForInternetConnection(getApplicationContext());

        if(!isConnected) {
            Log.d("INTERNET", "NO CONNECTION");
            Toast.makeText(getApplicationContext(), getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
        }

        return isConnected;

    }

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist_utente);

        if (!InternetConnection.checkForInternetConnection(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
            finish();
        }

        Intent intent = getIntent();

        if (intent.getSerializableExtra("contact") == null ||
                !(intent.getSerializableExtra("contact") instanceof Contact))
            finish();

        contact = (Contact) intent.getSerializableExtra("contact");


        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        simNumber = telemamanger.getLine1Number();

        if (simNumber!=null && PhoneNumberUtils.compare(simNumber, contact.getPhoneNumber())) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_thiss_my_wishlist), Toast.LENGTH_SHORT).show();
            finish();
        }


        ListView listView = findViewById(R.id.userwishlist);
        ArrayAdapter<String> wishlistListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(wishlistListViewAdapter);

        UserWishlistModel wishlistModel = new UserWishlistModel(contact.getPhoneNumber(), wishlistListViewAdapter,
                /* strings to local-format data */
                getString(R.string.userwishlist_self_assigned), getString(R.string.userwishlist_assign_date),
                getString(R.string.userwishlist_description));
        wishlistModel.getUserWishlist(this);

        listView.setOnItemClickListener(
                (parent, view, position, id) ->
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_long_press_to_self_assign),
                                Toast.LENGTH_SHORT).show()
        );

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            onItemLongClick(wishlistModel, parent.getItemAtPosition(position).toString(), position);
            return true;
        });

    }

    /**
     * This method handles the assignment/de-assignment of the user to the long-clicked wish
     * @param wishData listview item long-clicked
     * @param pos position of the listview item long-clicked
     */
    private void onItemLongClick(UserWishlistModel wishlistModel, String wishData, int pos) {

        if(!checkInternetConnection())
            return;

        /* NB: wish title is unique for a user */
        String wishTitle = wishData.split(WishStrings.SEPARATOR_TOKEN)[0];

        if(simNumber==null || simNumber.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_no_sim_number), Toast.LENGTH_SHORT).show();
            return;
        }

        wishlistModel.assignToAWishRequest(simNumber, wishTitle, wishData, pos, this);

    }

}