package com.peppedalterio.whatsyourwish;

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

import java.util.HashMap;
import java.util.Map;

public class MyWishlistFragment extends Fragment {

    private static final String SEPARATOR_TOKEN = "\r\n";

    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private String simNumber;
    private ArrayAdapter<String> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mywishlist_fragment, container, false);

        return view;

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = getActivity().findViewById(R.id.mywishlistrv);

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

        listView.setAdapter(adapter);

        TelephonyManager telemamanger = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // FIXME: AVVISO NO PERMESSIONS
            Log.e("NO_PERMISSION", "No READ_PHONE_STATE");
        }

        simNumber = telemamanger.getLine1Number();

        Log.d("SIM_NUMBER", "num="+simNumber);

        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference(simNumber);

        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Log.d("ADD", "added: " + dataSnapshot.getValue());
//fixme: stringhe costanti

                String str = "";

                String title = dataSnapshot.child("TITOLO").getValue(String.class);
                String description = dataSnapshot.child("DESCRIZIONE").getValue(String.class);
                str += title + SEPARATOR_TOKEN + description;

                adapter.add(str);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("CHANGE", "changed: " + dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("REMOVE", "removed"+dataSnapshot.child("TITOLO").getValue(String.class));

                String str = dataSnapshot.child("TITOLO").getValue(String.class) + SEPARATOR_TOKEN +
                        dataSnapshot.child("DESCRIZIONE").getValue(String.class);

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
        actionButton.setOnClickListener((l)->{
            addAWish();
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Log.d("DEBUG", "long_click:"+parent.getItemAtPosition(position).toString());
            onItemLongClick(parent.getItemAtPosition(position).toString());
            return true;
        });

    }

    private void onItemLongClick(String s) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Confirm dialog demo !");
        builder.setMessage("You are about to delete all records of database. Do you really want to proceed ?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Query query = dbRef.orderByChild("TITOLO").equalTo(s.split(SEPARATOR_TOKEN)[0]);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
                            ds.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("TAG", "onCancelled", databaseError.toException());
                    }
                });

                Toast.makeText(getContext(), "Desiderio cancellato", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Cancellazione annullata", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();

    }

    private void addAWish() {

        Intent myIntent = new Intent(getActivity(), AddItemActivity.class);
        myIntent.putExtra("simNumber", simNumber);
        this.startActivity(myIntent);

        Log.d("FINISH", "finito u.u");
    }

}
