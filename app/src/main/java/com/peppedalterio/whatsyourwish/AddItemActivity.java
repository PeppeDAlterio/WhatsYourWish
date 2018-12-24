package com.peppedalterio.whatsyourwish;

import android.content.Intent;
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
                Toast.makeText(getApplicationContext(), "Errore.", Toast.LENGTH_SHORT).show();
        });

    }

    private boolean addItemClick() {

        String title = ((TextView)findViewById(R.id.wishtitle)).getText().toString();
        String description = ((TextView)findViewById(R.id.wishdescription)).getText().toString();

        if(!validate(title, description)) {
            return false;
        }

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(simNumber);

        Query query = dbRef.orderByChild("TITOLO").equalTo(title);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0) {
                    Log.d("EXISTS", "esiste!!!");
                    Toast.makeText(getApplicationContext(), "Desiderio gia' presente in lista.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("NOT_EXISTS", "non esiste :)");

                    Map<String, Object> tmpMap = new HashMap<>();
                    tmpMap.put("TITOLO", title);
                    tmpMap.put("DESCRIZIONE", description);

                    DatabaseReference tmpRef = dbRef.push();
                    tmpRef.updateChildren(tmpMap);

                    Toast.makeText(getApplicationContext(), "Desiderio aggiunto con successo", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("TAG", "onCancelled", databaseError.toException());
            }
        });

        return true;

    }

    private boolean validate(String title, String description) {

        if(title.isEmpty() || description.isEmpty() || title.length()>20 || description.length()>50
                || title.contains("\r\n") || description.contains("\r\n"))
            return false;
        else
            return true;

    }
}
