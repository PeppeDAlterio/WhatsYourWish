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
import java.util.Objects;

public class ContactsListFragment extends Fragment {

    /**
     * Projection for contacts list data retrival.
     * <br>
     * We're just retriving DISPLAY_NAME and NUMBER for each contact.
     *
     * @author Giuseppe D'Alterio
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
     * @author Giuseppe D'Alterio
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
     * @author Giuseppe D'Alterio
     */
    private ArrayList<String> getContactsList() {
        Cursor contactList = Objects.requireNonNull(getActivity()).getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, SELECTION,null, null);

        ArrayAdapter<String> adapter;
        List<Contact> numbersList = new ArrayList<>();
        ArrayList<String> listItems = new ArrayList<>();

        if(contactList == null) return null;

        while (contactList.moveToNext())
        {
            String name = contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = contactList.getString(contactList.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            listItems.add(name + WishStrings.SEPARATOR_TOKEN + phoneNumber);
            numbersList.add(new Contact(name, phoneNumber));
        }
        contactList.close();
        return listItems;
    }

    /**
     * This method defines the action to be performed on contactlistview click.
     * <br>
     * It shows the activity containing selected contact's wishlist.
     *
     * @author Giuseppe D'Alterio
     */
    private void mostraListaUtente(Contact contact) {
        Intent myIntent = new Intent(getActivity(), WishlistUtenteActivity.class);
        myIntent.putExtra("contact", contact);
        this.startActivity(myIntent);
    }

}
