package com.peppedalterio.whatsyourwish;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.peppedalterio.whatsyourwish.util.InternetConnection;
import com.peppedalterio.whatsyourwish.util.WishStrings;

import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {

    private String simNumber;

    @Override
    protected void onStart() {
        super.onStart();

        if(!checkInternetConnection())
            finish();

    }

    /*
     * This method check if internet connection is available
     */
    private boolean checkInternetConnection() {

        boolean isConnected = InternetConnection.checkForInternetConnection(getApplicationContext());

        if(!isConnected) {
            Log.d("INTERNET", "NO CONNECTION");
            Toast.makeText(getApplicationContext(), getString(R.string.toast_no_internet), Toast.LENGTH_SHORT).show();
        }

        return isConnected;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Intent intent = getIntent();

        if(intent.getStringExtra("simNumber") != null) {
            simNumber = intent.getStringExtra("simNumber");
        } else {
            finish();
        }

        Button btn = findViewById(R.id.addwishbutton);
        btn.setOnClickListener((l)->{
            if(!addItemClick())
                Toast.makeText(getApplicationContext(), getString(R.string.toast_add_wish_error), Toast.LENGTH_SHORT).show();
        });

    }

    /**
     * This method defines the action to be performed on addwishbutton click.
     *
     * It check for item's data validity and puts the info into the database.
     *
     */
    private boolean addItemClick() {

        String title = ((TextView)findViewById(R.id.wishtitle)).getText().toString();
        String description = ((TextView)findViewById(R.id.wishdescription)).getText().toString();

        if(!validate(title, description)) {
            return false;
        }

        if(!checkInternetConnection()) {
            Log.d("ADD_A_WISH", "Error");
            return true;
        }
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(simNumber);

        Query query = dbRef.orderByChild(WishStrings.WISH_TITLE_KEY).equalTo(title);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0) {
                    Log.d("ADD_A_WISH", "Exists");
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_wish_exists), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("ADD_A_WISH", "Not exists");

                    Map<String, Object> tmpMap = new HashMap<>();
                    tmpMap.put(WishStrings.WISH_TITLE_KEY, title);
                    tmpMap.put(WishStrings.WISH_DESCRIPTION_KEY, description);
                    tmpMap.put(WishStrings.PROCESSING_WISH, 0);
                    tmpMap.put(WishStrings.WISH_ASSIGNED_TO, "");
                    tmpMap.put(WishStrings.PROCESSING_WISH_SINCE, "");

                    DatabaseReference tmpRef = dbRef.push();

                    tmpRef.updateChildren(tmpMap);

                    Log.d("ADD_A_WISH", "Success");
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_add_wish_success), Toast.LENGTH_SHORT).show();
                    finish();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });

        return true;

    }

    /**
     * This method check the validity of wish title and description.
     * <br>
     * Title is required, so can't be empty or contain only blank spaces. Title lenght must be
     * lesser and equal to 40.
     * <br>
     * Description is optional and is trimmed if containing only blank spaces. Description length
     * mu be lesser and equal to 50.
     * <br>
     * Also both can't contain \r\n char.
     *
     * @param title Title of the item to be added to your wishlist
     * @param description Description of the item to be added to your wishlist
     * @return true if data are valid, false otherwise
     */
    protected boolean validate(String title, String description) {

        if (description.trim().isEmpty())
            description="";

        return  !title.isEmpty() &&
                title.length() <= 40 &&
                description.length() <= 50 &&
                !title.contains("\r\n") &&
                !description.contains("\r\n") &&
                !(title.trim().isEmpty());

    }
}
