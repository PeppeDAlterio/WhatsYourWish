package com.peppedalterio.whatsyourwish.activity.fragment;

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
import com.peppedalterio.whatsyourwish.R;
import com.peppedalterio.whatsyourwish.activity.AddItemActivity;
import com.peppedalterio.whatsyourwish.model.MyWishlistModel;
import com.peppedalterio.whatsyourwish.util.InternetConnection;


public class MyWishlistFragment extends Fragment {

    public static final int MIN_REFRESH_RATE = 5000;
    private long lastRefreshTime = 0;
    private String simNumber;
    private ArrayAdapter<String> wishListAdapter;
    private ChildEventListener childEventListener;
    private MyWishlistModel wishlistModel;

    public static MyWishlistFragment newInstance() {
        return new MyWishlistFragment();
    }

    /*
     * Action to be performed if the client disconnects from the Internet
     */
    private void disconnectedActionMethod() {
        Toast.makeText(getContext(), getString(com.peppedalterio.whatsyourwish.R.string.toast_no_internet), Toast.LENGTH_SHORT).show();

        wishListAdapter.clear();

        if(getActivity()!=null) {
            getActivity().findViewById(com.peppedalterio.whatsyourwish.R.id.mylistnointernettextview).setVisibility(View.VISIBLE);
            getActivity().findViewById(com.peppedalterio.whatsyourwish.R.id.refresh_no_internet_button).setVisibility(View.VISIBLE);
        }
    }

    /*
     * Action to be performed if the client connects again to the Internet
     */
    private void connectedActionMethod() {
        if(getActivity()!=null) {
            getActivity().findViewById(com.peppedalterio.whatsyourwish.R.id.mylistnointernettextview).setVisibility(View.INVISIBLE);
            getActivity().findViewById(com.peppedalterio.whatsyourwish.R.id.refresh_no_internet_button).setVisibility(View.INVISIBLE);
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

        return inflater.inflate(com.peppedalterio.whatsyourwish.R.layout.mywishlist_fragment, container, false);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wishlistModel.removeEventListener();
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity()==null) return;

        ListView listView = getActivity().findViewById(com.peppedalterio.whatsyourwish.R.id.mywishlistrv);

        wishListAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        listView.setAdapter(wishListAdapter);

        TelephonyManager telemamanger = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            getActivity().finish();
        }

        simNumber = telemamanger.getLine1Number();

        if( simNumber!=null && !simNumber.isEmpty() ) {

            wishlistModel = new MyWishlistModel(simNumber, wishListAdapter);

            Log.d("SIM_NUMBER", "num=" + simNumber);

            Button refreshButton = getActivity().findViewById(com.peppedalterio.whatsyourwish.R.id.refresh_no_internet_button);
            refreshButton.setOnClickListener( (View v) -> refreshMyWishlist(wishlistModel, listView) );

            FloatingActionButton refreshFloatingButton = getActivity().findViewById(com.peppedalterio.whatsyourwish.R.id.refresh_floating_button);
            refreshFloatingButton.setOnClickListener( (View v) -> refreshMyWishlist(wishlistModel, listView) );

            FloatingActionButton actionButton = getActivity().findViewById(com.peppedalterio.whatsyourwish.R.id.floatingActionButton);
            actionButton.setOnClickListener((View v) -> addAWish());

            refreshMyWishlist(wishlistModel, listView);

        } else {

            Toast.makeText(getContext(), getString(com.peppedalterio.whatsyourwish.R.string.toast_no_sim_number),
                    Toast.LENGTH_LONG).show();

            FloatingActionButton actionButton = getActivity().findViewById(com.peppedalterio.whatsyourwish.R.id.floatingActionButton);
            actionButton.setOnClickListener((View v) ->
                    Toast.makeText(getContext(), getString(com.peppedalterio.whatsyourwish.R.string.toast_no_sim_number),
                            Toast.LENGTH_LONG).show());

        }

    }

    private void refreshMyWishlist(MyWishlistModel wishlistModel, ListView wishlistListView) {

        if (SystemClock.elapsedRealtime() - lastRefreshTime < MIN_REFRESH_RATE){
            Toast.makeText(getContext(), getString(R.string.toast_refresh_rate),
                Toast.LENGTH_LONG).show();
        } else {
            lastRefreshTime = SystemClock.elapsedRealtime();
            if(!checkInternetConnection()) return;
            wishlistModel.refreshMyWishList();
        }

        wishlistListView.setOnItemClickListener(
                (parent, view, position, id) ->
                        Toast.makeText(getContext(), getString(R.string.toast_long_press_to_delete_wish),
                                Toast.LENGTH_SHORT).show()
        );

        wishlistListView.setOnItemLongClickListener((parent, view, position, id) -> {
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
        builder.setTitle(getString(com.peppedalterio.whatsyourwish.R.string.dialog_confirm));
        builder.setMessage(getString(com.peppedalterio.whatsyourwish.R.string.dialog_delete_wish));
        builder.setCancelable(false);

        builder.setPositiveButton(getString(com.peppedalterio.whatsyourwish.R.string.dialog_yes), (DialogInterface dialog, int which) -> {

            if(!checkInternetConnection()) return;

            MyWishlistModel wishlistModel = new MyWishlistModel(simNumber);
            wishlistModel.removeWishlistItem(wishData);

            Toast.makeText(getContext(), getString(com.peppedalterio.whatsyourwish.R.string.remove_wish_ok), Toast.LENGTH_SHORT).show();

        });

        builder.setNegativeButton(getString(R.string.dialog_no), (DialogInterface dialog, int which) -> {
            // nothing to do then
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
