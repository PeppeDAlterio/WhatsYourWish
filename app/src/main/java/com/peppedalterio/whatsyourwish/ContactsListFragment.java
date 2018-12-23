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
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.peppedalterio.whatsyourwish.pojo.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsListFragment extends Fragment {

    private static final String PROJECTION[] = {
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private static final String SELECTION =
            ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";

    private List<Contact> listaContatti = new ArrayList<>();

    private SimpleCursorAdapter m;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.contactslist_fragment, container, false);

        return view;

    }

    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            android.R.id.text1,
            android.R.id.text2
    };

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listaContattiView = (ListView) getActivity().findViewById(R.id.contactlistview);

        Cursor cntList = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, SELECTION,null, null);

        ArrayAdapter<String> adapter;
        ArrayList<String> listItems = new ArrayList<String>();

        while (cntList.moveToNext())
        {
            String name = cntList.getString(cntList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cntList.getString(cntList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            listItems.add(phoneNumber + " | " + name);
        }
        cntList.close();

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItems);

        listaContattiView.setAdapter(adapter);

    }

}
