package com.example.green.bierliste;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartMenue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_menue);
    }

    public void hostButtonClicked(View view){
        Intent intent = new Intent(this, CreateParty.class);

        startActivity(intent);
    }

    public void joinButtonClicked(View view) {

    }
}
