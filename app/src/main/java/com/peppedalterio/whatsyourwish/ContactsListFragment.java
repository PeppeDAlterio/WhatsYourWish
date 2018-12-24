package com.peppedalterio.whatsyourwish;

import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

    private List<Contact> numbersList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.contactslist_fragment, container, false);

        /*
        FIXME: CHECK PERMISSION LIKE MYWISHLISTFRAGMENT TO AVOID CRASH
         */

        return view;

    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView contactsListView = (ListView) getActivity().findViewById(R.id.contactlistview);

        Cursor contactList = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, SELECTION,null, null);

        ArrayAdapter<String> adapter;
        ArrayList<String> listItems = new ArrayList<String>();

        while (contactList.moveToNext())
        {
            String name = contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            listItems.add(phoneNumber + " | " + name);
            numbersList.add(new Contact(name, phoneNumber));
        }
        contactList.close();

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listItems);

        contactsListView.setAdapter(adapter);
        contactsListView.setOnItemClickListener((parent, view, position, id) -> {
            //String selectedItem = (String) parent.getItemAtPosition(position);
            Log.i("CLICK","Phone number: " + numbersList.get(position).getPhoneNumber());
            mostraListaUtente(numbersList.get(position));
        });

    }

    private void mostraListaUtente(Contact contact) {
        Intent myIntent = new Intent(getActivity(), WishlistUtenteActivity.class);
        myIntent.putExtra("contact", contact);
        this.startActivity(myIntent);
    }

}
