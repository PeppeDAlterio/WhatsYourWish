package com.peppedalterio.whatsyourwish;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.*;
import com.peppedalterio.whatsyourwish.pojo.WishStrings;


public class MyWishlistFragment extends Fragment {

    private DatabaseReference dbRef;
    private String simNumber;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.mywishlist_fragment, container, false);

    }

    @SuppressLint("HardwareIds")
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity()==null) return;

        ListView listView = getActivity().findViewById(R.id.mywishlistrv);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        TelephonyManager telemamanger = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            getActivity().finish();
        }

        simNumber = telemamanger.getLine1Number();

        Log.d("SIM_NUMBER", "num="+simNumber);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbRef = database.getReference(simNumber);

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

        FloatingActionButton actionButton = getActivity().findViewById(R.id.floatingActionButton);
        actionButton.setOnClickListener((View v) -> addAWish());

        listView.setOnItemClickListener(
                (parent, view, position, id) ->
                        Toast.makeText(getContext(), getString(R.string.toast_long_press_to_delete_wish),
                                Toast.LENGTH_SHORT).show()
        );

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Log.d("DEBUG", "long_click:"+parent.getItemAtPosition(position).toString());
            onItemLongClick(parent.getItemAtPosition(position).toString());
            return true;
        });

    }

    private void onItemLongClick(String s) {

        if(getContext()==null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.dialog_confirm));
        builder.setMessage(getString(R.string.dialog_delete_wish));
        builder.setCancelable(false);

        builder.setPositiveButton(getString(R.string.dialog_yes), (DialogInterface dialog, int which) -> {
                Query query = dbRef.orderByChild(WishStrings.WISH_TITLE_KEY).equalTo(s.split(WishStrings.SEPARATOR_TOKEN)[0]);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
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

    private void addAWish() {

        Intent myIntent = new Intent(getActivity(), AddItemActivity.class);
        myIntent.putExtra("simNumber", simNumber);
        this.startActivity(myIntent);

    }

}
