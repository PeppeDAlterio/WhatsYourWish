package com.peppedalterio.whatsyourwish.activity;

import android.annotation.SuppressLint;
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
import com.peppedalterio.whatsyourwish.R;
import com.peppedalterio.whatsyourwish.util.Contact;
import com.peppedalterio.whatsyourwish.util.InternetConnection;
import com.peppedalterio.whatsyourwish.util.WishStrings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserWishlistActivity extends AppCompatActivity {

    private String effectiveDbNumber;

    private FirebaseDatabase database;

    private ArrayAdapter<String> adapter;

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

        listView.setOnItemClickListener(
                (parent, view, position, id) ->
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_long_press_to_self_assign),
                                Toast.LENGTH_SHORT).show()
        );

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            onItemLongClick(parent.getItemAtPosition(position).toString(), position);
            return true;
        });

    }

    /**
     * This method handles the assignment/de-assignment of the user to the long-clicked wish
     * @param wishData listview item long-clicked
     * @param pos position of the listview item long-clicked
     */
    private void onItemLongClick(String wishData, int pos) {

        if(!checkInternetConnection())
            return;

        /* NB: wish title is unique for a user */
        String wishTitle = wishData.split(WishStrings.SEPARATOR_TOKEN)[0];

        if(simNumber==null || simNumber.isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_no_sim_number), Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference dbRef = database.getReference(effectiveDbNumber);

        Query query = dbRef.orderByChild(WishStrings.WISH_TITLE_KEY).equalTo(wishTitle).limitToFirst(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren().iterator().hasNext()) {

                    DataSnapshot ds = dataSnapshot.getChildren().iterator().next();
                    /* wish data from DB */
                    String currentAssignee = (String) ds.child(WishStrings.WISH_ASSIGNEE).getValue();
                    String wishDescription = (String) ds.child(WishStrings.WISH_DESCRIPTION_KEY).getValue();
                    String currentAssigneeDate = (String) ds.child(WishStrings.PROCESSING_WISH_SINCE).getValue();

                    /*
                        Taking current status of the assignee to decide if it's possible to
                        self-assign the wish.
                        case 1: assigned to another person -> do nothing
                        case 2: assigned to yourself -> delete the assignment
                        case 3: not assigned -> assign to yourself
                     */

                    String newAssignee;
                    String newAssigneeDate;
                    String newWishItemStr;

                    /* case 1: assigned to another person -> update displayed info if the current assignee is not displayed */
                    if ( currentAssignee!=null && !currentAssignee.isEmpty() && !PhoneNumberUtils.compare(simNumber, currentAssignee)) {
                        Log.d("ASSIGNEE", "case_1 - simNumber: " + simNumber + "| assignee: " + currentAssignee);
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_assigned_to_another_one), Toast.LENGTH_SHORT).show();
                        // updating displayed info with current assignee if currently not displayed
                        if(!wishData.contains(": "+currentAssignee+WishStrings.SEPARATOR_TOKEN)) {
                            newWishItemStr = formatWishDataStr(wishTitle, wishDescription, currentAssignee, currentAssigneeDate);
                            //newWishItemStr = wishData.concat(appendAssignee(currentAssignee, currentAssigneeDate));
                            adapter.remove(wishData);
                            adapter.insert(newWishItemStr, pos);
                        }
                        return;
                    }
                    /* case 2: assigned to yourself -> delete the assignment */
                    else if ( currentAssignee!=null && !currentAssignee.isEmpty() && PhoneNumberUtils.compare(simNumber, currentAssignee)) {
                        Log.d("ASSIGNEE", "case_2");
                        newAssignee = "";
                        newAssigneeDate = "";
                        /*newWishItemStr = wishTitle;
                        if (wishDescription!=null && !wishDescription.isEmpty()) {
                            newWishItemStr +=   WishStrings.SEPARATOR_TOKEN +
                                            wishDescription;
                        }*/
                    }
                    /* case 3: not assigned -> assign to yourself */
                    else {
                        Log.d("ASSIGNEE", "case_3");
                        Date todayDate = Calendar.getInstance().getTime();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        newAssigneeDate = formatter.format(todayDate);
                        newAssignee = simNumber;
                        //newWishItemStr = wishData.concat(appendAssignee(newAssignee, newAssigneeDate));
                    }

                    // format new wish list item string
                    newWishItemStr = formatWishDataStr(wishTitle, wishDescription, newAssignee, newAssigneeDate);

                    /* apply the change */
                    ds.child(WishStrings.WISH_ASSIGNEE).getRef().setValue(newAssignee);
                    ds.child(WishStrings.PROCESSING_WISH_SINCE).getRef().setValue(newAssigneeDate);
                    adapter.remove(wishData);
                    adapter.insert(newWishItemStr, pos);

                    /* show and informative toast */
                    if(newAssignee.isEmpty()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_remove_assignment), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_self_assign), Toast.LENGTH_SHORT).show();
                    }

                }
            }

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

                String wishDataStr;

                String title = dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class);
                String description = dataSnapshot.child(WishStrings.WISH_DESCRIPTION_KEY).getValue(String.class);
                String assignee = dataSnapshot.child(WishStrings.WISH_ASSIGNEE).getValue(String.class);
                String processingDate = dataSnapshot.child(WishStrings.PROCESSING_WISH_SINCE).getValue(String.class);

                wishDataStr = formatWishDataStr(title, description, assignee, processingDate);
                /*
                wishDataStr = title;

                if (description!=null && !description.isEmpty()) {
                    wishDataStr +=  WishStrings.SEPARATOR_TOKEN +
                                    getString(R.string.userwishlist_description) + ": " + description;
                }

                if(assignee!=null && !assignee.isEmpty()) {
                    wishDataStr += appendAssignee(assignee, processingDate);
                }
                */
                adapter.add(wishDataStr);

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

    //TODO: JUnit test this method
    /**
        This method appends assignee's information to the listview item for the wish:
        assignee: phone number of the assignee
        processingDate: the date on which it was taken over
     */
    @NonNull
    private String appendAssignee(String assignee, String processingDate) {
        return WishStrings.SEPARATOR_TOKEN +
                getString(R.string.userwishlist_self_assigned) + ": " + assignee +
                WishStrings.SEPARATOR_TOKEN +
                getString(R.string.userwishlist_assign_date) + ": " + processingDate;
    }

    //TODO: JUnit test this method
    /**
     * This methos formats the wish data String to be displayed into the list view.
     * @param title wish title
     * @param description wish description
     * @param assignee wish assignee
     * @param processingDate date on which the assignee took over the wish
     * @return Formatted string for list view item for user wish list
     */
    private String formatWishDataStr(@NonNull String title, String description, String assignee, String processingDate) {

        String wishDataStr;

        wishDataStr = title;

        if (description!=null && !description.isEmpty()) {
            wishDataStr +=  WishStrings.SEPARATOR_TOKEN +
                            getString(R.string.userwishlist_description) + ": " + description;
        }

        if(assignee!=null && !assignee.isEmpty()) {
            wishDataStr += appendAssignee(assignee, processingDate);
        }

        return wishDataStr;
    }

}