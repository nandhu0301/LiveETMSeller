package com.smiligence.etmsellerapk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.smiligence.etmsellerapk.Common.Constant;
import com.squareup.picasso.Picasso;

public class ContactUsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
//7845926102
    NavigationView navigationView;
    TextView whatsApp;
    View mHeaderView;
    public static TextView textViewUsername;
    ImageView imageView;
    String sellerIdIntent,storeImageIntent,storeNameIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        Toolbar toolbar = (Toolbar) findViewById ( R.id.toolbar );
        ActionBar actionBar = getSupportActionBar ();
        toolbar.setTitle (Constant.CONTACT_SUPPORT);

        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        toggle.syncState ();
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.white ) );
        navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( ContactUsActivity.this );
        drawer.setDrawerListener ( toggle );
        navigationView.setCheckedItem ( R.id.contactus );
        mHeaderView = navigationView.getHeaderView ( 0 );

        textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name );
        imageView = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader );
        whatsApp=findViewById(R.id.watsapptext);


        if (!"".equals(SellerProfileActivity.saved_id) && SellerProfileActivity.saved_id!=null && !"".equals(SellerProfileActivity.saved_customerPhonenumber) || SellerProfileActivity.saved_customerPhonenumber!=null && !"".equals(SellerProfileActivity.storeImage) && SellerProfileActivity.storeImage!=null )
        {
            sellerIdIntent= SellerProfileActivity.saved_id;
            storeNameIntent =SellerProfileActivity.storeName;
            storeImageIntent=SellerProfileActivity.storeImage;
        }
        /*else if (!"".equals(DashBoardActivity.saved_id) && DashBoardActivity.saved_id!=null && !"".equals(DashBoardActivity.saved_customerPhonenumber) || DashBoardActivity.saved_customerPhonenumber!=null && !"".equals(DashBoardActivity.storeImage) && DashBoardActivity.storeImage!=null  )
        {
            sellerIdIntent= DashBoardActivity.saved_id;
            storeNameIntent =DashBoardActivity.storeName;
            storeImageIntent=DashBoardActivity.storeImage;
        }*/

        if (storeNameIntent != null && !"".equals (storeNameIntent)) {
            textViewUsername.setText ( storeNameIntent );
        }
        if (storeImageIntent!= null && !"".equals ( storeImageIntent)) {
            Picasso.get().load ( String.valueOf ( Uri.parse (storeImageIntent) ) ).into ( imageView );
        }

      //  loadFunction();

        whatsApp.setOnClickListener (v -> {
            String url = "https://api.whatsapp.com/send?phone=" + whatsApp.getText ().toString ();
            Intent i = new Intent ( Intent.ACTION_VIEW );
            i.setData ( Uri.parse ( url ) );
            startActivity ( i );
        });


    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId ();

        if (id == R.id.sellerProfile) {

            Intent intent = new Intent ( ContactUsActivity.this, SellerProfileActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.addItem) {

            Intent intent = new Intent ( ContactUsActivity.this, AddItemActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        }  else if (id == R.id.orderHistory) {


            Intent intent = new Intent ( ContactUsActivity.this, OrderHistoryActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.storetimings) {
            Intent intent = new Intent ( getApplicationContext (), StoreMaintainanceActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.contactus) {
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }  else if (id == R.id.add_description) {
            Intent intent = new Intent(getApplicationContext(), Add_Description_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        else if (id == R.id.payments) {
            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

        else if (id == R.id.logout) {

            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( ContactUsActivity.this );
            bottomSheetDialog.setContentView ( R.layout.logout_confirmation );
            Button logout = bottomSheetDialog.findViewById ( R.id.logout );
            Button stayinapp = bottomSheetDialog.findViewById ( R.id.stayinapp );

            bottomSheetDialog.show ();
            bottomSheetDialog.setCancelable ( false );

            logout.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {

                    if (!((Activity) ContactUsActivity.this).isFinishing()) {
                        // DatabaseReference ref = FirebaseDatabase.getInstance ().getReference ( "SellerLoginDetails" ).child ( String.valueOf ( SellerProfileActivity.saved_id ) );
                        //ref.child ( "signIn" ).setValue ( false );

                        SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                        SharedPreferences.Editor editor = sharedPreferences.edit ();
                        editor.clear ();
                        editor.commit ();

                        Intent intent = new Intent ( ContactUsActivity.this, OtpRegister.class );
                        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                        startActivity ( intent );
                    }
                    bottomSheetDialog.dismiss ();

                }
            } );
            stayinapp.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    navigationView.setCheckedItem ( R.id.orderHistory );
                    bottomSheetDialog.dismiss ();
                }
            } );

        }else if (id == R.id.dashboard) {
            Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext (), SellerProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}