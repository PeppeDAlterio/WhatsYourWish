package com.peppedalterio.whatsyourwish.model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.util.NumberUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.peppedalterio.whatsyourwish.R;
import com.peppedalterio.whatsyourwish.util.WishStrings;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserWishlistModel {

    private String contactNumber;
    private String contactDBNumber;
    private String local_self_assigned;
    private String local_assign_date;
    private String local_description;
    private FirebaseDatabase database;// = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef;

    private WeakReference<ArrayAdapter<String>> listViewAdapter;

    public UserWishlistModel(String contactNumber, ArrayAdapter<String> listViewAdapter,
                             String self_assigned, String assign_date, String description) {

        this.contactNumber = contactNumber;
        this.listViewAdapter = new WeakReference<>(listViewAdapter);
        local_self_assigned = self_assigned;
        local_assign_date = assign_date;
        local_description = description;

        database = FirebaseDatabase.getInstance();

    }

    UserWishlistModel(String self_assigned, String assign_date, String description) {
        local_self_assigned = self_assigned;
        local_assign_date = assign_date;
        local_description = description;
    }

    public void getUserWishlist(Activity activity) {

        WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);

        if (contactNumber==null) {
            return;
        }

        /*
            if the effective (number from the db) of the given contact number is already obtained
            (and the number is not changed since last time -- just for a more robust code)
            then i'm just gonna load the wishlist from the db. I'm taking the effective number
            (if exists) from the db and retrieving the wishlist, otherwise
         */
        if (contactDBNumber != null && PhoneNumberUtils.compare(contactDBNumber, contactNumber)) {
            loadWishList(listViewAdapter.get());
        } else {

            ValueEventListener loadWishListEventListener = new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.getKey() != null && PhoneNumberUtils.compare(ds.getKey(), contactNumber)) {
                            contactDBNumber = ds.getKey();
                            ((TextView) activityWeakReference.get().findViewById(R.id.userwishlistnumber)).setText(contactDBNumber);
                            break;
                        }

                    }
                    if (contactDBNumber != null) {
                        loadWishList(listViewAdapter.get());
                    } else {
                        Toast.makeText(activityWeakReference.get().getApplicationContext(), activity.getString(R.string.toast_user_not_found),
                                Toast.LENGTH_SHORT).show();
                        activityWeakReference.get().finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };

            getEffectiveContactDBNumber(loadWishListEventListener);
        }
    }

    /**
     * This methods gets the contact's effective number from the DB and performs the
     * actions specied by the listener
     * @param listener listener for the callback to be executed when the effective DB number is obtained
     */
    private void getEffectiveContactDBNumber(ValueEventListener listener) {
        dbRef = database.getReference();

        Query query = dbRef.orderByKey();
        query.addListenerForSingleValueEvent(listener);
    }

    private void loadWishList(ArrayAdapter<String> listViewAdapter) {

        dbRef = database.getReference(contactDBNumber);

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {

                //Log.d("ADD", "added: " + dataSnapshot.getValue());

                String wishDataStr = "";

                String title = dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class);
                String description = dataSnapshot.child(WishStrings.WISH_DESCRIPTION_KEY).getValue(String.class);
                String assignee = dataSnapshot.child(WishStrings.WISH_ASSIGNEE).getValue(String.class);
                String processingDate = dataSnapshot.child(WishStrings.PROCESSING_WISH_SINCE).getValue(String.class);

                if (title!=null) {
                    // no try-catch 'cause it's already null-protected (title)
                    wishDataStr = formatWishDataStr(title, description, assignee, processingDate);
                }
                listViewAdapter.add(wishDataStr);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Log.d("CHANGE", "changed: " + dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("REMOVE", "removed"+dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class));

                String str = dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class) + WishStrings.SEPARATOR_TOKEN +
                        dataSnapshot.child(WishStrings.WISH_DESCRIPTION_KEY).getValue(String.class);

                listViewAdapter.remove(str);

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


    /**
        This method handles the request to assign to a wish:
        if a user with the given contact number is found into the DB, then the action is passed to
        the assignment method
     */
    public void assignToAWishRequest(String mySimNumber, String wishTitle,
                              String wishFormattedData, int pos, Activity activity) {

        if (contactNumber==null) {
            return;
        }

        /*
            if the effective (number from the db) of the given contact number is already obtained
            (and the number is not changed since last time -- just for a more robust code)
            then i'm just gonna load the wishlist from the db. I'm taking the effective number
            (if exists) from the db and retrieving the wishlist, otherwise
         */
        if (contactDBNumber==null) {

            ValueEventListener listener = new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.getKey() != null && PhoneNumberUtils.compare(ds.getKey(), contactNumber)) {
                            contactDBNumber = ds.getKey();
                            ((TextView) activity.findViewById(R.id.userwishlistnumber)).setText(contactDBNumber);
                            break;
                        }

                    }
                    if (contactDBNumber != null) {
                        assignToAWish(mySimNumber, wishTitle, wishFormattedData, pos, activity);
                    } else {
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.toast_user_not_found),
                                Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };

            getEffectiveContactDBNumber(listener);

        } else if (PhoneNumberUtils.compare(contactDBNumber, contactNumber)) {
            assignToAWish(mySimNumber, wishTitle, wishFormattedData, pos, activity);
        }


    }

    /**
     * Assignment to a wish using mySimNumber as asignee number
     * @param mySimNumber sim number of the assignee
     * @param wishTitle title fo the wish
     * @param wishFormattedData formatte data of the wish, displayed by activity level
     * @param pos position of the selected with in listview
     * @param activity activity to show Toasts
     */
    private void assignToAWish(String mySimNumber, String wishTitle, String wishFormattedData, int pos, Activity activity) {

        dbRef = database.getReference(contactDBNumber);

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
                    if ( currentAssignee!=null && !currentAssignee.isEmpty() && !PhoneNumberUtils.compare(mySimNumber, currentAssignee)) {
                        Log.d("ASSIGNEE", "case_1 - simNumber: " + mySimNumber + "| assignee: " + currentAssignee);
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.toast_assigned_to_another_one), Toast.LENGTH_SHORT).show();
                        // updating displayed info with current assignee if currently not displayed
                        if(!wishFormattedData.contains(": "+currentAssignee+WishStrings.SEPARATOR_TOKEN)) {
                            // we don't need a null-test of wish title because we're already sure that's not null (found in db)
                            newWishItemStr = formatWishDataStr(wishTitle, wishDescription, currentAssignee, currentAssigneeDate);
                            listViewAdapter.get().remove(wishFormattedData);
                            listViewAdapter.get().insert(newWishItemStr, pos);
                        }
                        return;
                    }
                    /* case 2: assigned to yourself -> delete the assignment */
                    else if ( currentAssignee!=null && !currentAssignee.isEmpty() && PhoneNumberUtils.compare(mySimNumber, currentAssignee)) {
                        Log.d("ASSIGNEE", "case_2");
                        newAssignee = "";
                        newAssigneeDate = "";
                    }
                    /* case 3: not assigned -> assign to yourself */
                    else {
                        Log.d("ASSIGNEE", "case_3");
                        Date todayDate = Calendar.getInstance().getTime();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        newAssigneeDate = formatter.format(todayDate);
                        newAssignee = mySimNumber;
                    }

                    // format new wish list item string
                    newWishItemStr = formatWishDataStr(wishTitle, wishDescription, newAssignee, newAssigneeDate);

                    /* apply the change */
                    ds.child(WishStrings.WISH_ASSIGNEE).getRef().setValue(newAssignee);
                    ds.child(WishStrings.PROCESSING_WISH_SINCE).getRef().setValue(newAssigneeDate);
                    listViewAdapter.get().remove(wishFormattedData);
                    listViewAdapter.get().insert(newWishItemStr, pos);

                    /* show and informative toast */
                    if(newAssignee.isEmpty()) {
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.toast_remove_assignment), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.toast_self_assign), Toast.LENGTH_SHORT).show();
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
     This method appends assignee's information to the listview item for the wish:
     assignee: phone number of the assignee
     processingDate: the date on which it was taken over
     */
    String appendAssignee(String assignee, String processingDate) {

        if ( (assignee==null || assignee.isEmpty()) || (!isValidDate(processingDate)) ) {
            return "";
        }

        return WishStrings.SEPARATOR_TOKEN +
                local_self_assigned + ": " + assignee +
                WishStrings.SEPARATOR_TOKEN +
                local_assign_date + ": " + processingDate;
    }

    /**
     * This methos formats the wish data String to be displayed into the list view.
     * @param title wish title
     * @param description wish description
     * @param assignee wish assignee
     * @param processingDate date on which the assignee took over the wish. You can also pass null or empty for today's date
     * @return Formatted string for list view item for user wish list
     * @throws NullPointerException if title is null, IllegalArgumentException if title/description invalid
     */
    String formatWishDataStr(String title, String description, String assignee, String processingDate) {

        if (!MyWishlistModel.validate(title, description)) {
            throw new IllegalArgumentException("invalid wish data");
        }

        String wishDataStr = title;

        if (description!=null && !description.isEmpty()) {
            wishDataStr +=  WishStrings.SEPARATOR_TOKEN +
                    local_description + ": " + description;
        }

        if(assignee!=null && !assignee.isEmpty()) {
            //if processing date is not a valid date AND we're gonna make the assignment -> today's date in dd-MM-yyyy format
            if(!isValidDate(processingDate)) {
                Date todayDate = Calendar.getInstance().getTime();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                processingDate = formatter.format(todayDate);
            }
            wishDataStr += appendAssignee(assignee, processingDate);
        }

        return wishDataStr;
    }

    /**
     * Method checks if the String is a valid date in pattern dd-MM-yyyy
     * @param inDate date String to be checked
     * @return true if valid, false otherwise
     */
    static boolean isValidDate(String inDate) {

        if (inDate==null || inDate.isEmpty()) {
            return false;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        dateFormat.setLenient(false); //strictly  match my pattern!
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }



}