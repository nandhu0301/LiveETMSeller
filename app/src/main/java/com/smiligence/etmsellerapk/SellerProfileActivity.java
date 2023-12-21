package com.smiligence.etmsellerapk;

import static com.smiligence.etmsellerapk.Common.CommonMethods.screenPermissions;
import static com.smiligence.etmsellerapk.Common.Constant.SELLERLOGIN_DETAILS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.smiligence.etmsellerapk.Common.CommonMethods;
import com.smiligence.etmsellerapk.Common.Constant;
import com.smiligence.etmsellerapk.Common.MultiSelectionSpinner;
import com.smiligence.etmsellerapk.Common.StoreTimings;
import com.smiligence.etmsellerapk.Common.TextUtils;
import com.smiligence.etmsellerapk.bean.CategoryDetails;
import com.smiligence.etmsellerapk.bean.ItemDetails;
import com.smiligence.etmsellerapk.bean.OrderDetails;
import com.smiligence.etmsellerapk.bean.UserDetails;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SellerProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String saved_customerPhonenumber, saved_id;
    DatabaseReference sellerDataRef, categoryDataRef, newsellerDataRef,storeTimingMaintenanceDataRef, orderdetailRef;
    EditText firstNameEdt, storeAddressEdt, lastnameEdt, email_IdEDt, phoneNumberEdt, pincodeEdt, bankNameEdt, branchNameEdt, accountnumber, ifscCodeEdt, businessNameEdt, businessTypeEdt,  aadhaNumberEdt, gstNumberEdt;
    Button storeProfileButton, aadharIdbutton, fssaicertificateButton, uploadKycButton;
    OrderDetails orderDetails;
    ArrayList<ItemDetails> itemDetailsArrayList = new ArrayList<>();
    int resultTotalAmount = 0;
    final ArrayList<String> orderListSize = new ArrayList<String>();
    ArrayList<ItemDetails> itemDetails = new ArrayList<>();
    Intent intent;
    Ringtone r;
    Uri storeImageUri, aadharIdUri, gstCertificateUri;
    ImageView storeImageView, aadharimageView, gstImageView;
    SweetAlertDialog pDialog;
    StorageReference storageReference;
    Uri downloadUrl1, downloadUrl2, downloadUrl3;
    Uri downloadUrl1res, downloadUrl2res, downloadUrl3res;
    StorageTask storeImageTask, aadharImageTask, gstTask;
    MultiSelectionSpinner multiSelectionSpinner;
    public static Menu menuNav;
    private List<CategoryDetails> categoryDetailsArrayList = new ArrayList<>();
    public static String approvedStatus = "";
    TextView statusTxt, suggestion, selectedSpinnerData;
    private UserDetails userDetails = new UserDetails();
    boolean checkUser = false;
    StoreTimings storeTimings = new StoreTimings();
    public static TextView textViewUsername;
    public static ImageView imageView;
    public static View mHeaderView;
    public static String storeName, storeImage, storePincode;
    NavigationView navigationView;
    private Handler handler = new Handler();
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private final static String default_notification_channel_id = "default";
    String sellerIdIntent, storeImageIntent, storeNameIntent;
    final ArrayList<String> list = new ArrayList<String>();
    String startDateMon, endDateSunday;
    private ArrayList<CategoryDetails> catagoryList = new ArrayList<>();
    ArrayList<String> storenamelist = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        disableAutofill();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(Constant.STORE_PROFILE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(SellerProfileActivity.this);
        drawer.setDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.sellerProfile);
        textViewUsername = (TextView) mHeaderView.findViewById(R.id.name);
        imageView = (ImageView) mHeaderView.findViewById(R.id.imageViewheader);
        final SharedPreferences loginSharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
        saved_customerPhonenumber = loginSharedPreferences.getString("sellerPhoneNumber", "");
        saved_id = loginSharedPreferences.getString("sellerId", "");
        storeAddressEdt = findViewById(R.id.Edittextstoreaddressseller);
        firstNameEdt = findViewById(R.id.Edittextfirstnameseller);
        lastnameEdt = findViewById(R.id.Edittextlastnameseller);
        email_IdEDt = findViewById(R.id.Edittextemailidseller);
        phoneNumberEdt = findViewById(R.id.Edittextphonenumberseller);
        pincodeEdt = findViewById(R.id.Edittextstorepincodeseller);
        bankNameEdt = findViewById(R.id.Edittextbanknameeseller);
        branchNameEdt = findViewById(R.id.Edittextbranchnameeseller);
        accountnumber = findViewById(R.id.Edittextaccountnumberseller);
        ifscCodeEdt = findViewById(R.id.Edittextifsccodeseller);
        businessNameEdt = findViewById(R.id.Edittextbusinessnameseller);
        businessTypeEdt = findViewById(R.id.Edittextbusinesstypeseller);
        aadhaNumberEdt = findViewById(R.id.Edittextaadharnumberseller);
        gstNumberEdt = findViewById(R.id.Edittextgstseller);
        multiSelectionSpinner = findViewById(R.id.categorySpinner);
        multiSelectionSpinner.setPrompt("Select");
        statusTxt = findViewById(R.id.statusTxt);
        suggestion = findViewById(R.id.Suggestion);
        selectedSpinnerData = findViewById(R.id.selectedSpinnerData);
        storeProfileButton = findViewById(R.id.chhosestoreprofile);
        aadharIdbutton = findViewById(R.id.aadhariDProof);
        fssaicertificateButton = findViewById(R.id.fssainumberButton);
        uploadKycButton = findViewById(R.id.uploaddeliveryboydetails);

        storeImageView = findViewById(R.id.choosestoreprofileImageview);
        aadharimageView = findViewById(R.id.aadhariDProofImageview);
        gstImageView = findViewById(R.id.fssainumberImageview);



        if (!"".equals(SellerProfileActivity.saved_id) && SellerProfileActivity.saved_id != null && !"".equals(SellerProfileActivity.saved_customerPhonenumber) || SellerProfileActivity.saved_customerPhonenumber != null && !"".equals(SellerProfileActivity.storeImage) && SellerProfileActivity.storeImage != null) {
            sellerIdIntent = SellerProfileActivity.saved_id;
            storeNameIntent = SellerProfileActivity.storeName;
            storeImageIntent = SellerProfileActivity.storeImage;
        } else if (!"".equals(DashBoardActivity.saved_id) && DashBoardActivity.saved_id != null && !"".equals(DashBoardActivity.saved_customerPhonenumber) || DashBoardActivity.saved_customerPhonenumber != null && !"".equals(DashBoardActivity.storeImage) && DashBoardActivity.storeImage != null) {
            sellerIdIntent = DashBoardActivity.saved_id;
            storeNameIntent = DashBoardActivity.storeName;
            storeImageIntent = DashBoardActivity.storeImage;
        }

        storageReference = CommonMethods.fetchFirebaseStorageReference(SELLERLOGIN_DETAILS);

        sellerDataRef = CommonMethods.fetchFirebaseDatabaseReference(SELLERLOGIN_DETAILS).child(String.valueOf(saved_id));
        newsellerDataRef = CommonMethods.fetchFirebaseDatabaseReference("SellerLoginDetails");
        categoryDataRef =CommonMethods.fetchFirebaseDatabaseReference("Category");

        phoneNumberEdt.setText(saved_customerPhonenumber);
        storeTimingMaintenanceDataRef =CommonMethods.fetchFirebaseDatabaseReference("storeTimingMaintenance");


        autoLoadFunction(saved_customerPhonenumber);


        storeProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
                startActivityForResult(intent, 0);
            }
        });
        aadharIdbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
                startActivityForResult(intent, 1);
            }
        });
        fssaicertificateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
                startActivityForResult(intent, 2);
            }
        });



        categoryDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                catagoryList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    CategoryDetails categoryDetails = postSnapshot.getValue(CategoryDetails.class);
                    catagoryList.add(categoryDetails);

                }

                multiSelectionSpinner.setItems(catagoryList, selectedSpinnerData);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        uploadKycButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String storeAddressstring = storeAddressEdt.getText().toString().trim();
                String sellerName = firstNameEdt.getText().toString().trim();
                String sellerLastName = lastnameEdt.getText().toString().trim();
                String emailstring = email_IdEDt.getText().toString().trim();
                String pincode = pincodeEdt.getText().toString().trim();
                String bankstring = bankNameEdt.getText().toString().trim();
                String branchString = branchNameEdt.getText().toString().trim();
                String accountNumberString = accountnumber.getText().toString().trim();
                String IFSCString = ifscCodeEdt.getText().toString().trim();
                String businessNameString = businessNameEdt.getText().toString().trim();
                String businessTypeString = businessTypeEdt.getText().toString().trim();
                String AadharNumberString = aadhaNumberEdt.getText().toString().trim();
                String GstNumberString = gstNumberEdt.getText().toString().trim();


                if (approvedStatus != null && !approvedStatus.equals("")) {
                    if (approvedStatus.equals("Rejected")) {

                        if ("".equals(storeAddressstring)) {
                            storeAddressEdt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "StoreAddress Required", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (!"".equalsIgnoreCase(storeAddressstring) && !(storeAddressstring.length() >= 5)) {
                            storeAddressEdt.setError("Storeaddress name should have minimum 5 characters");
                            Toast.makeText(SellerProfileActivity.this, "Storeaddress name should have minimum 5 characters", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if (multiSelectionSpinner.getSelectedItems().size() == 0 && selectedSpinnerData.getText().toString().equals("")) {
                            Toast.makeText(SellerProfileActivity.this, "Please select Category", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if ("".equals(sellerName)) {
                            firstNameEdt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "First Name Required", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!"".equalsIgnoreCase(sellerName) && !(sellerName.length() >= 3)) {
                            firstNameEdt.setError("seller name should have minimum 3 characters");
                            Toast.makeText(SellerProfileActivity.this, "First Name should have minimum 3 characters", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!"".equalsIgnoreCase(sellerName) && !TextUtils.validateCustomerName(sellerName)) {
                            firstNameEdt.setError("Enter Valid Name");
                            Toast.makeText(SellerProfileActivity.this, "Enter Valid First Name", Toast.LENGTH_SHORT).show();
                            return;
                        } else if ("".equals(sellerLastName)) {
                            lastnameEdt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "Last Name Required", Toast.LENGTH_SHORT).show();

                            return;
                        } else if (!"".equalsIgnoreCase(sellerLastName) && !TextUtils.validateCustomerName(sellerLastName)) {
                            lastnameEdt.setError("Enter Valid Name");
                            Toast.makeText(SellerProfileActivity.this, "Enter Valid Last Name", Toast.LENGTH_SHORT).show();
                            return;
                        } else if ("".equals(emailstring)) {
                            email_IdEDt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "Enter email id", Toast.LENGTH_SHORT).show();

                            return;
                        } else if (!"".equalsIgnoreCase(emailstring) && !TextUtils.isValidEmail(emailstring)) {
                            email_IdEDt.setError("Enter Valid Email");
                            Toast.makeText(SellerProfileActivity.this, "Enter Valid Email id", Toast.LENGTH_SHORT).show();
                            return;
                        } else if ("".equals(pincode)) {
                            pincodeEdt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "Enter pincode Required ", Toast.LENGTH_SHORT).show();

                            return;
                        }
                        else if (!"".equalsIgnoreCase(pincode) && !(pincode.length() >= 6)) {
                            pincodeEdt.setError("Pincode must be 6 numbers");
                            Toast.makeText(SellerProfileActivity.this, "Pincode must be 6 numbers ", Toast.LENGTH_SHORT).show();

                            return;
                        }


                        else if ("".equals(bankstring)) {
                            bankNameEdt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "Bank Name Required ", Toast.LENGTH_SHORT).show();

                            return;
                        } else if (!"".equalsIgnoreCase(bankstring) && !(bankstring.length() >= 3)) {
                            bankNameEdt.setError("Bank name  requires minimum 3 characters");
                            Toast.makeText(SellerProfileActivity.this, "Bank name  requires minimum 3 characters", Toast.LENGTH_SHORT).show();

                            return;
                        } else if ("".equals(branchString)) {
                            branchNameEdt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "Branch Name Required", Toast.LENGTH_SHORT).show();

                            return;
                        } else if (!"".equalsIgnoreCase(branchString) && !(branchString.length() >= 3)) {
                            branchNameEdt.setError("Branch Name  requires minimum 3 characters");
                            Toast.makeText(SellerProfileActivity.this, "Branch Name  requires minimum 3 characters", Toast.LENGTH_SHORT).show();

                            return;
                        } else if ("".equals(accountNumberString)) {
                            accountnumber.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "Account Number Required ", Toast.LENGTH_SHORT).show();

                            return;
                        } else if (!"".equalsIgnoreCase(accountNumberString) &&
                                !(accountNumberString.length() >= 12) || !(accountNumberString.length() <= 15)) {
                            accountnumber.setError("Enter Valid Account Number");
                            Toast.makeText(SellerProfileActivity.this, "Enter Valid Account Number ", Toast.LENGTH_SHORT).show();
                            return;
                        } else if ("".equals(IFSCString)) {
                            ifscCodeEdt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "Ifsc code Required ", Toast.LENGTH_SHORT).show();

                            return;
                        } else if (!"".equalsIgnoreCase(IFSCString) && !TextUtils.validIFSCCode(IFSCString)) {
                            ifscCodeEdt.setError("Enter Valid IFSC Code");
                            Toast.makeText(SellerProfileActivity.this, "Enter Valid IFSC Code", Toast.LENGTH_SHORT).show();

                            return;
                        } else if ("".equals(businessNameString)) {
                            businessNameEdt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "Store Name Required ", Toast.LENGTH_SHORT).show();

                            return;
                        }
                        else if (businessNameString.length() <3) {
                            businessNameEdt.setError("Store Name must be greater than 3 character");
                            Toast.makeText(SellerProfileActivity.this, "Store Name must be greater than 3 character", Toast.LENGTH_SHORT).show();

                            return;
                        }


                        else if ("".equals(businessTypeString)) {
                            businessTypeEdt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "Business type Required ", Toast.LENGTH_SHORT).show();

                            return;
                        } else if (businessTypeString.length() <3) {
                            businessTypeEdt.setError("Business type must be greater than 3 character");
                            Toast.makeText(SellerProfileActivity.this, "Business type must be greater than 3 character ", Toast.LENGTH_SHORT).show();

                            return;
                        }


                        else if ("".equals(AadharNumberString)) {
                            aadhaNumberEdt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "Aadhar Number Required ", Toast.LENGTH_SHORT).show();

                            return;
                        } else if (!(AadharNumberString.length() == 12)) {
                            aadhaNumberEdt.setError("Invalid Aadhar Number");
                            Toast.makeText(SellerProfileActivity.this, "Invalid Aadhar Number ", Toast.LENGTH_SHORT).show();

                            return;
                        } else if ("".equals(GstNumberString)) {
                            gstNumberEdt.setError("Required");
                            Toast.makeText(SellerProfileActivity.this, "Gst Number Required ", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!"".equalsIgnoreCase(GstNumberString) && !TextUtils.validate_GSTNumber(GstNumberString)) {
                            gstNumberEdt.setError("Invalid GST Number");
                            Toast.makeText(SellerProfileActivity.this, "Invalid GST Number ", Toast.LENGTH_SHORT).show();

                            return;
                        } else if(!checkUser) {
                            if (storenamelist.size() > 0) {
                                for (int i = 0; i < storenamelist.size(); i++) {
                                    if (storenamelist.get(i).equals(businessNameString)) {
                                        checkUser = false;
                                        Toast.makeText(SellerProfileActivity.this, "Store Name is already exist", Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                    else {
                                        checkUser = true;
                                    }
                                }
                            }
                            else {
                                checkUser = true;
                            }
                        }


                        else if (storeImageUri != null && !storeImageUri.equals("") && aadharIdUri != null && !aadharIdUri.equals("") &&
                                gstCertificateUri != null && !gstCertificateUri.equals("")) {

                            pDialog = new SweetAlertDialog(SellerProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#150055"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();



                            StorageReference fileRef = storageReference.child("Uploads/" + System.currentTimeMillis());
                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), storeImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();

                            storeImageTask = fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl1 = urlTask.getResult();

                                    StorageReference fileRef1 = storageReference.child("Uploads/" + System.currentTimeMillis());

                                    Bitmap bmp = null;
                                    try {
                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharIdUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                    byte[] data = baos.toByteArray();

                                    aadharImageTask = fileRef1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            downloadUrl2 = urlTask.getResult();

                                            final StorageReference fileRef2 = storageReference.child("Uploads/" + System.currentTimeMillis());

                                            Bitmap bmp = null;
                                            try {
                                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), gstCertificateUri);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                            byte[] data = baos.toByteArray();

                                            gstTask = fileRef2.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                    while (!urlTask.isSuccessful()) ;
                                                    downloadUrl3 = urlTask.getResult();
                                                    if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                        sellerDataRef.child("storeLogo").setValue(downloadUrl1.toString());
                                                        sellerDataRef.child("gstCertificateImage").setValue(downloadUrl3.toString());
                                                        sellerDataRef.child("aadharImage").setValue(downloadUrl2.toString());
                                                    }


                                                    if (downloadUrl3 != null) {
                                                        if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                            loadDataFunction( sellerDataRef);
                                                        }
                                                        if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                            pDialog.dismiss();
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        } else if (storeImageUri != null && !storeImageUri.equals("") && downloadUrl2res != null && !downloadUrl2res.equals("") &&
                                downloadUrl3res != null && !downloadUrl3res.equals("")) {


                            pDialog = new SweetAlertDialog(SellerProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#150055"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();



                            StorageReference fileRef = storageReference.child("Uploads/" + System.currentTimeMillis());
                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), storeImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();

                            storeImageTask = fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl1 = urlTask.getResult();

                                    if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                        sellerDataRef.child("storeLogo").setValue(downloadUrl1.toString());
                                        sellerDataRef.child("gstCertificateImage").setValue(downloadUrl3res.toString());
                                        sellerDataRef.child("aadharImage").setValue(downloadUrl2res.toString());
                                    }


                                    if (downloadUrl1 != null) {
                                        if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                            loadDataFunction(sellerDataRef);
                                        }
                                        if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                            pDialog.dismiss();
                                        }
                                    }
                                }
                            });

                        } else if (downloadUrl1res != null && !downloadUrl1res.equals("") && aadharIdUri != null && !aadharIdUri.equals("") &&
                                downloadUrl3res != null && !downloadUrl3res.equals("")) {

                            pDialog = new SweetAlertDialog(SellerProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#150055"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();




                            StorageReference fileRef1 = storageReference.child("Uploads/" + System.currentTimeMillis());

                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharIdUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();

                            aadharImageTask = fileRef1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl2 = urlTask.getResult();


                                    if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                        sellerDataRef.child("storeLogo").setValue(downloadUrl1res.toString());
                                        sellerDataRef.child("gstCertificateImage").setValue(downloadUrl3res.toString());
                                        sellerDataRef.child("aadharImage").setValue(downloadUrl2.toString());
                                    }


                                    if (downloadUrl2 != null) {
                                        if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                            loadDataFunction(sellerDataRef);
                                        }
                                        if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                            pDialog.dismiss();
                                        }
                                    }
                                }
                            });

                        } else if (downloadUrl1res != null && !downloadUrl1res.equals("") && downloadUrl2res != null && !downloadUrl2res.equals("") &&
                                gstCertificateUri != null && !gstCertificateUri.equals("")) {

                            pDialog = new SweetAlertDialog(SellerProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#150055"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();




                            final StorageReference fileRef2 = storageReference.child("Uploads/" + System.currentTimeMillis());

                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), gstCertificateUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();

                            gstTask = fileRef2.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl3 = urlTask.getResult();
                                    if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                        sellerDataRef.child("storeLogo").setValue(downloadUrl1res.toString());
                                        sellerDataRef.child("gstCertificateImage").setValue(downloadUrl3.toString());
                                        sellerDataRef.child("aadharImage").setValue(downloadUrl2res.toString());
                                    }
                                    if (downloadUrl3 != null) {
                                        if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                            loadDataFunction(sellerDataRef);
                                        }
                                        if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                            pDialog.dismiss();
                                        }
                                    }
                                }
                            });
                        } else if (downloadUrl1res != null && !downloadUrl1res.equals("") && aadharIdUri != null && !aadharIdUri.equals("") &&
                                gstCertificateUri != null && !gstCertificateUri.equals("")) {
                            pDialog = new SweetAlertDialog(SellerProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#150055"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();



                            StorageReference fileRef1 = storageReference.child("Uploads/" + System.currentTimeMillis());

                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharIdUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();

                            aadharImageTask = fileRef1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl2 = urlTask.getResult();

                                    final StorageReference fileRef2 = storageReference.child("Uploads/" + System.currentTimeMillis());

                                    Bitmap bmp = null;
                                    try {
                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), gstCertificateUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                    byte[] data = baos.toByteArray();

                                    gstTask = fileRef2.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            downloadUrl3 = urlTask.getResult();
                                            if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                sellerDataRef.child("storeLogo").setValue(downloadUrl1res.toString());
                                                sellerDataRef.child("gstCertificateImage").setValue(downloadUrl3.toString());
                                                sellerDataRef.child("aadharImage").setValue(downloadUrl2.toString());
                                            }


                                            if (downloadUrl3 != null) {
                                                if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                    loadDataFunction(sellerDataRef);
                                                }
                                                if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                    pDialog.dismiss();
                                                }
                                            }
                                        }
                                    });
                                }
                            });


                        } else if (storeImageUri != null && !storeImageUri.equals("") && downloadUrl2res != null && !downloadUrl2res.equals("") &&
                                gstCertificateUri != null && !gstCertificateUri.equals("")) {
                            pDialog = new SweetAlertDialog(SellerProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#150055"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();



                            StorageReference fileRef = storageReference.child("Uploads/" + System.currentTimeMillis());
                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), storeImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();

                            storeImageTask = fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl1 = urlTask.getResult();

                                    final StorageReference fileRef2 = storageReference.child("Uploads/" + System.currentTimeMillis());

                                    Bitmap bmp = null;
                                    try {
                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), gstCertificateUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                    byte[] data = baos.toByteArray();

                                    gstTask = fileRef2.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            downloadUrl3 = urlTask.getResult();
                                            if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                sellerDataRef.child("storeLogo").setValue(downloadUrl1.toString());
                                                sellerDataRef.child("gstCertificateImage").setValue(downloadUrl3.toString());
                                                sellerDataRef.child("aadharImage").setValue(downloadUrl2res.toString());
                                            }


                                            if (downloadUrl3 != null) {
                                                if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                    loadDataFunction(sellerDataRef);
                                                }
                                                if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                    pDialog.dismiss();
                                                }
                                            }
                                        }
                                    });
                                }
                            });

                        } else if (storeImageUri != null && !storeImageUri.equals("") && aadharIdUri != null && !aadharIdUri.equals("") &&
                                downloadUrl3res != null && !downloadUrl3res.equals("")) {
                            pDialog = new SweetAlertDialog(SellerProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#150055"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();



                            StorageReference fileRef = storageReference.child("Uploads/" + System.currentTimeMillis());
                            Bitmap bmp = null;
                            try {
                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), storeImageUri);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                            byte[] data = baos.toByteArray();

                            storeImageTask = fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    downloadUrl1 = urlTask.getResult();

                                    StorageReference fileRef1 = storageReference.child("Uploads/" + System.currentTimeMillis());

                                    Bitmap bmp = null;
                                    try {
                                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharIdUri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                    byte[] data = baos.toByteArray();

                                    aadharImageTask = fileRef1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                            while (!urlTask.isSuccessful()) ;
                                            downloadUrl2 = urlTask.getResult();
                                            if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                sellerDataRef.child("storeLogo").setValue(downloadUrl1.toString());
                                                sellerDataRef.child("gstCertificateImage").setValue(downloadUrl3res.toString());
                                                sellerDataRef.child("aadharImage").setValue(downloadUrl2.toString());
                                            }


                                            if (downloadUrl3 != null) {
                                                if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                    loadDataFunction(sellerDataRef);
                                                }
                                                if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                    pDialog.dismiss();
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            pDialog = new SweetAlertDialog(SellerProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                            pDialog.getProgressHelper().setBarColor(Color.parseColor("#150055"));
                            pDialog.setTitleText("Loading ...");
                            pDialog.setCancelable(false);
                            pDialog.show();



                            if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                sellerDataRef.child("storeLogo").setValue(downloadUrl1res.toString());
                                sellerDataRef.child("gstCertificateImage").setValue(downloadUrl3res.toString());
                                sellerDataRef.child("aadharImage").setValue(downloadUrl2res.toString());
                                if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                    loadDataFunction(sellerDataRef);
                                }
                                if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                    pDialog.dismiss();
                                }
                            }
                        }

                    }
                } else {

                    if ("".equals(storeAddressstring)) {
                        storeAddressEdt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "StoreAddress Required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if (!"".equalsIgnoreCase(storeAddressstring) && !(storeAddressstring.length() >= 5)) {
                        storeAddressEdt.setError("Storeaddress name should have minimum 5 characters");
                        Toast.makeText(SellerProfileActivity.this, "Storeaddress name should have minimum 5 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if (multiSelectionSpinner.getSelectedItems().size() == 0 && selectedSpinnerData.getText().toString().equals("")) {
                        Toast.makeText(SellerProfileActivity.this, "Please select Category", Toast.LENGTH_SHORT).show();
                        return;

                    }

                    if ("".equals(sellerName)) {
                        firstNameEdt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "First Name Required", Toast.LENGTH_SHORT).show();
                        return;

                    } else if (!"".equalsIgnoreCase(sellerName) && !(sellerName.length() >= 3)) {
                        firstNameEdt.setError("seller name should have minimum 3 characters");
                        Toast.makeText(SellerProfileActivity.this, "First Name should have minimum 3 characters", Toast.LENGTH_SHORT).show();

                        return;
                    } else if (!"".equalsIgnoreCase(sellerName) && !TextUtils.validateCustomerName(sellerName)) {
                        firstNameEdt.setError("Enter Valid Name");
                        Toast.makeText(SellerProfileActivity.this, "Enter Valid First Name", Toast.LENGTH_SHORT).show();

                        return;
                    } else if ("".equals(sellerLastName)) {
                        lastnameEdt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "Last Name Required", Toast.LENGTH_SHORT).show();

                        return;
                    } else if (!"".equalsIgnoreCase(sellerLastName) && !TextUtils.validateCustomerName(sellerLastName)) {
                        lastnameEdt.setError("Enter Valid Name");
                        Toast.makeText(SellerProfileActivity.this, "Enter Valid Last Name", Toast.LENGTH_SHORT).show();
                        return;
                    } else if ("".equals(emailstring)) {
                        email_IdEDt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "Enter email Id", Toast.LENGTH_SHORT).show();

                        return;
                    } else if (!"".equalsIgnoreCase(emailstring) && !TextUtils.isValidEmail(emailstring)) {
                        email_IdEDt.setError("Enter Valid Email");
                        Toast.makeText(SellerProfileActivity.this, "Enter Valid email Id", Toast.LENGTH_SHORT).show();
                        return;
                    }   else if ("".equals(pincode)) {
                        pincodeEdt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, " Pincode  Required  ", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    else if (!"".equalsIgnoreCase(pincode) && !(pincode.length() >= 6)) {
                        pincodeEdt.setError("Pincode must be 6 numbers");
                        Toast.makeText(SellerProfileActivity.this, "Pincode must be 6 numbers ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    else if ("".equals(bankstring)) {
                        bankNameEdt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "Bank name Required", Toast.LENGTH_SHORT).show();

                        return;
                    } else if (!"".equalsIgnoreCase(bankstring) && !(bankstring.length() >= 3)) {
                        bankNameEdt.setError("Bank name  requires minimum 3 characters");
                        Toast.makeText(SellerProfileActivity.this, "Bank name  requires minimum 3 characters", Toast.LENGTH_SHORT).show();
                        return;
                    } else if ("".equals(branchString)) {
                        branchNameEdt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "Branch Name Required", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!"".equalsIgnoreCase(branchString) && !(branchString.length() >= 3)) {
                        branchNameEdt.setError("Branch Name  requires minimum 3 characters");
                        Toast.makeText(SellerProfileActivity.this, "Branch Name  requires minimum 3 characters", Toast.LENGTH_SHORT).show();
                        return;
                    } else if ("".equals(accountNumberString)) {
                        accountnumber.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "Accountnumber Required", Toast.LENGTH_SHORT).show();

                        return;
                    } else if (!"".equalsIgnoreCase(accountNumberString) &&
                            !(accountNumberString.length() >= 12) || !(accountNumberString.length() <= 15)) {
                        accountnumber.setError("Enter Valid Account Number");
                        Toast.makeText(SellerProfileActivity.this, "Enter Valid Account Number", Toast.LENGTH_SHORT).show();

                        return;
                    } else if ("".equals(IFSCString)) {
                        ifscCodeEdt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "Ifsc code Required ", Toast.LENGTH_SHORT).show();

                        return;
                    } else if (!"".equalsIgnoreCase(IFSCString) && !TextUtils.validIFSCCode(IFSCString)) {
                        ifscCodeEdt.setError("Enter Valid IFSC Code");
                        Toast.makeText(SellerProfileActivity.this, "Enter Valid IFSC Code ", Toast.LENGTH_SHORT).show();

                        return;
                    } else if ("".equals(businessNameString)) {
                        businessNameEdt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "Store Name Required", Toast.LENGTH_SHORT).show();
                        return;
                    }  else if (businessNameString.length() < 3) {
                        businessNameEdt.setError("Store Name must be greater than 3 character");
                        Toast.makeText(SellerProfileActivity.this, "Store Name must be greater than 3 character", Toast.LENGTH_SHORT).show();

                        return;
                    }

                    else if ("".equals(businessTypeString)) {
                        businessTypeEdt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "business Type Required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if (businessTypeString.length() <3) {
                        businessTypeEdt.setError("Business type must be greater than 3 character");
                        Toast.makeText(SellerProfileActivity.this, "Business type must be greater than 3 character ", Toast.LENGTH_SHORT).show();

                        return;
                    }

                    else if ("".equals(AadharNumberString)) {
                        aadhaNumberEdt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "Aadhar Number Required", Toast.LENGTH_SHORT).show();

                        return;
                    } else if (!(AadharNumberString.length() == 12)) {
                        aadhaNumberEdt.setError("Invalid Aadhar Number");
                        Toast.makeText(SellerProfileActivity.this, "Invalid Aadhar Number", Toast.LENGTH_SHORT).show();
                        return;
                    } else if ("".equals(GstNumberString)) {
                        gstNumberEdt.setError("Required");
                        Toast.makeText(SellerProfileActivity.this, "Gst  Number Required", Toast.LENGTH_SHORT).show();

                        return;
                    } else if (!"".equalsIgnoreCase(GstNumberString) && !TextUtils.validate_GSTNumber(GstNumberString)) {
                        gstNumberEdt.setError("Invalid GST Number");
                        Toast.makeText(SellerProfileActivity.this, "Invalid GST Number", Toast.LENGTH_SHORT).show();

                        return;
                    } else if (storeImageUri == null || storeImageUri.equals("")) {
                        Toast.makeText(SellerProfileActivity.this, "Please upload store logo", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (aadharIdUri == null || aadharIdUri.equals("")) {
                        Toast.makeText(SellerProfileActivity.this, "Please upload aadhar Proof", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (gstCertificateUri == null || gstCertificateUri.equals("")) {
                        Toast.makeText(SellerProfileActivity.this, "Please upload fssai certificate", Toast.LENGTH_SHORT).show();
                        return;
                    } else if(!checkUser) {
                        if (storenamelist.size() > 0) {
                            for (int i = 0; i < storenamelist.size(); i++) {
                                if (storenamelist.get(i).equals(businessNameString)) {
                                    checkUser = false;
                                    Toast.makeText(SellerProfileActivity.this, "Store Name is already exist", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                else {
                                    checkUser = true;
                                }
                            }
                        }
                        else {
                            checkUser = true;
                        }
                    }

                    else {
                        pDialog = new SweetAlertDialog(SellerProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                        pDialog.getProgressHelper().setBarColor(Color.parseColor("#150055"));
                        pDialog.setTitleText("Loading ...");
                        pDialog.setCancelable(false);
                        pDialog.show();



                        StorageReference fileRef = storageReference.child("Uploads/" + System.currentTimeMillis());
                        Bitmap bmp = null;
                        try {
                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), storeImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                        byte[] data = baos.toByteArray();

                        storeImageTask = fileRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                downloadUrl1 = urlTask.getResult();

                                StorageReference fileRef1 = storageReference.child("Uploads/" + System.currentTimeMillis());

                                Bitmap bmp = null;
                                try {
                                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), aadharIdUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                byte[] data = baos.toByteArray();

                                aadharImageTask = fileRef1.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!urlTask.isSuccessful()) ;
                                        downloadUrl2 = urlTask.getResult();

                                        final StorageReference fileRef2 = storageReference.child("Uploads/" + System.currentTimeMillis());

                                        Bitmap bmp = null;
                                        try {
                                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), gstCertificateUri);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                                        byte[] data = baos.toByteArray();

                                        gstTask = fileRef2.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!urlTask.isSuccessful()) ;
                                                downloadUrl3 = urlTask.getResult();
                                                if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                    sellerDataRef.child("storeLogo").setValue(downloadUrl1.toString());
                                                    sellerDataRef.child("gstCertificateImage").setValue(downloadUrl3.toString());
                                                    sellerDataRef.child("aadharImage").setValue(downloadUrl2.toString());
                                                }


                                                if (downloadUrl3 != null) {
                                                    if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                        loadDataFunction(sellerDataRef);
                                                        //onTransaction(sellerDataRef);


                                                    }
                                                    if (!((Activity) SellerProfileActivity.this).isFinishing()) {
                                                        pDialog.dismiss();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }

            }

        });
    }

    private void openFileChooser() {
        intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0: // To get the imageview uri for separate imageview
                if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    downloadUrl1res = null;
                    storeImageUri = data.getData();
                    Picasso.get().load(storeImageUri).into(storeImageView);
                }
                break;
            case 1:
                if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    downloadUrl2res = null;
                    aadharIdUri = data.getData();
                    Picasso.get().load(aadharIdUri).into(aadharimageView);
                }
                break;
            case 2:
                if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    downloadUrl3res = null;
                    gstCertificateUri = data.getData();
                    Picasso.get().load(gstCertificateUri).into(gstImageView);
                }
                break;
        }


        Calendar c = Calendar.getInstance();
        // Set the calendar to monday of the current week
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        System.out.println();
        // Print dates of the current week starting on Monday
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        startDateMon = df.format(c.getTime());
        list.add(df.format(c.getTime()));
        for (int i = 0; i < 6; i++) {
            c.add(Calendar.DATE, 1);
            list.add(df.format(c.getTime()));
        }
        endDateSunday = df.format(c.getTime());


        if (approvedStatus.equals("Approved")) {
            orderdetailRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() > 0) {
                        for (DataSnapshot itemSnap : dataSnapshot.getChildren()) {
                            orderDetails = itemSnap.getValue(OrderDetails.class);
                            itemDetailsArrayList = (ArrayList<ItemDetails>) orderDetails.getItemDetailList();
                            if (itemDetailsArrayList != null && itemDetailsArrayList.size() > 0) {
                                if (itemDetailsArrayList.get(0).getSellerId().equals(sellerIdIntent)) {
                                    for (int i = 0; i < list.size(); i++) {
                                        if (orderDetails.getOrderStatus().equals("Delivered")) {
                                            if (orderDetails.getFormattedDate().equals(list.get(i))) {
                                                resultTotalAmount = resultTotalAmount + orderDetails.getPaymentamount() - orderDetails.getTipAmount();
                                                orderListSize.add(orderDetails.getOrderId());
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        DatabaseReference startTimeDataRef = CommonMethods.fetchFirebaseDatabaseReference("Payments").child(sellerIdIntent).child(startDateMon);
                        startTimeDataRef.child("startDate").setValue(startDateMon);
                        startTimeDataRef.child("endDate").setValue(endDateSunday);
                        startTimeDataRef.child("totalAmount").setValue(resultTotalAmount);
                        startTimeDataRef.child("orderCount").setValue(String.valueOf(orderListSize.size()));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sellerProfile) {
            Intent intent = new Intent(getApplicationContext(), SellerProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.addItem) {
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
        }else if (id == R.id.add_description) {
            Intent intent = new Intent(getApplicationContext(), Add_Description_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        else if (id == R.id.payments) {
            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }

        else if (id == R.id.logout)
        {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SellerProfileActivity.this);
            bottomSheetDialog.setContentView(R.layout.logout_confirmation);
            Button logout = bottomSheetDialog.findViewById(R.id.logout);
            Button stayinapp = bottomSheetDialog.findViewById(R.id.stayinapp);

            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!((Activity) SellerProfileActivity.this).isFinishing()) {

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
    public void onBackPressed() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(SellerProfileActivity.this);
        bottomSheetDialog.setContentView(R.layout.application_existing_dialog);
        Button quit = bottomSheetDialog.findViewById(R.id.quit_dialog);
        Button cancel = bottomSheetDialog.findViewById(R.id.cancel_dialog);

        bottomSheetDialog.show();
        bottomSheetDialog.setCancelable(false);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                bottomSheetDialog.dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();

            }
        });
    }

    private void preLoadFunction(String firstName, String lastName, String email_id, String mobileNumber,
                                 String address, String zipCode, String bankName, String bankBranchName, String accountNumber,
                                 String bankIfscCode, String aadharNumber, String businessname, String businessType, String gstNumber, String fssaiNumber, String aadharProof, String storeLogo, String gstCertificateImage, String status, String comments) {

        String trimmedfirstName = firstName.replaceFirst("^\\s+", ""); // Remove leading space
        String trimmedlastName = lastName.replaceFirst("^\\s+", ""); // Remove leading space
        String trimmedemailid = email_id.replaceFirst("^\\s+", ""); // Remove leading space
        String trimmedaddres = address.replaceFirst("^\\s+", ""); // Remove leading space
        String trimmedbankname = bankName.replaceFirst("^\\s+", ""); // Remove leading space
        String trimmedbranchname = bankBranchName.replaceFirst("^\\s+", ""); // Remove leading space
        String trimmedbanktype = businessType.replaceFirst("^\\s+", ""); // Remove leading space
        String trimmedbusinessname = businessname.replaceFirst("^\\s+", ""); // Remove leading space


        firstNameEdt.setText(trimmedfirstName);
        lastnameEdt.setText(trimmedlastName);
        email_IdEDt.setText(trimmedemailid);
        phoneNumberEdt.setText(mobileNumber);
        storeAddressEdt.setText(trimmedaddres);
        pincodeEdt.setText(zipCode);
        bankNameEdt.setText(trimmedbankname);
        branchNameEdt.setText(trimmedbranchname);
        accountnumber.setText(accountNumber);
        ifscCodeEdt.setText(bankIfscCode);
        aadhaNumberEdt.setText(aadharNumber);
        businessNameEdt.setText(trimmedbusinessname);
        businessTypeEdt.setText(trimmedbanktype);
        gstNumberEdt.setText(gstNumber);
        statusTxt.setText(status);
        suggestion.setText(comments);

        if (storeLogo != null || !storeLogo.equals("")) {
            String deliveryBoyProfileUri = String.valueOf(Uri.parse(storeLogo));

            Picasso.get().load(deliveryBoyProfileUri).into(storeImageView);
            downloadUrl1res = Uri.parse(deliveryBoyProfileUri);

        }

        if (aadharProof != null || !aadharProof.equals("")) {
            String aadharProofeUri1 = String.valueOf(Uri.parse(aadharProof));
            Picasso.get().load(aadharProofeUri1).into(aadharimageView);

            downloadUrl2res = Uri.parse(aadharProofeUri1);
        }

        if (gstCertificateImage != null || !gstCertificateImage.equals("")) {
            String drivingLicenseProofUri = String.valueOf(Uri.parse(gstCertificateImage));
            Picasso.get().load(drivingLicenseProofUri).into(gstImageView);

            downloadUrl3res = Uri.parse(drivingLicenseProofUri);
        }
    }

    public void createNotification(String res, String orderId) {
        int count = 0;
        if (count == 0) {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SellerProfileActivity.this, default_notification_channel_id)
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
    protected void onStop() {
        super.onStop();
    }



    public void hideData() {
        firstNameEdt.setEnabled(false);
        lastnameEdt.setEnabled(false);
        email_IdEDt.setEnabled(false);
        phoneNumberEdt.setEnabled(false);
        storeAddressEdt.setEnabled(false);
        pincodeEdt.setEnabled(false);
        bankNameEdt.setEnabled(false);
        branchNameEdt.setEnabled(false);
        accountnumber.setEnabled(false);
        ifscCodeEdt.setEnabled(false);
        businessNameEdt.setEnabled(false);
        businessTypeEdt.setEnabled(false);

        aadhaNumberEdt.setEnabled(false);
        gstNumberEdt.setEnabled(false);
        uploadKycButton.setEnabled(false);
        uploadKycButton.setVisibility(View.INVISIBLE);
        storeProfileButton.setEnabled(false);
        aadharIdbutton.setEnabled(false);
        fssaicertificateButton.setEnabled(false);
        selectedSpinnerData.setVisibility(View.VISIBLE);
        multiSelectionSpinner.setVisibility(View.INVISIBLE);
    }

    public void unhideData() {
        firstNameEdt.setEnabled(true);
        lastnameEdt.setEnabled(true);
        email_IdEDt.setEnabled(true);
        phoneNumberEdt.setEnabled(false);
        storeAddressEdt.setEnabled(true);
        pincodeEdt.setEnabled(false);
        bankNameEdt.setEnabled(true);
        branchNameEdt.setEnabled(true);
        accountnumber.setEnabled(true);
        ifscCodeEdt.setEnabled(true);
        businessNameEdt.setEnabled(true);
        businessTypeEdt.setEnabled(true);
        aadhaNumberEdt.setEnabled(true);
        gstNumberEdt.setEnabled(true);
        uploadKycButton.setEnabled(true);
        uploadKycButton.setVisibility(View.VISIBLE);
        storeProfileButton.setEnabled(true);
        aadharIdbutton.setEnabled(true);
        fssaicertificateButton.setEnabled(true);

        multiSelectionSpinner.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }


    public void autoLoadFunction(String phoneNumber){
        Query query =CommonMethods.fetchFirebaseDatabaseReference(SELLERLOGIN_DETAILS).orderByChild("phoneNumber").equalTo(phoneNumber);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.getChildrenCount() > 0) {
                        for (DataSnapshot approvedStatusSnap : snapshot.getChildren()) {
                            UserDetails userDetails = approvedStatusSnap.getValue(UserDetails.class);
                            approvedStatus = userDetails.getApprovalStatus();
                            screenPermissions();
                            storeName = userDetails.getStoreName();
                            storeImage = userDetails.getStoreLogo();
                            storePincode = userDetails.getPincode();
                            textViewUsername.setText(storeName);
                            if (storeImage != null && !"".equals(storeImage)) {
                                Picasso.get().load(String.valueOf(Uri.parse(storeImage))).into(imageView);
                            }
                            selectedSpinnerData.setText("");
                            categoryDetailsArrayList.clear();
                            categoryDetailsArrayList = userDetails.getCategoryList();
                            for (int i = 0; i < categoryDetailsArrayList.size(); i++) {

                                if (userDetails.getCategoryList().size() == 1) {
                                    selectedSpinnerData.append(categoryDetailsArrayList.get(i).getCategoryName() + ".");
                                } else {
                                    selectedSpinnerData.append(categoryDetailsArrayList.get(i).getCategoryName() + ",");
                                }
                            }

                            if (approvedStatus.equals("Approved") || approvedStatus.equals("Waiting for approval")) {

                                hideData();
                                if (userDetails.getStoreLogo() != null && userDetails.getAadharImage() != null && userDetails.getGstCertificateImage() != null) {
                                    preLoadFunction(userDetails.getFirstName(), userDetails.getLastName()
                                            , userDetails.getEmail_Id(), userDetails.getPhoneNumber(), userDetails.getAddress(), userDetails.getPincode(),
                                            userDetails.getBankName(), userDetails.getBranchName(), userDetails.getAccountNumber(),
                                            userDetails.getIfscCode(), userDetails.getAadharNumber(), userDetails.getStoreName(), userDetails.getBusinessType(), userDetails.getGstNumber(), userDetails.getFssaiNumber(),
                                            userDetails.getAadharImage(), userDetails.getStoreLogo(), userDetails.getGstCertificateImage(), userDetails.getApprovalStatus(), userDetails.getCommentsIfAny());
                                }
                            } else if (approvedStatus.equals("Rejected")) {
                                unhideData();

                                if (userDetails.getStoreLogo() != null && userDetails.getAadharImage() != null && userDetails.getGstCertificateImage() != null) {
                                    preLoadFunction(userDetails.getFirstName(), userDetails.getLastName()
                                            , userDetails.getEmail_Id(), userDetails.getPhoneNumber(), userDetails.getAddress(), userDetails.getPincode(),
                                            userDetails.getBankName(), userDetails.getBranchName(), userDetails.getAccountNumber(),
                                            userDetails.getIfscCode(), userDetails.getAadharNumber(), userDetails.getStoreName(), userDetails.getBusinessType(), userDetails.getGstNumber(), userDetails.getFssaiNumber()
                                            , userDetails.getAadharImage(), userDetails.getStoreLogo(), userDetails.getGstCertificateImage(), userDetails.getApprovalStatus(), userDetails.getCommentsIfAny());
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
    }

    public void loadDataFunction(DatabaseReference sellerDetailsDataRef) {

        sellerDetailsDataRef.child("firstName").setValue(firstNameEdt.getText().toString().trim());
        sellerDetailsDataRef.child("lastName").setValue(lastnameEdt.getText().toString().trim());
        sellerDetailsDataRef.child("email_Id").setValue(email_IdEDt.getText().toString());
        sellerDetailsDataRef.child("phoneNumber").setValue(phoneNumberEdt.getText().toString());
        sellerDetailsDataRef.child("address").setValue(storeAddressEdt.getText().toString());
        sellerDetailsDataRef.child("pincode").setValue(pincodeEdt.getText().toString());
        sellerDetailsDataRef.child("bankName").setValue(bankNameEdt.getText().toString());
        sellerDetailsDataRef.child("branchName").setValue(branchNameEdt.getText().toString());
        sellerDetailsDataRef.child("accountNumber").setValue(accountnumber.getText().toString());
        sellerDetailsDataRef.child("ifscCode").setValue(ifscCodeEdt.getText().toString());
        sellerDetailsDataRef.child("storeName").setValue(businessNameEdt.getText().toString());
        sellerDetailsDataRef.child("businessType").setValue(businessTypeEdt.getText().toString());
        sellerDetailsDataRef.child("aadharNumber").setValue(aadhaNumberEdt.getText().toString());
        sellerDetailsDataRef.child("gstNumber").setValue(gstNumberEdt.getText().toString());
        sellerDetailsDataRef.child("approvalStatus").setValue("Waiting for approval");
        sellerDetailsDataRef.child("commentsIfAny").setValue("");
        if (selectedSpinnerData.getText().toString().equals("")) {
            sellerDetailsDataRef.child("categoryList").setValue(multiSelectionSpinner.getSelectedItems());
        }
        storeTimings.setCreationDate("");
        storeTimings.setShopStartTime("");
        storeTimings.setShopEndTime("");
        storeTimings.setSellerId(SellerProfileActivity.saved_id);
        storeTimings.setStoreStatus("");
        storeTimingMaintenanceDataRef.child(String.valueOf(SellerProfileActivity.saved_id)).setValue(storeTimings);

        Intent intent = new Intent(SellerProfileActivity.this,SellerProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();

        newsellerDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storenamelist.clear();
                if (snapshot.getChildrenCount() >0){
                    for (DataSnapshot sellerstorename : snapshot.getChildren()){
                        UserDetails userDetails = sellerstorename.getValue(UserDetails.class);
                        if(!SellerProfileActivity.saved_id.equals(userDetails.getUserId()) ) {
                            storenamelist.add(userDetails.getStoreName());
                        }

                    }
                    TextUtils.removeDuplicatesList(storenamelist);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onTransaction(DatabaseReference sellerDetailsDataRef){

        sellerDetailsDataRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                sellerDetailsDataRef.child("firstName").setValue(firstNameEdt.getText().toString());
                sellerDetailsDataRef.child("lastName").setValue(lastnameEdt.getText().toString());
                sellerDetailsDataRef.child("email_Id").setValue(email_IdEDt.getText().toString());
                sellerDetailsDataRef.child("phoneNumber").setValue(phoneNumberEdt.getText().toString());
                sellerDetailsDataRef.child("address").setValue(storeAddressEdt.getText().toString());
                sellerDetailsDataRef.child("pincode").setValue(pincodeEdt.getText().toString());
                sellerDetailsDataRef.child("bankName").setValue(bankNameEdt.getText().toString());
                sellerDetailsDataRef.child("branchName").setValue(branchNameEdt.getText().toString());
                sellerDetailsDataRef.child("accountNumber").setValue(accountnumber.getText().toString());
                sellerDetailsDataRef.child("ifscCode").setValue(ifscCodeEdt.getText().toString());
                sellerDetailsDataRef.child("storeName").setValue(businessNameEdt.getText().toString());
                sellerDetailsDataRef.child("businessType").setValue(businessTypeEdt.getText().toString());
                sellerDetailsDataRef.child("aadharNumber").setValue(aadhaNumberEdt.getText().toString());
                sellerDetailsDataRef.child("gstNumber").setValue(gstNumberEdt.getText().toString());
                sellerDetailsDataRef.child("approvalStatus").setValue("Waiting for approval");
                sellerDetailsDataRef.child("commentsIfAny").setValue("");
                if (selectedSpinnerData.getText().toString().equals("")) {
                    currentData.child("categoryList").setValue(multiSelectionSpinner.getSelectedItems());
                }
                storeTimings.setCreationDate("");
                storeTimings.setShopStartTime("");
                storeTimings.setShopEndTime("");
                storeTimings.setSellerId(SellerProfileActivity.saved_id);
                storeTimings.setStoreStatus("");
                storeTimingMaintenanceDataRef.child(String.valueOf(SellerProfileActivity.saved_id)).setValue(storeTimings);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
    }

}







