package com.example.attendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class addloc extends AppCompatActivity {

    Button add_loc2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addloc);

        add_loc2 = findViewById(R.id.add_loc_id2);

        add_loc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app1();
            }
        });

    }

    private void app1() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}