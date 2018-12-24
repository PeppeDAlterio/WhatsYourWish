package com.peppedalterio.whatsyourwish;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.peppedalterio.whatsyourwish.pojo.Contact;

public class WishlistUtenteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist_utente);

        Contact contact;

        Intent intent = getIntent();
        
        if(intent.getSerializableExtra("contact") != null && intent.getSerializableExtra("contact") instanceof  Contact)
            contact = (Contact) intent.getSerializableExtra("contact");

        Log.i("EXTRA", "NAME="+contact.getName());
        Log.i("EXTRA", "NUMBER="+contact.getPhoneNumber());

    }


}
