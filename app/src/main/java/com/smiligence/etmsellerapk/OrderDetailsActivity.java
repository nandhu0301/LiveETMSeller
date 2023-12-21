package com.smiligence.etmsellerapk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.smiligence.etmsellerapk.Adapter.ItemOrderDetails;
import com.smiligence.etmsellerapk.Common.CommonMethods;
import com.smiligence.etmsellerapk.Common.Constant;
import com.smiligence.etmsellerapk.Common.DateUtils;
import com.smiligence.etmsellerapk.Common.NetworkStateReceiver;
import com.smiligence.etmsellerapk.Common.TextUtils;
import com.smiligence.etmsellerapk.bean.ItemDetails;
import com.smiligence.etmsellerapk.bean.OrderDetails;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    ImageView backButton ;
    DatabaseReference orderdetailRef;
    String orderIdFromHistory ,orderstatus  ;
    int temp = 0,ordertemp =0;
    int total =0;
    int taxamount =0   ,sgst =0;
    double  cgst = 0;

    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetailsList1 = new ArrayList<ItemDetails> ();
    ArrayList<ItemDetails> itemDetailsList = new ArrayList<ItemDetails> ();
    ArrayList<ItemDetails> newitemDetailsList = new ArrayList<ItemDetails> ();
    Switch changeStatusToReadyForPickup, changeStatusToDeliveryOnTheWay;
    ArrayList<OrderDetails> billDetailsArrayList = new ArrayList<OrderDetails> ();
    RelativeLayout itemDetailsLayout, itemHeaderlayout, changeStatusToReadyForPickupLayout, changeStatusToDeliveryOnTheWayLayout;
    boolean check = true;
    ItemOrderDetails itemOrderDetails;
    ListView listView;
    TextView orderStatusText, order_status, customerPhoneNumber, storeNameText, orderTimeTxt, order_Date, order_Id,
            order_Total, type_Of_Payment, fullName, shipping_Address, noContactDelivery, anyInstructions, amount, shipping, wholeCharge, deliveryBoyName,CGSTtextview,SGSTtextview ,totaltaxtextview;
    ConstraintLayout orderDetailsLayout, paymentDetailsLayout, shippingAddressLayout, orderSummaryLayout, specialinstructionLayout;
    String sellerIdIntent,storeNameIntent,storeImageIntent;
    ImageView trackImage;
    Button chooseTrackImage;
    AlertDialog dialog;
    String statusChangeString;
    Uri mimageuri;
    Intent intentImage = new Intent();
    final static int PICK_IMAGE_REQUEST = 100;
    StorageReference imageFileStorageRef  ;
    StorageReference StorageRef;
    StorageTask  profile_StorageTask;
    Uri  profile_DownloadUrl;
    ItemDetails itemDetails ;
    int count=0;
    int shippingamount = 100;
    ProgressDialog progressdialog;
    public int deliverycount = 0;
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        backButton = findViewById ( R.id.back );
        changeStatusToReadyForPickupLayout = findViewById ( R.id.readyforPickup );
        changeStatusToDeliveryOnTheWayLayout = findViewById ( R.id.deliveryontheway );
        changeStatusToDeliveryOnTheWay = findViewById ( R.id.deliveryonway );
        changeStatusToReadyForPickup = findViewById ( R.id.readyforPickupstatuschange );

        changeStatusToReadyForPickupLayout.setVisibility ( View.VISIBLE );
        changeStatusToDeliveryOnTheWayLayout.setVisibility ( View.INVISIBLE );
        //Order details value
        orderDetailsLayout = findViewById ( R.id.order_details_layout );
        order_Date = orderDetailsLayout.findViewById ( R.id.orderdate );
        order_Id = orderDetailsLayout.findViewById ( R.id.bill_num );
        order_Total = orderDetailsLayout.findViewById ( R.id.order_total );
        order_status = orderDetailsLayout.findViewById ( R.id.order_status );
        orderTimeTxt = orderDetailsLayout.findViewById ( R.id.ordertimetxt );
        CGSTtextview = orderDetailsLayout.findViewById ( R.id.CGSTtextview );
        SGSTtextview = orderDetailsLayout.findViewById ( R.id.SGSTtextview );
        totaltaxtextview = orderDetailsLayout.findViewById ( R.id.toaltaxtextview );

        itemDetailsLayout = findViewById ( R.id.itemdetailslayout );
        //ItemDetails
        listView = itemDetailsLayout.findViewById ( R.id.itemDetailslist );
        itemHeaderlayout = findViewById ( R.id.itemdetailslayoutheader );

        //Payment details
        paymentDetailsLayout = findViewById ( R.id.payment_details );
        type_Of_Payment = paymentDetailsLayout.findViewById ( R.id.type_of_payment );


        //ItemHeaderlayout
        storeNameText = itemHeaderlayout.findViewById ( R.id.storeName );

        orderIdFromHistory = getIntent ().getStringExtra ( "billidnum" );
        orderdetailRef = CommonMethods.fetchFirebaseDatabaseReference("OrderDetails");
        StorageRef = CommonMethods.fetchFirebaseStorageReference("Images").child("Trackingimages");

        //Shipping Address Details
        shippingAddressLayout = findViewById ( R.id.shipping_details_layout );
        fullName = shippingAddressLayout.findViewById ( R.id.full_name );
        shipping_Address = shippingAddressLayout.findViewById ( R.id.address );
        customerPhoneNumber = shippingAddressLayout.findViewById ( R.id.phoneNumber );

        //Order summary
        orderSummaryLayout = findViewById ( R.id.cart_total_amount_layout );
        amount = orderSummaryLayout.findViewById ( R.id.tips_price1 );
        shipping = orderSummaryLayout.findViewById ( R.id.tips_price );
        wholeCharge = orderSummaryLayout.findViewById ( R.id.total_price );

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();



        if (!"".equals(SellerProfileActivity.saved_id) && SellerProfileActivity.saved_id != null && !"".equals(SellerProfileActivity.saved_customerPhonenumber) || SellerProfileActivity.saved_customerPhonenumber != null && !"".equals(SellerProfileActivity.storeImage) && SellerProfileActivity.storeImage != null) {
            sellerIdIntent = SellerProfileActivity.saved_id;
            storeNameIntent = SellerProfileActivity.storeName;
            storeImageIntent = SellerProfileActivity.storeImage;
        }

        backButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( OrderDetailsActivity.this, OrderHistoryActivity.class );
                intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity ( intent );

            }
        } );



        changeStatusToReadyForPickup.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(OrderDetailsActivity.this);

                View mView = getLayoutInflater().inflate(R.layout.status_change_popup, null);

                ImageView cancel = mView.findViewById(R.id.cancelpop);

                TextView txtshipped = mView.findViewById(R.id.shipped);
                EditText courierPartnerNameEdt = mView.findViewById(R.id.courierPartnerName);
                EditText trackingId = mView.findViewById(R.id.trackingId);
                RelativeLayout trackingidRelativeLayout = mView.findViewById(R.id.trackingIdLayout);
                RelativeLayout courierPartnerRelativeLayout = mView.findViewById(R.id.courierPartnerNameLayout);
                RelativeLayout trackingLayout = mView.findViewById(R.id.trackingLayout);
                chooseTrackImage = mView.findViewById(R.id.choose_track_Image);
                trackImage = mView.findViewById(R.id.trackImage);


                final EditText editText = mView.findViewById(R.id.edittext);
                Button ok = mView.findViewById(R.id.buttonok);


                mBuilder.setView(mView);
                dialog = mBuilder.create();
                if (!((Activity) OrderDetailsActivity.this).isFinishing()) {
                    dialog.show();
                }
                dialog.setCancelable(false);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        changeStatusToReadyForPickup.setChecked(false);
                    }
                });

                chooseTrackImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFileChooser();
                        startActivityForResult(intentImage, 100);
                    }
                });


                if (!changeStatusToReadyForPickup.isChecked ()) {
                    dialog.dismiss ();
                }


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (txtshipped.getText().toString().equals("Shipped")) {

                            if (courierPartnerNameEdt.getText().toString().trim().equals("") || courierPartnerNameEdt.getText().toString().trim().isEmpty()) {
                                courierPartnerNameEdt.setError(Constant.REQUIRED);
                                return;
                            } else if (courierPartnerNameEdt.getText().toString().trim().length() < 3) {
                                courierPartnerNameEdt.setError("Minimum 3 characters required");
                                return;
                            } else if (!(0 <= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnoqrstuvwxyz".indexOf(courierPartnerNameEdt.getText().toString().trim().charAt(0)))) {
                                courierPartnerNameEdt.setError("Courirer Partner Name Must be starts with Alphabets");
                                return;
                            } else if (trackingId.getText().toString().equals("") || trackingId.getText().toString().isEmpty()) {
                                trackingId.setError(Constant.REQUIRED);
                                return;
                            } else if (trackingId.getText().toString().trim().length() < 3) {
                                trackingId.setError("Minimum 3 characters required");
                                return;
                            }else if (mimageuri == null){
                                Toast.makeText(OrderDetailsActivity.this, "Please add tracking image", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else {
                                changeStatusToReadyForPickup.setChecked ( true );

                                temp = 0;
                                orderdetailRef.child( orderIdFromHistory).child("itemDetailList").addListenerForSingleValueEvent ( new ValueEventListener () {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (temp == 0) {
                                            if (dataSnapshot.getChildrenCount () > 0) {
                                                for ( DataSnapshot orderDetailsSnap : dataSnapshot.getChildren () ) {

                                                     itemDetails = orderDetailsSnap.getValue(ItemDetails.class);
                                                     if (temp == 0) {
                                                        if ("Order Placed".equalsIgnoreCase(itemDetails.getOrderStatus())) {
                                                            statusOrderplaced(itemDetails.getOrderStatus(), courierPartnerNameEdt.getText().toString().trim(), trackingId.getText().toString().trim(),mimageuri,dialog);
                                                            temp = temp + 1;
                                                            break;
                                                        }
                                                   }

                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                                });
                            }
                        }
                    }
                });
            }
        } );
        changeStatusToDeliveryOnTheWay.setOnCheckedChangeListener ( new CompoundButton.OnCheckedChangeListener () {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( OrderDetailsActivity.this );
                bottomSheetDialog.setContentView ( R.layout.confirmation_order_change );
                Button no = bottomSheetDialog.findViewById ( R.id.statuschangenegative );
                Button yes = bottomSheetDialog.findViewById ( R.id.statuschangepositive );

                bottomSheetDialog.show ();
                bottomSheetDialog.setCancelable ( false );

                no.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss ();
                        changeStatusToDeliveryOnTheWay.setChecked ( false );
                        bottomSheetDialog.dismiss ();
                    }
                } );

                if (!changeStatusToDeliveryOnTheWay.isChecked ()) {
                    bottomSheetDialog.dismiss ();
                }

                yes.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {

                        changeStatusToDeliveryOnTheWay.setChecked ( true );


                        temp = 0;
                        orderdetailRef.child ( String.valueOf ( orderIdFromHistory ) ).child("itemDetailList").addValueEventListener ( new ValueEventListener () {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (temp == 0) {
                                    if (dataSnapshot.getChildrenCount () > 0) {

                                        for ( DataSnapshot orderDetailsSnap : dataSnapshot.getChildren () ) {
                                                 ItemDetails itemDetails = orderDetailsSnap.getValue(ItemDetails.class);
                                            if (itemDetails.getStoreName().equals(storeNameIntent)) {
                                                changeStatusToReadyForPickupLayout.setVisibility(View.INVISIBLE);

                                                if (temp == 0) {
                                                    if ("Your Order is Shipped".equalsIgnoreCase(itemDetails.getOrderStatus())) {
                                                        statusPickup((itemDetails.getOrderStatus()),bottomSheetDialog);
                                                        temp = temp + 1;
                                                        break;

                                                    }
                                                }
                                                changeStatusToDeliveryOnTheWayLayout.setVisibility(View.INVISIBLE);
                                                bottomSheetDialog.dismiss();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        } );
                    }
                } );
            }
        });

        loadParticularStoredetails();


    }
    public void loadParticularStoredetails(){


        orderdetailRef.child (orderIdFromHistory).child("itemDetailList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (temp == 0) {
                    if (snapshot.getChildrenCount () > 0) {

                        for ( DataSnapshot orderDetailsSnap : snapshot.getChildren () ){
                        //    orderDetails = orderDetailsSnap.getValue ( OrderDetails.class );
                       ItemDetails itemDetails = orderDetailsSnap.getValue(ItemDetails.class);

                            if (itemDetails.getStoreName().equals(storeNameIntent)) {

                                if (itemDetails.getOrderStatus().equalsIgnoreCase("Order Placed")) {
                                    changeStatusToDeliveryOnTheWayLayout.setVisibility(View.INVISIBLE);
                                    changeStatusToReadyForPickupLayout.setVisibility(View.VISIBLE);
                                } else if (itemDetails.getOrderStatus().equalsIgnoreCase("Your Order is Shipped")) {
                                    changeStatusToDeliveryOnTheWayLayout.setVisibility(View.VISIBLE);
                                    changeStatusToReadyForPickupLayout.setVisibility(View.INVISIBLE);
                                } else if (itemDetails.getOrderStatus().equalsIgnoreCase("Delivered")) {
                                    changeStatusToDeliveryOnTheWayLayout.setVisibility(View.INVISIBLE);
                                    changeStatusToReadyForPickupLayout.setVisibility(View.INVISIBLE);
                                }
                            }


                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Query storebasedQuery = orderdetailRef.orderByChild ( "orderId" ).equalTo ( String.valueOf ( orderIdFromHistory ) );

        storebasedQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                billDetailsArrayList.clear ();
                if (check ) {
                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                        orderDetails = dataSnapshot1.getValue(OrderDetails.class);
                        billDetailsArrayList.add(orderDetails);
                    }
                    String paymenttype = orderDetails.getPaymentType ();

                     String paymentType = paymenttype.substring(0, 1).toUpperCase() + paymenttype.substring(1);
                    type_Of_Payment.setText ( paymentType );
                    fullName.setText ( " " + orderDetails.getFullName () );
                    shipping_Address.setText ( " " + orderDetails.getShippingaddress () );
                    customerPhoneNumber.setText ( " " + orderDetails.getCustomerPhoneNumber () );
                    fullName.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_cus, 0, 0, 0 );
                    shipping_Address.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_location_01, 0, 0, 0 );
                    customerPhoneNumber.setCompoundDrawablesWithIntrinsicBounds ( R.drawable.ic_phonenumicon_01, 0, 0, 0 );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        orderdetailRef.child (orderIdFromHistory).child("itemDetailList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemDetailsList.clear ();
                if (check ) {
                    for ( DataSnapshot itemsnap : snapshot.getChildren () ){
                        ItemDetails itemDetails = itemsnap.getValue(ItemDetails.class);

                        if (itemDetails.getStoreName().equals(storeNameIntent)) {
                            newitemDetailsList.add(itemDetails);
                            total = total + itemDetails.getTotalItemQtyPrice();
                            orderstatus = itemDetails.getOrderStatus();
                            taxamount = taxamount + itemDetails.getTotalTaxPrice();


                        }
                    }
                    TextUtils.removeDuplicatesList(newitemDetailsList);

                }
                cgst = taxamount /2;


                order_Total.setText ( " ₹" + String.valueOf (total + shippingamount ) );
                CGSTtextview.setText(" ₹" + String.valueOf (cgst )  );
                SGSTtextview.setText(" ₹" + String.valueOf (cgst ) );
                totaltaxtextview.setText(" ₹" + String.valueOf (taxamount ) );

                order_Date.setText ( orderDetails.getPaymentDate() );
                order_Id.setText ( orderDetails.getOrderNumberForFinancialYearCalculation () );

              //    order_Total.setText ( " ₹" + String.valueOf (total ) );
             //   order_Total.setText ( " ₹" + String.valueOf ( orderDetails.getPaymentamount() ) );
                order_status.setText ( orderstatus );
                storeNameText.setText ( storeNameIntent );
                orderTimeTxt.setText ( orderDetails.getOrderTime () );


                amount.setText ( " ₹" + String.valueOf ( total ) );
                shipping.setText ( " ₹" + String.valueOf (shippingamount) );
                wholeCharge.setText ( " ₹" + String.valueOf (total +shippingamount) );


                itemOrderDetails = new ItemOrderDetails ( OrderDetailsActivity.this, newitemDetailsList);
                listView.setAdapter ( itemOrderDetails );
                itemOrderDetails.notifyDataSetChanged ();

                if (listView != null) {
                    int totalHeight = 0;

                    for ( int i = 0; i < itemOrderDetails.getCount (); i++ ) {
                        View listItem = itemOrderDetails.getView ( i, null, listView );
                        listItem.measure ( 0, 0 );
                        totalHeight += listItem.getMeasuredHeight ();
                    }
                    ViewGroup.LayoutParams params = listView.getLayoutParams ();
                    params.height = totalHeight + (listView.getDividerHeight () * (listView.getCount () - 1));
                    listView.setLayoutParams ( params );
                    listView.requestLayout ();
                    listView.setAdapter ( itemOrderDetails );
                    itemOrderDetails.notifyDataSetChanged ();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }); ;
    }

    public void statusPickup(String pickupDtatus ,BottomSheetDialog bottomSheetDialog) {
        ArrayList<String> deliverystatusarray = new ArrayList<>();
        if (pickupDtatus.equals ( "Your Order is Shipped" )) {

            DatabaseReference refdeliverted = CommonMethods.fetchFirebaseDatabaseReference ( "OrderDetails" ).child ( String.valueOf ( orderIdFromHistory ) ).child("itemDetailList");
            refdeliverted.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        deliverystatusarray.clear();
                        for (DataSnapshot updatedeliverysnap : snapshot.getChildren()){
                            ItemDetails itemDetails = updatedeliverysnap.getValue(ItemDetails.class);

                            if (storeNameIntent.equals(itemDetails.getStoreName())){
                                String key = updatedeliverysnap.getKey();

                                deliverystatusarray.add(key);

                            }
                        }

                        for (int i=0 ;i< deliverystatusarray.size();i++){
                            DatabaseReference changeref = CommonMethods.fetchFirebaseDatabaseReference ( "OrderDetails" ).child ( String.valueOf ( orderIdFromHistory ) ).child("itemDetailList").child(deliverystatusarray.get(i));
                            changeref.child ( "orderStatus" ).setValue ( "Delivered" );
                            changeref.child("deliveryDate").setValue(DateUtils.fetchCurrentDateAndTime());

                            if (deliverystatusarray.size()-1 == i) {
                                deliverydialogcancelfun(progressdialog);
                                statusfunction(orderIdFromHistory);
                            }


                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            progressdialog = new ProgressDialog(OrderDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
            progressdialog.setMessage("Loading");
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.show();

            order_status.setText("Delivered");
            changeStatusToDeliveryOnTheWayLayout.setVisibility(View.INVISIBLE);
            changeStatusToReadyForPickupLayout.setVisibility(View.INVISIBLE);
            bottomSheetDialog.dismiss();
        }
    }

    public void statusOrderplaced(String status, String courieredt, String trackingid, Uri imageuri, AlertDialog dialog) {

        ArrayList<String> newarraykey = new ArrayList<>();

        if (status.equals ( "Order Placed" )) {

            DatabaseReference ref = CommonMethods.fetchFirebaseDatabaseReference ( "OrderDetails" ).child ( String.valueOf ( orderIdFromHistory ) ).child("itemDetailList");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    newarraykey.clear();

                        if (snapshot.exists()) {
                            for (DataSnapshot updatedeliverysnap : snapshot.getChildren()) {
                                ItemDetails itemDetails = updatedeliverysnap.getValue(ItemDetails.class);

                                if (storeNameIntent.equals(itemDetails.getStoreName())) {
                                    String key = updatedeliverysnap.getKey();
                                    Log.e("KeySnap", key);

                                    newarraykey.add(key);

                                    if (imageuri != null) {
                                        imageFileStorageRef = StorageRef.child("Image" + System.currentTimeMillis() + "." + getExtenstion(imageuri));

                                        Bitmap bitmap = null;
                                        try {
                                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageuri);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                        byte[] data = baos.toByteArray();

                                        profile_StorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!urlTask.isSuccessful()) ;
                                                profile_DownloadUrl = urlTask.getResult();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });

                                    }

                                }
                            }
                         new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i < newarraykey.size(); i++) {
                                        Log.e("newarraykey.size()", "one");
                                        DatabaseReference changeref = CommonMethods.fetchFirebaseDatabaseReference("OrderDetails").child(String.valueOf(orderIdFromHistory)).child("itemDetailList").child(newarraykey.get(i));
                                        changeref.child("orderStatus").setValue("Your Order is Shipped");
                                        changeref.child("courierName").setValue(courieredt);
                                        changeref.child("trackingId").setValue(trackingid);
                                        changeref.child("deliveryby").setValue("Seller");
                                        changeref.child("trackingimage").setValue(profile_DownloadUrl.toString());

                                       if (newarraykey.size()-1 == i) {
                                           deliverydialogcancelfun(progressdialog);
                                       }


                                    }
                                }
                            }, 2000);

                        }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
               progressdialog = new ProgressDialog(OrderDetailsActivity.this, R.style.AppCompatAlertDialogStyle);
               progressdialog.setMessage("Loading");
               progressdialog.setCanceledOnTouchOutside(false);
               progressdialog.show();

            order_status.setText("Your order is Shipped");
            changeStatusToDeliveryOnTheWayLayout.setVisibility(View.VISIBLE);
            changeStatusToReadyForPickupLayout.setVisibility(View.INVISIBLE);
            dialog.dismiss();


        }
    }


    public void deliverydialogcancelfun(ProgressDialog progressdialog){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
           progressdialog.dismiss();
            }
        }, 2000);
    }

    private String getExtenstion(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void openFileChooser() {
        intentImage = new Intent();
        intentImage.setType("image/*");
        intentImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intentImage.setAction(Intent.ACTION_GET_CONTENT);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mimageuri = data.getData();
            Glide.with(OrderDetailsActivity.this).load(mimageuri).into(trackImage);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent ( OrderDetailsActivity.this, OrderHistoryActivity.class );
        intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity ( intent );
    }

    public void statusfunction(String orderId){

        orderdetailRef.child(orderId).child("itemDetailList").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()){
                long count = snapshot.getChildrenCount();
                deliverycount = 0;

                for (DataSnapshot itemsnap : snapshot.getChildren()) {

                    ItemDetails itemDetails = itemsnap.getValue(ItemDetails.class);

                    if (itemDetails.getOrderStatus().equals("Delivered")) {
                        deliverycount = deliverycount + 1;
                    }

                }


                if (deliverycount == count) {
                    orderdetailRef.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            OrderDetails orderDetails = dataSnapshot.getValue(OrderDetails.class);

                            if (!orderDetails.getOrderStatus().equals("Completed")) {
                                orderdetailRef.child(orderId).child("orderStatus").setValue("Completed");
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });

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
        if (!((Activity) OrderDetailsActivity.this).isFinishing()) {
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