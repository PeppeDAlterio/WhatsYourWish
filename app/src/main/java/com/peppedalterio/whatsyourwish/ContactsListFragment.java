package com.peppedalterio.whatsyourwish;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ContactsListFragment extends Fragment {

    private static final String TAG = "ContactsListFragment";

    private Button bttst;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.contactslist_fragment, container, false);
        bttst = (Button) view.findViewById(R.id.btnTEST);

        bttst.setOnClickListener( (View) -> {
            Toast.makeText(getActivity(), "TEST BTN1", Toast.LENGTH_SHORT).show();
        } );

        return view;

    }
}
