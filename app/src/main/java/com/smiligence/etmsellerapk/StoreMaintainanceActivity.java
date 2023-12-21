package com.smiligence.etmsellerapk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmsellerapk.Common.CommonMethods;
import com.smiligence.etmsellerapk.Common.Constant;
import com.smiligence.etmsellerapk.Common.NetworkStateReceiver;
import com.smiligence.etmsellerapk.Common.StoreTimings;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StoreMaintainanceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,NetworkStateReceiver.NetworkStateReceiverListener {

    DatabaseReference storeTimingMaintenanceDataRef,metrozStoteTimingDataRef;
    View mHeaderView;
    NavigationView navigationView;
    TextView textViewUsername;
    ImageView imageView;
    String sellerIdIntent,storeImageIntent,storeNameIntent;
    Switch openCloseStatus;
    public static String DATE_FORMAT = "MMMM dd, YYYY";
    String currentDateAndTime;
    Date currentLocalTime;
    String currentTime;
    DateFormat date;
    String pattern = "hh:mm aa";
    SimpleDateFormat sdf = new SimpleDateFormat ( pattern );
    String metrozStartTime;
    String metrozStopTime;
    public static Menu menuNav;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_maintainance);

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        disableAutofill();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(Constant.STORE_TIMINGS);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(StoreMaintainanceActivity.this);
        drawer.setDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.storetimings);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.name);
        imageView = (ImageView) mHeaderView.findViewById(R.id.imageViewheader);


        if (!"".equals(SellerProfileActivity.saved_id) && SellerProfileActivity.saved_id!=null && !"".equals(SellerProfileActivity.saved_customerPhonenumber) || SellerProfileActivity.saved_customerPhonenumber!=null && !"".equals(SellerProfileActivity.storeImage) && SellerProfileActivity.storeImage!=null )

        {
            sellerIdIntent= SellerProfileActivity.saved_id;
            storeNameIntent =SellerProfileActivity.storeName;
            storeImageIntent=SellerProfileActivity.storeImage;
        }
        else if (!"".equals(DashBoardActivity.saved_id) && DashBoardActivity.saved_id!=null && !"".equals(DashBoardActivity.saved_customerPhonenumber) || DashBoardActivity.saved_customerPhonenumber!=null && !"".equals(DashBoardActivity.storeImage) && DashBoardActivity.storeImage!=null  )
        {
            sellerIdIntent= DashBoardActivity.saved_id;
            storeNameIntent =DashBoardActivity.storeName;
            storeImageIntent=DashBoardActivity.storeImage;
        }

        if (storeNameIntent != null && !"".equals (storeNameIntent)) {
            textViewUsername.setText ( storeNameIntent );
        }
        if (storeImageIntent!= null && !"".equals ( storeImageIntent)) {
            Picasso.get().load ( String.valueOf ( Uri.parse (storeImageIntent) ) ).into ( imageView );
        }

        openCloseStatus = findViewById ( R.id.openShopManuallySwitch );


        Calendar cal = Calendar.getInstance ();
        currentLocalTime = cal.getTime ();
        date = new SimpleDateFormat( "HH:mm aa" );
        currentTime = date.format ( currentLocalTime );
        DateFormat dateFormat = new SimpleDateFormat ( DATE_FORMAT );
        currentDateAndTime = dateFormat.format ( new Date() );


        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();



        storeTimingMaintenanceDataRef = CommonMethods.fetchFirebaseDatabaseReference( "storeTimingMaintenance");


        openCloseStatus.setChecked(false);

/*
        openCloseStatus.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                            if (isChecked) {
                                AlertDialog.Builder builder= new AlertDialog.Builder(StoreMaintainanceActivity.this);
                                builder .setTitle("Store Status");

                                //Setting message manually and performing action on button click
                                builder.setMessage("Do you want to change store status ?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                DatabaseReference startTimeDataRef = storeTimingMaintenanceDataRef.child(sellerIdIntent);
                                                startTimeDataRef.child("storeStatus").setValue("Opened");
                                                startTimeDataRef.child("creationDate").setValue(currentDateAndTime);
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //  Action for 'NO' Button
                                                dialog.cancel();
                                                openCloseStatus.setChecked(false);
                                            }
                                        });
                                AlertDialog alert = builder.create();
                                //Setting the title manually
                                alert.setTitle("Store Status");
                                alert.show();

                            } else {
                                AlertDialog.Builder builder= new AlertDialog.Builder(StoreMaintainanceActivity.this);
                                builder .setTitle("Store Status");

                                //Setting message manually and performing action on button click
                                builder.setMessage("Do you want to close the store ?")
                                        .setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                DatabaseReference startTimeDataRef = storeTimingMaintenanceDataRef.child(sellerIdIntent);
                                                startTimeDataRef.child("storeStatus").setValue("Closed");
                                                startTimeDataRef.child("creationDate").setValue(currentDateAndTime);
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //  Action for 'NO' Button
                                                dialog.cancel();
                                                openCloseStatus.setChecked(true);
                                                dialog.dismiss();
                                            }
                                        });

                                AlertDialog alert = builder.create();
                                //Setting the title manually
                                alert.setTitle("Store Status");
                                alert.show();
                            }



            }
        } );
*/

        openCloseStatus.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    DatabaseReference startTimeDataRef = storeTimingMaintenanceDataRef.child(sellerIdIntent);
                    startTimeDataRef.child("storeStatus").setValue("Opened");
                    startTimeDataRef.child("creationDate").setValue(currentDateAndTime);
                } else {
                    DatabaseReference startTimeDataRef = storeTimingMaintenanceDataRef.child(sellerIdIntent);
                    startTimeDataRef.child("storeStatus").setValue("Closed");
                    startTimeDataRef.child("creationDate").setValue(currentDateAndTime);
                }



            }
        } );

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sellerProfile) {
            Intent intent = new Intent(getApplicationContext(), SellerProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.storetimings) {
            Intent intent = new Intent(getApplicationContext(), StoreMaintainanceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        else if (id == R.id.addItem) {
            Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }  else if (id == R.id.orderHistory) {
            Intent intent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.dashboard) {
            Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }  else if (id == R.id.add_description) {
            Intent intent = new Intent(getApplicationContext(), Add_Description_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }



        else if (id == R.id.contactus) {
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.payments) {
            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (id == R.id.logout) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(StoreMaintainanceActivity.this);
            bottomSheetDialog.setContentView(R.layout.logout_confirmation);
            Button logout = bottomSheetDialog.findViewById(R.id.logout);
            Button stayinapp = bottomSheetDialog.findViewById(R.id.stayinapp);

            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!((Activity) StoreMaintainanceActivity.this).isFinishing())
                    {
                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(getApplicationContext(), OtpRegister.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }

                    bottomSheetDialog.dismiss();
                }
            });

            stayinapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigationView.setCheckedItem(R.id.sellerProfile);
                    bottomSheetDialog.dismiss();
                }
            });
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent ( getApplicationContext (), SellerProfileActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    public String pad(int input)
    {

        String str = "";

        if (input >= 10) {

            str = Integer.toString(input);
        } else {
            str = "0" + Integer.toString(input);

        }
        return str;
    }






    @Override
    protected void onStart() {
        super.onStart();
        if(sellerIdIntent!=null && !sellerIdIntent.equals("")) {

            storeTimingMaintenanceDataRef.child(sellerIdIntent).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                            StoreTimings storeTimings = dataSnapshot.getValue(StoreTimings.class);


                            if (storeTimings.getStoreStatus().equals("Opened")) {
                                openCloseStatus.setChecked(true);
                            }
                            if (storeTimings.getStoreStatus().equals("Closed")) {
                                openCloseStatus.setChecked(false);
                            }

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    @Override
    protected void onPause() {
        startNetworkBroadcastReceiver(this);
        unregisterNetworkBroadcastReceiver(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        startNetworkBroadcastReceiver(this);
        registerNetworkBroadcastReceiver(this);
        super.onResume();
    }

    @Override
    public void networkAvailable() {
        alertDialog.dismiss();
    }

    @Override
    public void networkUnavailable() {
        if (!((Activity) StoreMaintainanceActivity.this).isFinishing()) {
            showCustomDialog();
        }
    }

    public void startNetworkBroadcastReceiver(Context currentContext) {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener) currentContext);
        registerNetworkBroadcastReceiver(currentContext);
    }


    public void registerNetworkBroadcastReceiver(Context currentContext) {
        currentContext.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void unregisterNetworkBroadcastReceiver(Context currentContext) {
        currentContext.unregisterReceiver(networkStateReceiver);
    }

    private void showCustomDialog() {

        alertDialog.setCancelable(false);
        alertDialog.show();

    }
}