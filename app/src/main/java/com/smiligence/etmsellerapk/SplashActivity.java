package com.smiligence.etmsellerapk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmsellerapk.Common.CommonMethods;
import com.smiligence.etmsellerapk.bean.UserDetails;

public class SplashActivity extends AppCompatActivity {

    DatabaseReference sellerLoginDataRef;
    String username;
    private static int splashTimeOut = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sellerLoginDataRef=  CommonMethods.fetchFirebaseDatabaseReference("SellerLoginDetails");

   new Handler ().postDelayed(new Runnable() {
       @Override
       public void run() {
           SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
           SharedPreferences.Editor editor = sharedPreferences.edit ();
           username = sharedPreferences.getString ( "sellerId", "" );
           if (!username.equals ( "" )) {
               Query query=sellerLoginDataRef.child(username);
               query.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       if (dataSnapshot.getChildrenCount() > 0) {

                           UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                           Intent intent = new Intent(SplashActivity.this, SellerProfileActivity.class);
                           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                           startActivity(intent);
                           finish();

                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });



           } else {
               Intent intent = new Intent ( SplashActivity.this, OtpRegister.class );
               intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
               startActivity ( intent );
           }

       }
   },splashTimeOut);

    }
}