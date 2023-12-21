package com.smiligence.etmsellerapk;

import static com.smiligence.etmsellerapk.Common.Constant.PRODUCT_DETAILS_TABLE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smiligence.etmsellerapk.Adapter.ItemAdapter;
import com.smiligence.etmsellerapk.Common.CommonMethods;
import com.smiligence.etmsellerapk.Common.Constant;
import com.smiligence.etmsellerapk.Common.NetworkStateReceiver;
import com.smiligence.etmsellerapk.bean.ItemDetails;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Add_Description_Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , NetworkStateReceiver.NetworkStateReceiverListener{

    NavigationView navigationView;
    public static Menu menuNav;
    public static TextView textViewUsername;
    public static ImageView imageView;
    public static View mHeaderView;
    String sellerIdIntent,storeImageIntent,storeNameIntent,storePinCode;
    RecyclerView recyclerView;
    DatabaseReference itemDetailsRef ;
    Query fetchItem;
    ArrayList<String> itemNameList = new ArrayList<>();
    ArrayList<ItemDetails> itemDetailList = new ArrayList<ItemDetails> ();
    ItemAdapter itemAdapter;
    ItemDetails existingitemDetails;
    private Uri filepath;
    StorageReference storageReference;
    private final int PICK_PDF_CODE = 2342;
    int maxid = 0;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_description);

        recyclerView = findViewById(R.id.recycler_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitle(Constant.AddDESCRIPTION);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menuNav = navigationView.getMenu();
        mHeaderView = navigationView.getHeaderView(0);


        navigationView.setNavigationItemSelectedListener(Add_Description_Activity.this);
        drawer.setDrawerListener(toggle);
        navigationView.setCheckedItem(R.id.add_description);

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



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(Add_Description_Activity.this));


        itemDetailsRef = CommonMethods.fetchFirebaseDatabaseReference(PRODUCT_DETAILS_TABLE);
        storageReference = CommonMethods.fetchFirebaseStorageReference ( Constant.ITEMDETAILS_TABLE );


        fetchItem = itemDetailsRef.orderByChild("sellerId").equalTo(sellerIdIntent);

        fetchItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {

                    itemDetailList.clear();
                    itemNameList.clear();

                    for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                        ItemDetails itemDetails = itemSnapshot.getValue(ItemDetails.class);
                        itemDetailList.add(itemDetails);
                        itemNameList.add(itemDetails.getItemName());

                    }


                    if (itemDetailList.size() > 0) {
                        itemAdapter = new ItemAdapter(Add_Description_Activity.this, itemDetailList);
                        recyclerView.setAdapter(itemAdapter);
                        itemAdapter.notifyDataSetChanged();
                    }

                    if (itemAdapter != null) {
                        itemAdapter.setOnItemclickListener(new ItemAdapter.OnItemClicklistener() {
                            @Override
                            public void Onitemclick(int Position) {
                                existingitemDetails = itemDetailList.get(Position);

                                CharSequence[] items = {"Add Description", "View Description"};
                                AlertDialog.Builder dialog = new AlertDialog.Builder(Add_Description_Activity.this);
                                dialog.setTitle("Choose");
                                dialog.setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, final int item) {

                                        if (item == 0) {
                                            chooseDoc();
                                        } else if (item == 1) {
                                            if (existingitemDetails.getDescriptionUrl() != null && !existingitemDetails.getDescriptionUrl().equals("")) {
                                                Intent viewWebIntent = new Intent(getApplicationContext(), WebPageActivity.class);
                                                viewWebIntent.putExtra("intentHtmlUrl", existingitemDetails.getDescriptionUrl());
                                                startActivity(viewWebIntent);
                                            } else {
                                                Toast.makeText(Add_Description_Activity.this, "No File Uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });

                                dialog.show();


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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (data.getData() != null) {
                filepath = data.getData();
                UploadFile();
            } else {
                Toast.makeText(this, "NO FILE CHOSEN", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void UploadFile() {
        if (filepath != null) {
            Date dateObject = new Date(System.currentTimeMillis());
            String formattedDate = formatDate(dateObject);
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference sref = storageReference.child("HTMLFILES" + formattedDate + ".html");

            sref.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());
                            Uri downloadUrl_update = urlTask.getResult();
                            Log.d("url", downloadUrl_update.toString());
                            Log.e("existingitemDetails", String.valueOf(existingitemDetails.getItemId()));
                            itemDetailsRef.child(String.valueOf(existingitemDetails.getItemId())).child("descriptionUrl").setValue(downloadUrl_update.toString());
                            Toast.makeText(Add_Description_Activity.this, "Uploaded Succesfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Add_Description_Activity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot
                                    .getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sellerProfile) {
            Intent intent = new Intent(Add_Description_Activity.this, SellerProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.addItem) {
            Intent intent = new Intent(Add_Description_Activity.this, AddItemActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.orderHistory) {
            Intent intent = new Intent(Add_Description_Activity.this, OrderHistoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }else if (id == R.id.add_description) {
            Intent intent = new Intent(Add_Description_Activity.this, Add_Description_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else if (id == R.id.payments) {
            Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


        else if (id == R.id.dashboard) {
            Intent intent = new Intent(Add_Description_Activity.this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.logout) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Add_Description_Activity.this);
            bottomSheetDialog.setContentView(R.layout.logout_confirmation);
            Button logout = bottomSheetDialog.findViewById(R.id.logout);
            Button stayinapp = bottomSheetDialog.findViewById(R.id.stayinapp);

            bottomSheetDialog.show();
            bottomSheetDialog.setCancelable(false);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!((Activity) Add_Description_Activity.this).isFinishing()) {

                        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(Add_Description_Activity.this, OtpRegister.class);
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

    private void chooseDoc() {
        Intent intent = new Intent();
        intent.setType("text/html");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), PICK_PDF_CODE);
    }

    private String formatDate(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), SellerProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        itemDetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                maxid = (int) dataSnapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
        if (!((Activity) Add_Description_Activity.this).isFinishing()) {
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