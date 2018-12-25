package com.peppedalterio.whatsyourwish;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.peppedalterio.whatsyourwish.pojo.Contact;
import com.peppedalterio.whatsyourwish.pojo.WishStrings;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.contactslist_fragment, container, false);


    }


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity()==null) return;

        ListView contactsListView = getActivity().findViewById(R.id.contactlistview);
        EditText searchBar = getActivity().findViewById(R.id.contactssearchbar);

        Cursor contactList = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, SELECTION,null, null);

        ArrayAdapter<String> adapter;
        List<Contact> numbersList = new ArrayList<>();
        ArrayList<String> listItems = new ArrayList<>();

        if(contactList == null) return;

        while (contactList.moveToNext())
        {
            String name = contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            listItems.add(name + WishStrings.SEPARATOR_TOKEN + phoneNumber);
            numbersList.add(new Contact(name, phoneNumber));
        }
        contactList.close();

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listItems);

        contactsListView.setAdapter(adapter);
        contactsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);

            Log.d("CLICK", "Phone number: " + selectedItem.split(WishStrings.SEPARATOR_TOKEN)[1]);
            mostraListaUtente(new Contact(selectedItem.split(WishStrings.SEPARATOR_TOKEN)[0],
                    selectedItem.split(WishStrings.SEPARATOR_TOKEN)[1]));
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ArrayAdapter adp = (ArrayAdapter)((ListView)getActivity().findViewById(R.id.contactlistview)).getAdapter();

                adp.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void mostraListaUtente(Contact contact) {
        Intent myIntent = new Intent(getActivity(), WishlistUtenteActivity.class);
        myIntent.putExtra("contact", contact);
        this.startActivity(myIntent);
    }

}
