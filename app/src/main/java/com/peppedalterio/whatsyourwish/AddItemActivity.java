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
import com.peppedalterio.whatsyourwish.pojo.Contact;
import com.peppedalterio.whatsyourwish.pojo.WishStrings;

import java.util.HashMap;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {

    private String simNumber;

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

    private boolean addItemClick() {

        String title = ((TextView)findViewById(R.id.wishtitle)).getText().toString();
        String description = ((TextView)findViewById(R.id.wishdescription)).getText().toString();

        if(!validate(title, description)) {
            return false;
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(simNumber);

        Query query = dbRef.orderByChild(WishStrings.WISH_TITLE_KEY).equalTo(title);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0) {
                    Log.d("EXISTS", "esiste!!!");
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_wish_exists), Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("NOT_EXISTS", "non esiste :)");

                    Map<String, Object> tmpMap = new HashMap<>();
                    tmpMap.put(WishStrings.WISH_TITLE_KEY, title);
                    tmpMap.put(WishStrings.WISH_DESCRIPTION_KEY, description);

                    DatabaseReference tmpRef = dbRef.push();
                    tmpRef.updateChildren(tmpMap);

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

    protected boolean validate(String title, String description) {

        if (description.trim().isEmpty())
            description="";

        return  !title.isEmpty() &&
                title.length() <= 20 &&
                description.length() <= 50 &&
                !title.contains("\r\n") &&
                !description.contains("\r\n") &&
                !(title.trim().isEmpty());

    }
}
