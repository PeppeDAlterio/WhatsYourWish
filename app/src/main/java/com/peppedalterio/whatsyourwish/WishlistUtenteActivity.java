package com.peppedalterio.whatsyourwish;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.peppedalterio.whatsyourwish.pojo.Contact;
import com.peppedalterio.whatsyourwish.pojo.WishStrings;

public class WishlistUtenteActivity extends AppCompatActivity {

    private String effectiveDbNumber;

    private FirebaseDatabase database;

    private ArrayAdapter<String> adapter;

    private Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist_utente);

        Intent intent = getIntent();

        if(intent.getSerializableExtra("contact") == null ||
                !(intent.getSerializableExtra("contact") instanceof  Contact))
            finish();

        contact = (Contact) intent.getSerializableExtra("contact");

        ListView listView = findViewById(R.id.userwishlist);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference();
        Query query = dbRef.orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                boolean found = false;

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    if(ds.getKey() != null && PhoneNumberUtils.compare(ds.getKey(), contact.getPhoneNumber())) {
                        effectiveDbNumber = ds.getKey();
                        ((TextView)findViewById(R.id.userwishlistnumber)).setText(effectiveDbNumber);
                        found = true;
                        break;
                    }

                }

                if(found) {
                    loadWishList();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_user_not_found),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });





    }

    private void loadWishList() {

        DatabaseReference dbRef = database.getReference(effectiveDbNumber);

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d("ADD", "added: " + dataSnapshot.getValue());

                String str = "";

                String title = dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class);
                String description = dataSnapshot.child(WishStrings.WISH_DESCRIPTION_KEY).getValue(String.class);
                str += title + WishStrings.SEPARATOR_TOKEN + description;

                adapter.add(str);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("CHANGE", "changed: " + dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("REMOVE", "removed"+dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class));

                String str = dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class) + WishStrings.SEPARATOR_TOKEN +
                        dataSnapshot.child(WishStrings.WISH_DESCRIPTION_KEY).getValue(String.class);

                adapter.remove(str);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("CHANGE", "changed: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
