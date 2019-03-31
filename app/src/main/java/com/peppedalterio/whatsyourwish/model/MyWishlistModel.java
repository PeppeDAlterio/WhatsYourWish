package com.peppedalterio.whatsyourwish.model;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

public class MyWishlistModel {

    private DatabaseReference dbRef;
    private String simNumber;
    private WeakReference<ArrayAdapter<String>> wishListAdapter;
    private ChildEventListener childEventListener; //db

    public MyWishlistModel(String simNumber, ArrayAdapter<String> wishListAdapter) {
        this.simNumber = simNumber;
        this.wishListAdapter = new WeakReference<>(wishListAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference(simNumber);

    }

    public MyWishlistModel(String simNumber) {
        this.simNumber = simNumber;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference(simNumber);
    }

    public void removeEventListener() {
        dbRef.removeEventListener(childEventListener);
    }

    public void refreshMyWishList() {

        if(childEventListener!=null)
            dbRef.removeEventListener(childEventListener);

        wishListAdapter.get().clear();

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d("ADD", "added: " + dataSnapshot.getValue());

                String str = "";

                String title = dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class);
                String description = dataSnapshot.child(WishStrings.WISH_DESCRIPTION_KEY).getValue(String.class);
                str += title + WishStrings.SEPARATOR_TOKEN + description;

                wishListAdapter.get().add(str);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("CHANGE", "changed: " + dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("REMOVE", "removed" + dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class));

                String str = dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class) + WishStrings.SEPARATOR_TOKEN +
                        dataSnapshot.child(WishStrings.WISH_DESCRIPTION_KEY).getValue(String.class);

                wishListAdapter.get().remove(str);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("CHANGE", "changed: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        dbRef.addChildEventListener(childEventListener);

    }

    public void removeWishlistItem(String wishData) {

        Query query = dbRef.orderByChild(WishStrings.WISH_TITLE_KEY).equalTo(wishData.split(WishStrings.SEPARATOR_TOKEN)[0]).limitToFirst(1);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren().iterator().hasNext()) {
                    DataSnapshot ds = dataSnapshot.getChildren().iterator().next();
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });

    }

    public boolean addWishlistItem(String title, String description, Activity activity) {

        WeakReference<Activity> activityRef = new WeakReference<>(activity);

        if(!validate(title, description)) {
            return false;
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(simNumber);

        Query query = dbRef.orderByChild(WishStrings.WISH_TITLE_KEY).equalTo(title);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0) {
                    Log.d("ADD_A_WISH", "Exists");
                    Toast.makeText(activityRef.get().getApplicationContext(),
                            activity.getString(R.string.toast_wish_exists), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("ADD_A_WISH", "Not exists");

                    Map<String, Object> tmpMap = new HashMap<>();
                    tmpMap.put(WishStrings.WISH_TITLE_KEY, title);
                    tmpMap.put(WishStrings.WISH_DESCRIPTION_KEY, description);
                    tmpMap.put(WishStrings.WISH_ASSIGNEE, "");
                    tmpMap.put(WishStrings.PROCESSING_WISH_SINCE, "");

                    DatabaseReference tmpRef = dbRef.push();

                    tmpRef.updateChildren(tmpMap);

                    Log.d("ADD_A_WISH", "Success");
                    Toast.makeText(activityRef.get().getApplicationContext(),
                            activity.getString(R.string.toast_add_wish_success), Toast.LENGTH_SHORT).show();
                    activityRef.get().finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });

        return true;

    }

    /**
     * This method checks if the title and the description of the wish is valid:
     * title can't be null or empty and lesser or eq. than 40 chars
     * description is optional, but have to be lesser or eq. than 50 chars
     * @param title wish title
     * @param description wish description
     * @throws NullPointerException if title is null, IllegalArgumentException if title/description invalid
     */
    static public boolean validate(String title, String description) {

        if(title==null) {
            throw new NullPointerException("title is null");
        }

        if (description==null || description.trim().isEmpty()) {
            description = "";
        }

        return  !title.isEmpty() &&
                title.length() <= 40 &&
                description.length() <= 50 &&
                !title.contains("\r\n") &&
                !description.contains("\r\n") &&
                !(title.trim().isEmpty());

    }

}
