package com.peppedalterio.whatsyourwish;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.Manifest;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MyWishlistFragment extends Fragment {

    private static final String SEPARATOR_TOKEN = " --- ";

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

                String str = "";
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                Log.d("numero figli", "figli: "+dataSnapshot.getChildrenCount());

                if(dataSnapshot.getChildrenCount()==2) {

                    str += iterator.next().getValue().toString() + SEPARATOR_TOKEN +
                            iterator.next().getValue().toString();

                    Log.d("PARSER_DB", "leggo=" + str);

                    adapter.add(str);

                    }
                    
                //adapter.add(dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("CHANGE", "changed: " + dataSnapshot.getValue());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("REMOVE", "removed");
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
            testMethod();
        });

    }

    private void testMethod() {

        DatabaseReference myRef = database.getReference(simNumber);

        String nome1 = "PC Windows";
        String nome2 = "Preferibilmente Dell_"+Math.random();

        myRef = myRef.push();
        myRef.push().setValue(nome1);
        myRef.push().setValue(nome2);

        String str = nome1 + SEPARATOR_TOKEN + nome2;
        adapter.add(str);

    }

}
