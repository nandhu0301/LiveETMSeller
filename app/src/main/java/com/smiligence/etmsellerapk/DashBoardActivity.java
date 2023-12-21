package com.smiligence.etmsellerapk;

import static com.smiligence.etmsellerapk.Common.CommonMethods.screenPermissions;
import static com.smiligence.etmsellerapk.Common.Constant.BILLED_DATE_COLUMN;
import static com.smiligence.etmsellerapk.Common.Constant.ORDER_DETAILS_FIREBASE_TABLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.github.mikephil.charting.charts.BarChart;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smiligence.etmsellerapk.Common.CommonMethods;
import com.smiligence.etmsellerapk.Common.Constant;
import com.smiligence.etmsellerapk.Common.DateUtils;
import com.smiligence.etmsellerapk.Common.NetworkStateReceiver;
import com.smiligence.etmsellerapk.Common.TextUtils;
import com.smiligence.etmsellerapk.bean.ItemDetails;
import com.smiligence.etmsellerapk.bean.OrderDetails;
import com.smiligence.etmsellerapk.bean.UserDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


public class DashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , NetworkStateReceiver.NetworkStateReceiverListener{

    TextView sales, items, quantity, customer, bill_report;
    Button viewSalesReport, viewItemsReport;
    DatabaseReference billdataref,orderdetailRef;
    BarChart barChart, salesBarChart;
    final ArrayList<OrderDetails> billDetailsArrayList = new ArrayList<>();
    final ArrayList<ItemDetails> itemDetailsArrayList = new ArrayList<>();
    List<String> customerList = new ArrayList<>();
    List<String> billTimeArrayList = new ArrayList<>();
    List<String> itemList = new ArrayList<>();
    List<String> storeList = new ArrayList<>();
    ArrayList<String> billList = new ArrayList<>();
    final ArrayList<String> itemName = new ArrayList<>();
    int uniqueItemCount = 0;
    int todaysTotalSalesAmt = 0;
    int todaysTotalQty = 0;
    int totalamount =0;
    int uniqueCustomerCount = 0;
    int totalItemCount = 0;
    final boolean[] onDataChangeCheck = {false};
    public static TextView textViewUsername;
    public static ImageView imageView;
    public static View mHeaderView;
    public static String storeName, storeImage, storePincode;
    public static Menu menuNav;
    public static String saved_customerPhonenumber, saved_id;
    HashMap<String, Integer> billAmountHashMap = new HashMap<>();
    NavigationView navigationView;
    public static String approvedStatus;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    boolean checkNotification = true;
    Ringtone r;
    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetails = new ArrayList<>();
    String sellerIdIntent,storeImageIntent,storeNameIntent,storePinCode;
    Query billDetailsQuery;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        viewSalesReport = findViewById(R.id.viewreport);
        sales = findViewById(R.id.sales);
        items = findViewById(R.id.items);
        quantity = findViewById(R.id.quantity);
        customer = findViewById(R.id.customer);
        bill_report = findViewById(R.id.bill);

        barChart = findViewById(R.id.barChart);
        viewItemsReport = findViewById(R.id.itemReports);
        salesBarChart = findViewById(R.id.salesBarChart);
        // Utils.getDatabase();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(Constant.DASHBOARD);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);


        navigationView.setNavigationItemSelectedListener(DashBoardActivity.this);
        drawer.setDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.dashboard);

        textViewUsername = (TextView) mHeaderView.findViewById(R.id.name);
        imageView = (ImageView) mHeaderView.findViewById(R.id.imageViewheader);


        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();

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

        billdataref = CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);

        billDetailsQuery = billdataref.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());



        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_customerPhonenumber = loginSharedPreferences.getString("sellerPhoneNumber", "");
        saved_id = loginSharedPreferences.getString("sellerId", "");


        orderdetailRef =CommonMethods.fetchFirebaseDatabaseReference("OrderDetails");

        //    loadFunction();

        if (!"".equals(SellerProfileActivity.saved_id) && SellerProfileActivity.saved_id!=null && !"".equals(SellerProfileActivity.saved_customerPhonenumber) || SellerProfileActivity.saved_customerPhonenumber!=null && !"".equals(SellerProfileActivity.storeImage) && SellerProfileActivity.storeImage!=null ) {
            sellerIdIntent= SellerProfileActivity.saved_id;
            storeNameIntent = SellerProfileActivity.storeName;
            storeImageIntent= SellerProfileActivity.storeImage;
            storePinCode= SellerProfileActivity.storePincode;
        }
        else if (!"".equals(DashBoardActivity.saved_id) && DashBoardActivity.saved_id!=null && !"".equals(DashBoardActivity.saved_customerPhonenumber) || DashBoardActivity.saved_customerPhonenumber!=null && !"".equals(DashBoardActivity.storeImage) && DashBoardActivity.storeImage!=null  ) {
            sellerIdIntent= DashBoardActivity.saved_id;
            storeNameIntent = DashBoardActivity.storeName;
            storeImageIntent= DashBoardActivity.storeImage;
            storePinCode= DashBoardActivity.storePincode;
        }



        Query query = CommonMethods.fetchFirebaseDatabaseReference("SellerLoginDetails").orderByChild("phoneNumber").equalTo(saved_customerPhonenumber);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() > 0) {

                    for (DataSnapshot approvedStatusSnap : dataSnapshot.getChildren()) {
                        UserDetails userDetails = approvedStatusSnap.getValue(UserDetails.class);
                        approvedStatus = userDetails.getApprovalStatus();
                        storeName = userDetails.getStoreName();
                        storeImage = userDetails.getStoreLogo();
                        storePincode = userDetails.getPincode();
                        textViewUsername.setText(storeName);
                      /*  if (storeImage != null && !"".equals(storeImage)) {
                            Picasso.get().load(String.valueOf(Uri.parse(storeImage))).into(imageView);
                            Glide.with(getApplicationContext()).load(storeImage).into(imageView)''
                        }*/
                        screenPermissions();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewSalesReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, ReportGenerationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        viewItemsReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashBoardActivity.this, ItemsReportGenerationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sellerProfile) {
            Intent intent = new Intent(DashBoardActivity.this, SellerProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.addItem) {
            Intent intent = new Intent(DashBoardActivity.this, AddItemActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.orderHistory) {
            Intent intent = new Intent(DashBoardActivity.this, OrderHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }else if (id == R.id.add_description) {
            Intent intent = new Intent(DashBoardActivity.this, Add_Description_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }else if (id == R.id.payments) {
            Intent intent = new Intent(DashBoardActivity.this, PaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }


        else if (id == R.id.dashboard) {
            Intent intent = new Intent(DashBoardActivity.this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.logout) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DashBoardActivity.this);
            bottomSheetDialog.setContentView(R.layout.logout_confirmation);
            Button logout = bottomSheetDialog.findViewById(R.id.logout);
            Button stayinapp = bottomSheetDialog.findViewById(R.id.stayinapp);

            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!((Activity) DashBoardActivity.this).isFinishing()) {

                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(DashBoardActivity.this, OtpRegister.class);
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
        } else if (id == R.id.storetimings) {
            Intent intent = new Intent(getApplicationContext(), StoreMaintainanceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else if (id == R.id.contactus) {
            Intent intent = new Intent(getApplicationContext(), ContactUsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
      //  loadFunction();
        loadFunction1();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
    //    loadFunction();
        loadFunction1();

        startNetworkBroadcastReceiver(this);
        registerNetworkBroadcastReceiver(this);

    }





   public void loadFunction1() {
        billDetailsQuery = billdataref.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());
        billDetailsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearData();
                if (dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot billSnapShot : dataSnapshot.getChildren()) {


                        billDetailsArrayList.add(billSnapShot.getValue(OrderDetails.class));
                        itemDetailsArrayList.add(billSnapShot.getValue(ItemDetails.class));

                        onDataChangeCheck[0] = true;
                    }


                    if (onDataChangeCheck[0]) {

                        Iterator billIterator = billDetailsArrayList.iterator();

                        while (billIterator.hasNext()) {

                            OrderDetails orderDetails = (OrderDetails) billIterator.next();


                                if (!(orderDetails.getOrderStatus().equals("Order Canceled"))) {



                                    Iterator itemIterator = orderDetails.getItemDetailList().iterator();
                                    totalamount  =0;
                                    Boolean ischeck = true ;

                                    while (itemIterator.hasNext()) {

                                        ItemDetails itemDetails = (ItemDetails) itemIterator.next();
                                        itemName.add(itemDetails.getItemName());
                                        todaysTotalQty = todaysTotalQty + itemDetails.getItemBuyQuantity();
                                        itemList.add(itemDetails.getItemName());

                                        if (itemDetails.getSellerId().equals(sellerIdIntent)){
                                             if (ischeck) {
                                                 ischeck = false;
                                                 billTimeArrayList.add(DateUtils.fetchTime(orderDetails.getOrderCreateDate()));
                                                 customerList.add(orderDetails.getCustomerName());
                                                 storeList.add(orderDetails.getStoreName());
                                                 todaysTotalSalesAmt += (orderDetails.getTotalAmount());
                                                 sales.setText(String.valueOf(todaysTotalSalesAmt));
                                                 billList.add(orderDetails.getOrderId());
                                             }

                                            totalamount = totalamount +itemDetails.getTotalItemQtyPrice();
                                        }
                                    }

                                    billAmountHashMap.put(DateUtils.fetchTimewithSeconds(orderDetails.getOrderCreateDate()), totalamount);

                                }

                                quantity.setText(String.valueOf(todaysTotalQty));

                        }

                        ArrayList<String> newItemList = TextUtils.removeDuplicates((ArrayList<String>) itemList);
                        ArrayList<String> newCustomerList = TextUtils.removeDuplicates((ArrayList<String>) customerList);
                        ArrayList<String> newItemNameList = TextUtils.removeDuplicates((ArrayList<String>) itemName);
                        ArrayList<String> newStoreNameList = TextUtils.removeDuplicates((ArrayList<String>) storeList);

                        uniqueItemCount = uniqueItemCount + newItemList.size();
                        uniqueCustomerCount = uniqueCustomerCount + newCustomerList.size();
                        items.setText(String.valueOf(uniqueItemCount));
                        TextUtils.removeDuplicates(billList);

                        for (int i=0;i< billList.size();i++){
                            Log.e("billlistsixe",billList.get(i));
                        }

                        int noOfBills = billList.size();
                        bill_report.setText(String.valueOf(noOfBills));
                        customer.setText(String.valueOf(uniqueCustomerCount));


                        CommonMethods.loadBarChart(barChart, (ArrayList<String>) billTimeArrayList);
                        barChart.animateXY(7000, 5000);
                        barChart.invalidate();
                        barChart.getDrawableState();
                        barChart.setPinchZoom(true);


                        CommonMethods.loadSalesBarChart(salesBarChart, billAmountHashMap);
                        salesBarChart.animateXY(7000, 5000);
                        salesBarChart.invalidate();
                        salesBarChart.getDrawableState();
                        salesBarChart.setPinchZoom(true);

                    }
                }
            }

            private void clearData() {
                billDetailsArrayList.clear();
                customerList.clear();
                itemList.clear();
                itemDetailsArrayList.clear();
                billTimeArrayList.clear();
                storeList.clear();
                billList.clear();
                itemName.clear();
                uniqueItemCount = 0;
                uniqueCustomerCount = 0;
                todaysTotalSalesAmt = 0;
                todaysTotalQty = 0;
                totalItemCount = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void createNotification(String res, String orderId) {
        int count = 0;
        if (count == 0) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DashBoardActivity.this, default_notification_channel_id)
                    .setSmallIcon(R.mipmap.ic_launcher_new)
                    .setContentTitle("New Order")
                    .setContentText(res)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(), 0));


            Intent secondActivityIntent = new Intent(this, SellerProfileActivity.class);
            secondActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


            PendingIntent secondActivityPendingIntent = PendingIntent.getActivity(this, 0, secondActivityIntent, PendingIntent.FLAG_ONE_SHOT);
            mBuilder.addAction(R.mipmap.ic_launcher_new, "View", secondActivityPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableVibration(true);
                notificationChannel.setLockscreenVisibility(0);
                notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                try {
                    Uri path = Uri.parse("android.resource://com.smiligenceUAT1.metrozsellerpartner/" + R.raw.old_telephone_tone);
                    r = RingtoneManager.getRingtone(getApplicationContext(), path);
                    r.play();
                } catch (Exception e) {
                }

                mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel);

            }
            assert mNotificationManager != null;
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            Random random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;
            notificationManager.notify(Integer.parseInt(orderId), notification);
            count = count + 1;
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this,SellerProfileActivity.class);
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }


    @Override
    protected void onPause() {
        startNetworkBroadcastReceiver(this);
        unregisterNetworkBroadcastReceiver(this);
        super.onPause();
    }



    @Override
    public void networkAvailable() {
        alertDialog.dismiss();
    }

    @Override
    public void networkUnavailable() {
        if (!((Activity) DashBoardActivity.this).isFinishing()) {
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

