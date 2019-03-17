package com.peppedalterio.whatsyourwish.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.peppedalterio.whatsyourwish.R;
import com.peppedalterio.whatsyourwish.model.MyWishlistModel;
import com.peppedalterio.whatsyourwish.util.InternetConnection;

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
            return false;
        }

        MyWishlistModel wishlistModel = new MyWishlistModel(simNumber);
        return wishlistModel.addWishlistItem(title, description, this);

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

        return MyWishlistModel.validate(title, description);

    }

}
