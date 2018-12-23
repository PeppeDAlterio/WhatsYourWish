package com.peppedalterio.whatsyourwish;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ContactsListFragment extends Fragment {

    ContactsContract contactsContract;

    private static final String PROJECTION[] = {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private static final String SELECTION =
            ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.contactslist_fragment, container, false);

        return view;

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Cursor cntList = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, SELECTION,null, null);
        while (cntList.moveToNext())
        {
            String name = cntList.getString(cntList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cntList.getString(cntList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //Toast.makeText(getActivity().getApplicationContext(),name, Toast.LENGTH_LONG).show();
            Log.d("DATA", name+" | "+phoneNumber);
        }
        cntList.close();

    }

}
