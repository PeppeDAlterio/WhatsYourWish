package com.peppedalterio.whatsyourwish.model;

import android.app.Activity;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.util.HashMap;
import java.util.Map;

public class MyWishlistModel {

    public static final int MIN_REFRESH_RATE = 5000;
    private long lastRefreshTime = 0;
    private DatabaseReference dbRef;
    private String simNumber;
    private ArrayAdapter<String> wishListAdapter;
    private ChildEventListener childEventListener;
    private ListView listView;

    public MyWishlistModel(String simNumber, ArrayAdapter<String> wishListAdapter,
                           ChildEventListener childEventListener, ListView listView) {
        this.simNumber = simNumber;
        this.wishListAdapter = wishListAdapter;
        this.childEventListener = childEventListener;
        this.listView = listView;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference(simNumber);

    }

    public MyWishlistModel(String simNumber) {
        this.simNumber = simNumber;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference(simNumber);
    }

    public void refreshWishListFromDB() {

        if (SystemClock.elapsedRealtime() - lastRefreshTime < MIN_REFRESH_RATE){
            /*fixme Toast.makeText(getContext(), getString(R.string.toast_refresh_rate),
                    Toast.LENGTH_LONG).show();*/
            return;
        }

        if(childEventListener!=null)
            dbRef.removeEventListener(childEventListener);

        lastRefreshTime = SystemClock.elapsedRealtime();

        wishListAdapter.clear();

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d("ADD", "added: " + dataSnapshot.getValue());

                String str = "";

                String title = dataSnapshot.child(WishStrings.WISH_TITLE_KEY).getValue(String.class);
                String description = dataSnapshot.child(WishStrings.WISH_DESCRIPTION_KEY).getValue(String.class);
                str += title + WishStrings.SEPARATOR_TOKEN + description;

                wishListAdapter.add(str);

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

                wishListAdapter.remove(str);

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

        listView.setOnItemClickListener(
                (parent, view, position, id) -> Log.d("abc", "test")
                       /* Toast.makeText(getContext(), getString(R.string.toast_long_press_to_delete_wish),
                                Toast.LENGTH_SHORT).show()*/
        );

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Log.d("DEBUG", "long_click:" + parent.getItemAtPosition(position).toString());
            //onItemLongClick(parent.getItemAtPosition(position).toString());
            return true;
        });
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

    public void addWishlistItem(String title, String description, Activity activity) {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(simNumber);

        Query query = dbRef.orderByChild(WishStrings.WISH_TITLE_KEY).equalTo(title);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0) {
                    Log.d("ADD_A_WISH", "Exists");
                    Toast.makeText(activity.getApplicationContext(),
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
                    Toast.makeText(activity.getApplicationContext(),
                            activity.getString(R.string.toast_add_wish_success), Toast.LENGTH_SHORT).show();
                    activity.finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });

    }

}
