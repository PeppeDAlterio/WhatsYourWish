package com.peppedalterio.whatsyourwish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.*;
import com.peppedalterio.whatsyourwish.util.InternetConnection;
import com.peppedalterio.whatsyourwish.util.WishStrings;


public class MyWishlistFragment extends Fragment {

    public static final int MIN_REFRESH_RATE = 5000;
    private long lastRefreshTime = 0;
    private DatabaseReference dbRef;
    private String simNumber;
    private ArrayAdapter<String> wishListAdapter;
    private ChildEventListener childEventListener;

    /*
     * Action to be performed if the client disconnects from the Internet
     */
    private void disconnectedActionMethod() {
        Toast.makeText(getContext(), getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();

        wishListAdapter.clear();

        if(getActivity()!=null) {
            getActivity().findViewById(R.id.mylistnointernettextview).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.refresh_no_internet_button).setVisibility(View.VISIBLE);
        }
    }

    /*
     * Action to be performed if the client connects again to the Internet
     */
    private void connectedActionMethod() {
        if(getActivity()!=null) {
            getActivity().findViewById(R.id.mylistnointernettextview).setVisibility(View.INVISIBLE);
            getActivity().findViewById(R.id.refresh_no_internet_button).setVisibility(View.INVISIBLE);
        }
    }

    /*
     * This method check if internet connection is available
     */
    private boolean checkInternetConnection() {

        boolean isConnected = InternetConnection.checkForInternetConnection(getContext());

        if (isConnected)
            connectedActionMethod();
        else
            disconnectedActionMethod();

        return isConnected;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.mywishlist_fragment, container, false);

    }

    @SuppressLint("HardwareIds")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity()==null) return;

        ListView listView = getActivity().findViewById(R.id.mywishlistrv);

        wishListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        listView.setAdapter(wishListAdapter);

        TelephonyManager telemamanger = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            getActivity().finish();
        }

        simNumber = telemamanger.getLine1Number();

        if(!simNumber.isEmpty()) {

            Log.d("SIM_NUMBER", "num=" + simNumber);

            Button refreshButton = getActivity().findViewById(R.id.refresh_no_internet_button);
            refreshButton.setOnClickListener((View v) -> refreshWishListFromDB(listView));

            FloatingActionButton refreshFloatingButton = getActivity().findViewById(R.id.refresh_floating_button);
            refreshFloatingButton.setOnClickListener((v) -> refreshWishListFromDB(listView));

            FloatingActionButton actionButton = getActivity().findViewById(R.id.floatingActionButton);
            actionButton.setOnClickListener((View v) -> addAWish());

            refreshWishListFromDB(listView);

        } else {

            Toast.makeText(getContext(), getString(R.string.toast_no_sim_number),
                    Toast.LENGTH_LONG).show();

            FloatingActionButton actionButton = getActivity().findViewById(R.id.floatingActionButton);
            actionButton.setOnClickListener((View v) ->
                    Toast.makeText(getContext(), getString(R.string.toast_no_sim_number),
                            Toast.LENGTH_LONG).show());

        }

    }

    private void refreshWishListFromDB(ListView listView) {

        if (SystemClock.elapsedRealtime() - lastRefreshTime < MIN_REFRESH_RATE){
            Toast.makeText(getContext(), getString(R.string.toast_refresh_rate),
                    Toast.LENGTH_LONG).show();
            return;
        }

        if(childEventListener!=null)
            dbRef.removeEventListener(childEventListener);

        lastRefreshTime = SystemClock.elapsedRealtime();

        wishListAdapter.clear();

        if( !checkInternetConnection() ) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference(simNumber);

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
                (parent, view, position, id) ->
                        Toast.makeText(getContext(), getString(R.string.toast_long_press_to_delete_wish),
                                Toast.LENGTH_SHORT).show()
        );

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Log.d("DEBUG", "long_click:" + parent.getItemAtPosition(position).toString());
            onItemLongClick(parent.getItemAtPosition(position).toString());
            return true;
        });
    }

    /**
     * This method defines the action to be performed on any mywishlistrv item long click.
     * <br>
     * It shows an alert that lets you delete the selected wish from your wishlist.
     *
     * @param wishData containing wish title and description in format TITLE\r\nDESCRIPTION
     */
    private void onItemLongClick(String wishData) {

        if(getContext()==null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.dialog_confirm));
        builder.setMessage(getString(R.string.dialog_delete_wish));
        builder.setCancelable(false);

        builder.setPositiveButton(getString(R.string.dialog_yes), (DialogInterface dialog, int which) -> {

            if(!checkInternetConnection())
                return;

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

            Toast.makeText(getContext(), getString(R.string.remove_wish_ok), Toast.LENGTH_SHORT).show();

        });

        builder.setNegativeButton(getString(R.string.dialog_no), (DialogInterface dialog, int which) -> {
            //
        });

        builder.show();

    }

    /**
     * This method defines the action to be performed on floatingActionButton click.
     * <br>
     * It shows the activity to add a new wish to your wishlist.
     *
     * @author Giuseppe D'Alterio
     */
    private void addAWish() {

        Intent myIntent = new Intent(getActivity(), AddItemActivity.class);
        myIntent.putExtra("simNumber", simNumber);
        this.startActivity(myIntent);

    }

}
