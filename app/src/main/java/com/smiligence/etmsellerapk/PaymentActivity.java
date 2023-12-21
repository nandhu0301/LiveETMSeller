package com.smiligence.etmsellerapk;

import static com.smiligence.etmsellerapk.Common.Constant.ORDER_DETAILS_FIREBASE_TABLE;
import static com.smiligence.etmsellerapk.Common.Constant.PAYMENTS;
import static com.smiligence.etmsellerapk.Common.Constant.SELLER_DETAILS_TABLE;
import static com.smiligence.etmsellerapk.Common.Constant.SELLER_PAYMENTS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmsellerapk.Adapter.PaymentAdapter;
import com.smiligence.etmsellerapk.Common.CommonMethods;
import com.smiligence.etmsellerapk.Common.Constant;
import com.smiligence.etmsellerapk.Common.NetworkStateReceiver;
import com.smiligence.etmsellerapk.bean.ItemDetails;
import com.smiligence.etmsellerapk.bean.OrderDetails;
import com.smiligence.etmsellerapk.bean.PaymentDetails;
import com.smiligence.etmsellerapk.bean.SellerPaymentDetails;
import com.smiligence.etmsellerapk.bean.UserDetails;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NetworkStateReceiver.NetworkStateReceiverListener{

    ListView list_details;
    String firstMonthdate ,lastMonthdate;
    final ArrayList<String> list = new ArrayList<String>();
    DatabaseReference orderdetailRef,sellerDataRef,sellerPaymentsRef,paymentRef ;
    OrderDetails orderDetails;
    ArrayList<PaymentDetails> paymentDetailsArrayList = new ArrayList<PaymentDetails>();
    ArrayList<ItemDetails> itemDetailsArrayList = new ArrayList<>();
    String sellerIdIntent,storeNameIntent,storeImageIntent;
    int resultTotalAmount = 0;
    int totalbilledAmount =0;
    int admindeliveryAmount = 0;
    int totaldeliveryAmount =0;
    int sellerdeliveryAmount = 0;
    TextView totalSalesAmount;
    final ArrayList<String> orderListSize = new ArrayList<String>();
    String storeType;
    int Percentage;
    NavigationView navigationView;
    View mHeaderView;
    TextView textViewUsername,plandetail_txt;
    ImageView imageView;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        plandetail_txt = findViewById(R.id.currentplan_detail);
        Toolbar toolbar = (Toolbar) findViewById ( R.id.toolbar );
        toolbar.setTitle ( Constant.PAYMENT_SETTLEMENT );
        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );

        toggle.syncState ();
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.white ) );
        navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( PaymentActivity.this );
        drawer.setDrawerListener ( toggle );
        navigationView.setCheckedItem ( R.id.payments );
        mHeaderView = navigationView.getHeaderView ( 0 );

        textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name );
        imageView = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader );

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


        list_details=findViewById(R.id.list_details);
        totalSalesAmount = findViewById(R.id.totalSalesamount);

        orderdetailRef = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);
        sellerDataRef =CommonMethods.fetchFirebaseDatabaseReference(SELLER_DETAILS_TABLE);
        sellerPaymentsRef = CommonMethods.fetchFirebaseDatabaseReference(SELLER_PAYMENTS);
        paymentRef = CommonMethods.fetchFirebaseDatabaseReference(PAYMENTS);
        list.clear();


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

        // Get the current month and year
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

// Set the calendar to the first day of the month
        calendar.set(year, month, 1);
        Date firstDate = calendar.getTime();

// Set the calendar to the last day of the month
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate = calendar.getTime();

// Format the dates in "DD-MM-YYYY" format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());


// Print the first and last date of the month
        firstMonthdate = dateFormat.format(firstDate);
        lastMonthdate = dateFormat.format(lastDate);


//run a for loop for  start to end date in a month

        // Get the current date
        Calendar currentDate = Calendar.getInstance();

// Set the start date to the first day of the current month
        Calendar startDate = (Calendar) currentDate.clone();
        startDate.set(Calendar.DAY_OF_MONTH, 1);

// Set the end date to the last day of the current month
        Calendar endDate = (Calendar) currentDate.clone();
        endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));

// Create a list to store the dates
        List<Calendar> dateList = new ArrayList<>();

// Iterate over each day within the range
        for (Calendar date = startDate; date.compareTo(endDate) <= 0; date.add(Calendar.DATE, 1)) {
            // Add the date to the list
            dateList.add((Calendar) date.clone());
        }

// Print the dates in the list
        for (Calendar date : dateList) {
            int yearr = date.get(Calendar.YEAR);
            int monthh = date.get(Calendar.MONTH) + 1; // Months are zero-based, so add 1
            int day = date.get(Calendar.DAY_OF_MONTH);
            // Format the date as desired (e.g., "YYYY-MM-DD")
            String formattedDate = String.format("%02d-%02d-%04d", day, monthh, yearr);
            System.out.println(formattedDate);
            list.add( formattedDate);
        }



        orderdetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot itemSnap : snapshot.getChildren()) {
                        orderDetails = itemSnap.getValue(OrderDetails.class);
                        if (orderDetails.getOrderStatus().equals("Completed")){


                            itemDetailsArrayList = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();

                            if (itemDetailsArrayList.size() > 0 && itemDetailsArrayList != null) {
                                for(int k=0;k< itemDetailsArrayList.size();k++){
                                    if (itemDetailsArrayList.get(k).getSellerId().equals(sellerIdIntent)){


                                        for (int i = 0; i < list.size(); i++) {
                                            if (orderDetails.getFormattedDate().equals(list.get(i))) {
                                                if (itemDetailsArrayList.get(k).getDeliveryby().equals("Admin")) {
                                                    admindeliveryAmount = admindeliveryAmount + 100;
                                                } else {
                                                    sellerdeliveryAmount = sellerdeliveryAmount + 100;
                                                }
                                                totaldeliveryAmount = totaldeliveryAmount + 100;
                                                totalbilledAmount = totalbilledAmount +itemDetailsArrayList.get(k).getTotalItemQtyPrice() ;
                                                resultTotalAmount = resultTotalAmount + itemDetailsArrayList.get(k).getTotalItemQtyPrice() +100;
                                                orderListSize.add(orderDetails.getOrderId());

                                            }
                                        }
                                    }

                                }
                            }
                        }

                    }
                    totalSalesAmount.setText("₹ " + resultTotalAmount);

                    DatabaseReference startTimeDataRef = CommonMethods.fetchFirebaseDatabaseReference(PAYMENTS).child(sellerIdIntent).child(firstMonthdate);
                    startTimeDataRef.child("startDate").setValue(firstMonthdate);
                    startTimeDataRef.child("endDate").setValue(lastMonthdate);
                    startTimeDataRef.child("totalBilledAmount").setValue(totalbilledAmount);
                    startTimeDataRef.child("totalAmount").setValue(resultTotalAmount);
                    startTimeDataRef.child("paymentStatus").setValue("Pending");
                    startTimeDataRef.child("orderCount").setValue(String.valueOf(orderListSize.size()));
                    startTimeDataRef.child("totaldeliveryamount").setValue(totaldeliveryAmount);
                    startTimeDataRef.child("admindeliveryAmount").setValue(admindeliveryAmount);
                    startTimeDataRef.child("sellerdeliveryAmount").setValue(sellerdeliveryAmount);

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sellerDataRef.child(sellerIdIntent).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                storeType = userDetails.getPaymentType();
                if (storeType == null) {
                    storeType = "Basic";
                }


                if (storeType != null) {
                    sellerPaymentsRef.child(storeType).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount() > 0) {
                                SellerPaymentDetails sellerPaymentDetails = snapshot.getValue(SellerPaymentDetails.class);
                                Percentage = sellerPaymentDetails.getPercentage();
                            }
                            paymentRef.child(sellerIdIntent).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getChildrenCount() > 0) {
                                        paymentDetailsArrayList.clear();
                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                            PaymentDetails paymentDetails = snap.getValue(PaymentDetails.class);
                                            paymentDetailsArrayList.add(paymentDetails);
                                            Collections.reverse(paymentDetailsArrayList);
                                        }
                                        plandetail_txt.setText(storeType);
                                        PaymentAdapter paymentAdapter = new PaymentAdapter(PaymentActivity.this, paymentDetailsArrayList, storeType, Percentage);
                                        list_details.setAdapter(paymentAdapter);
                                        paymentAdapter.notifyDataSetChanged();
                                        if (paymentAdapter != null) {
                                            int totalHeight = 0;
                                            for (int i = 0; i < paymentAdapter.getCount(); i++) {
                                                View listItem = paymentAdapter.getView(i, null, list_details);
                                                listItem.measure(0, 0);
                                                totalHeight += listItem.getMeasuredHeight();
                                            }
                                            ViewGroup.LayoutParams params = list_details.getLayoutParams();
                                            params.height = totalHeight + (list_details.getDividerHeight() * (paymentAdapter.getCount() - 1));
                                            list_details.setLayoutParams(params);
                                            list_details.requestLayout();
                                            list_details.setAdapter(paymentAdapter);
                                            paymentAdapter.notifyDataSetChanged();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,SellerProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId ();

        if (id == R.id.sellerProfile) {

            Intent intent = new Intent ( PaymentActivity.this, SellerProfileActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.addItem) {

            Intent intent = new Intent ( PaymentActivity.this, AddItemActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.add_description) {
            Intent intent = new Intent(getApplicationContext(), Add_Description_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        else if (id == R.id.payments) {
            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }


        else if (id == R.id.orderHistory) {

            Intent intent = new Intent ( PaymentActivity.this, OrderHistoryActivity.class );
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

        } else if (id == R.id.logout) {

            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( PaymentActivity.this );
            bottomSheetDialog.setContentView ( R.layout.logout_confirmation );
            Button logout = bottomSheetDialog.findViewById ( R.id.logout );
            Button stayinapp = bottomSheetDialog.findViewById ( R.id.stayinapp );

            bottomSheetDialog.show ();
            bottomSheetDialog.setCancelable ( false );

            logout.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {

                    if (!((Activity) PaymentActivity.this).isFinishing()) {

                        SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                        SharedPreferences.Editor editor = sharedPreferences.edit ();
                        editor.clear ();
                        editor.commit ();

                        Intent intent = new Intent ( PaymentActivity.this, OtpRegister.class );
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
        if (!((Activity) PaymentActivity.this).isFinishing()) {
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