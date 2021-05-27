package com.example.attendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    Button check_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        check_in = findViewById(R.id.chk_in);

        check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app3();
            }
        });

    }

    private void app3() {
        Intent intent = new Intent(this, detectface.class);
        startActivity(intent);
    }
}