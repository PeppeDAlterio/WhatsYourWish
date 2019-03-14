package com.peppedalterio.whatsyourwish.activity.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.peppedalterio.whatsyourwish.R;
import com.peppedalterio.whatsyourwish.activity.UserWishlistActivity;
import com.peppedalterio.whatsyourwish.util.Contact;
import com.peppedalterio.whatsyourwish.util.WishStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContactsListFragment extends Fragment {

    /**
     * Projection for contacts list data retrival.
     * <br>
     * We're just retriving DISPLAY_NAME and NUMBER for each contact.
     *
     */
    private static final String PROJECTION[] = {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    /**
     * Selection for contacts list data retrival.
     * <br>
     * We're just retriving contacts with a PHONE_NUMBER associated.
     *
     */
    private static final String SELECTION =
            ContactsContract.CommonDataKinds.Phone.NUMBER + " IS NOT NULL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.contactslist_fragment, container, false);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity()==null) return;

        ListView contactsListView = getActivity().findViewById(R.id.contactlistview);
        EditText searchBar = getActivity().findViewById(R.id.contactssearchbar);

        ArrayList<String> listItems = getContactsList();
        if (listItems == null) return;

        ArrayAdapter<String> adapter;
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

    /**
     * This method retrieves the contacts list.
     *
     * @return ArrayList containing the contacts list with DISPLAY_NAME and NUMBER
     */
    private ArrayList<String> getContactsList() {
        Cursor contactList = Objects.requireNonNull(getActivity()).getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, SELECTION,
                null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");

        ArrayAdapter<String> adapter;
        List<String> numbersList = new ArrayList<>();
        ArrayList<String> listItems = new ArrayList<>();

        if(contactList == null) return null;

        String name, phoneNumber, lastNumber;
        boolean duplicate;
        int i;

        while (contactList.moveToNext())
        {
            name = contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            /*
                Compare the number with last inserted ones (with the same contact's name)
                to check if it's a duplicate written with another format
                (eg. +39 123 456 7890 and 1234567890).
             */
            i=0;
            duplicate = false;
            while( (listItems.size()-1-i)>=0 &&
                    listItems.get(listItems.size()-1-i).startsWith(name+WishStrings.SEPARATOR_TOKEN) ) {

                if(PhoneNumberUtils.compare(numbersList.get(numbersList.size()-1-i), phoneNumber))
                    duplicate = true;

                i++;
            }

            if(!duplicate) {
                listItems.add(name + WishStrings.SEPARATOR_TOKEN + PhoneNumberUtils.normalizeNumber(phoneNumber));
                numbersList.add(PhoneNumberUtils.normalizeNumber(phoneNumber));
            }

        }
        contactList.close();
        return listItems;
    }

    /**
     * This method defines the action to be performed on contactlistview click.
     * <br>
     * It shows the activity containing selected contact's wishlist.
     */
    private void mostraListaUtente(Contact contact) {
        Intent myIntent = new Intent(getActivity(), UserWishlistActivity.class);
        myIntent.putExtra("contact", contact);
        this.startActivity(myIntent);
    }

}
