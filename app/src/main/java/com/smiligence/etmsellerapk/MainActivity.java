package com.smiligence.etmsellerapk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.smiligence.etmsellerapk.bean.CategoryDetails;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference ;
    CategoryDetails categoryDetails = new CategoryDetails() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);








        
    }
}