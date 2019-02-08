package com.peppedalterio.whatsyourwish;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
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
import com.peppedalterio.whatsyourwish.util.Contact;
import com.peppedalterio.whatsyourwish.util.InternetConnection;
import com.peppedalterio.whatsyourwish.util.WishStrings;

public class UserWishlistActivity extends AppCompatActivity {

    private String effectiveDbNumber;

    private FirebaseDatabase database;

    private ArrayAdapter<String> adapter;

    private Contact contact;

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

        ListView listView = findViewById(R.id.userwishlist);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference();
        Query query = dbRef.orderByKey();

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataChanged(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            onItemLongClick(parent.getItemAtPosition(position).toString(), position);
            return true;
        });

    }

    private void onItemLongClick(String wishData, int pos) {

        //TODO
        /*if(!checkInternetConnection())
            return;*/

        if (wishData.split(WishStrings.SEPARATOR_TOKEN).length>2)
            return;
        TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String simNumber = telemamanger.getLine1Number();

        if(simNumber==null || simNumber.isEmpty())
            return;

        DatabaseReference dbRef = database.getReference(effectiveDbNumber);

        Query query = dbRef.orderByChild(WishStrings.WISH_TITLE_KEY).equalTo(wishData.split(WishStrings.SEPARATOR_TOKEN)[0]).limitToFirst(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren().iterator().hasNext()) {
                    DataSnapshot ds = dataSnapshot.getChildren().iterator().next();
                    ds.child(WishStrings.WISH_ASSIGNEE).getRef().setValue(simNumber);
                    ds.child(WishStrings.PROCESSING_WISH_SINCE).getRef().setValue("01/23/4567");
                    String newItemStr = wishData.concat(appendAssignee(simNumber, "01/23/4567"));
                    adapter.remove(wishData);
                    adapter.insert(newItemStr, pos);
                }
            }//TODO: maybe there's a better solution

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });

    }

    /**
     * This method defines the action to be performed on database data change.
     * <br>
     * It checks if there's a number into the database comparable with the selected one.<br>
     * If so, it invokes loadWishList to show the wishlist associated with the selected phone number.
     *
     */
    private void dataChanged(@NonNull DataSnapshot dataSnapshot) {
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

    /**
     * This method loads the wishlist associated with the requested phone number from the database.
     * <p>
     * This method has to be invoked after a dataChange event
     * </p>
     *
     */
    private void loadWishList() {

        DatabaseReference dbRef = database.getReference(effectiveDbNumber);

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d("ADD", "added: " + dataSnapshot.getValue());

                String str;

                String title = dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class);
                String description = dataSnapshot.child(WishStrings.WISH_DESCRIPTION_KEY).getValue(String.class);
                String assignee = dataSnapshot.child(WishStrings.WISH_ASSIGNEE).getValue(String.class);
                String processingDate = dataSnapshot.child(WishStrings.PROCESSING_WISH_SINCE).getValue(String.class);

                str =   title +
                        WishStrings.SEPARATOR_TOKEN +
                        getString(R.string.userwishlist_description) + ": " + description;

                if(assignee!=null && !assignee.isEmpty()) {
                    str += appendAssignee(assignee, processingDate);
                }

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

    @NonNull
    private String appendAssignee(String assignee, String processingDate) {
        return WishStrings.SEPARATOR_TOKEN +
                getString(R.string.userwishlist_self_assigned) + ": " + assignee +
                WishStrings.SEPARATOR_TOKEN +
                getString(R.string.userwishlist_assign_date) + ": " + processingDate;
    }


}
