package com.smiligence.etmsellerapk;

import static com.smiligence.etmsellerapk.Common.Constant.CATEGORY;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.smiligence.etmsellerapk.Adapter.ImageSelectedAdapter;
import com.smiligence.etmsellerapk.Adapter.ItemAdapter;
import com.smiligence.etmsellerapk.Common.CommonMethods;
import com.smiligence.etmsellerapk.Common.Constant;
import com.smiligence.etmsellerapk.Common.NetworkStateReceiver;
import com.smiligence.etmsellerapk.Common.TextUtils;
import com.smiligence.etmsellerapk.bean.CategoryDetails;
import com.smiligence.etmsellerapk.bean.ItemDetails;
import com.smiligence.etmsellerapk.bean.UserDetails;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddItemActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NetworkStateReceiver.NetworkStateReceiverListener {

    NavigationView navigationView;
    View mHeaderView;
    TextView textViewUsername;
    ImageView imageView;
    DatabaseReference categoryRef, itemDetailsRef;
    StorageReference itemStorageRef;
    String sellerIdIntent,storeNameIntent,storeImage;
    Query fetchItem;
    RecyclerView recyclerView;
    public static int itemID;
    String sellerLogo;
    String sellerPinCode, storeAddress;
    FloatingActionButton fab;
    ArrayList<ItemDetails> itemDetailList = new ArrayList<ItemDetails> ();
    ArrayList<String>itemNameList = new ArrayList<>();
    ArrayList<String>itemnamearraylist = new ArrayList<>();
    ItemAdapter itemAdapter;
    int count=1;
    Button b_upload, b_choosefile;
    Spinner spinnerCategory ;
    String selectedCategoryName;
    String selectedItemQuantityunits;
    ImageView image;
    ProgressBar progressBar;
    EditText name, fixedprice, quantity , brand, description, mrpPrice, features ,taxprice;
    EditText categoryNameEditText, subcategoryNameEditText;
    TextView itemTypeTxt;
    String itemName, itemPrice, itemBrand, quantityString, itemDescription, itemMRPprice, itemFeatures, itemRating, itemReview ,itemtaxprice;
    EditText itemLimitation;
    AlertDialog b;
    ArrayList<CategoryDetails> categoryList = new ArrayList<CategoryDetails> ();
    ArrayList<String> categoryNamestring = new ArrayList<> ();
    ArrayAdapter<String> categoryArrayAdapter;
    String itemQuantityUnitsString, itemLimitationString;
    String subcategory;
    private Uri mimageuri, mimageuriUpdate,updateImageUri;
    ItemDetails itemDetails;
    ItemDetails updateitemdetailsDetails;
    private StorageTask mItemStorageTask;
    Integer increamentId;
    Intent intentImage = new Intent ();
    private static int PICK_IMAGE_REQUEST;
    private static int PICK_IMAGE_Limit;
    int clipDataCount;
    int uploadCount = 0;
    ImageView image_Item ;
    AutoCompleteTextView autotextview ;
    String SelectedcategoryID;
    ArrayList<String> imageStringList = new ArrayList<>();
    Button b_multipleimages;
    List<Bitmap> bitmapList = new ArrayList<>();
    ArrayList<Uri> pickimageList = new ArrayList<>();
    ArrayList<String> pickimageStringList = new ArrayList<>();
    int countIndicator = 0;
    ImageView updatedImage,multipleSelectionImages;
    ImageView multipleimagesview;
    RecyclerView  selectedImageRecyclerView,selectedimagesUpdate;
    Uri multipleimageUri;
    private NetworkStateReceiver networkStateReceiver;
    AlertDialog alertDialog;
    ViewGroup viewGroup;
    private static final int MAX_IMAGE_SELECTION_LIMIT = 5;

    String[] Subcategory_list ={"Desktops","Laptops","Monitors","Printers","Storage Devices",
            "Keyboards and Mice","Headphones ","Cables and Adapters","Graphics Cards","Routers and Networking Devices" ,"Software",
             "Televisions","Sound Systems","Streaming Devices","Blu-ray and DVD Players","Video Game Consoles","Home Theater Seating","Projectors","Home Automation","Outdoor Entertainment","Accessories",
             "Digital Cameras","Action Cameras","Lenses","Camcorders","Drone Cameras","Camera Accessories","Film Cameras","Camera and Video Lights","Video Equipment","Camera and Camcorder Repair",
                "Cellphones","Cellphone Cases","Screen Protectors","Chargers and Power Banks","Headphones and Earbuds","Memory Cards","Selfie Sticks and Tripods",
                  "Consoles","Video Games","Gaming Accessories","Virtual Reality Headsets","Gaming Keyboards","Gaming Mice","Gaming Chairs","Gaming Desks","Gaming Laptops",
                   "Portable Speakers","Wireless Headphones","Earbuds and In-Ear Headphones","Over-Ear Headphones","On-Ear Headphones","Noise-Cancelling Headphones","Sport Headphones","Kids Headphones","MP3 and Media Players",
                    "Voice Recorders","FM Transmitters","Portable Bluetooth Transmitters","Digital Voice Recorders","Audio Docks and Mini Speakers","Car Audio Adapters",
                   "Desks and Workstations","Office Chairs","Bookshelves and Storage","Filing and Organizing","Lighting","Computers and Accessories","Office Supplies","Office Decor","Stationery","Printer, Scanner, and Copier",
                 "Smartwatches","Fitness trackers","Smart clothing","Hearables","Augmented Reality Glasses","Virtual Reality Headset","Smart Glasses","Sleep trackers","GPS trackers","Health monitoring devices",
               "In-dash infotainment systems","Navigation systems","Rear-seat entertainment systems","Car audio systems","Telematics and connectivity systems","Advanced driver assistance systems (ADAS)","Parking assistance systems","Vehicle tracking and theft prevention systems",
    "Bluetooth hands-free systems","Power inverters and charging systems","Remote start and keyless entry systems",
    "Portable GPS devices","In-dash navigation systems","Smartphone navigation apps","Fleet management and commercial transportation navigation systems","Marine navigation systems","Outdoor and hiking GPS devices","Aviation navigation systems","Agricultural navigation and guidance systems",
    "Wearable GPS devices","Automotive dead-reckoning systems","GNSS (Global Navigation Satellite Systems) Receivers",
    "Video surveillance systems (CCTV)","Alarm systems","Access control systems","Intruder detection systems","Fire alarm systems","Intercom and entry systems","Biometric identification systems","Perimeter security systems","Smart home security systems","Electronic article surveillance (EAS) systems",
    "Wireless security systems" ,"Physical security systems","Keyboards and pianos","Guitars","Drums and percussion instruments","Brass instruments","Woodwind instruments","String instruments","Electronic musical instruments","Traditional instruments","Folk instruments","Percussion instruments","Amplifiers and sound systems",
    "Accessories and parts"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        setRequestedOrientation ( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );

        Toolbar toolbar = (Toolbar) findViewById ( R.id.toolbar );
        toolbar.setTitle ( Constant.MAINTAIN_ITEMS );
        DrawerLayout drawer = (DrawerLayout) findViewById ( R.id.drawer_layout );
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle (
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );

        toggle.syncState ();
        toggle.getDrawerArrowDrawable ().setColor ( getResources ().getColor ( R.color.white ) );
        navigationView = (NavigationView) findViewById ( R.id.nav_view );
        navigationView.setNavigationItemSelectedListener ( AddItemActivity.this );
        drawer.setDrawerListener ( toggle );
        navigationView.setCheckedItem ( R.id.addItem );
        mHeaderView = navigationView.getHeaderView ( 0 );

        textViewUsername = (TextView) mHeaderView.findViewById ( R.id.name );
        imageView = (ImageView) mHeaderView.findViewById ( R.id.imageViewheader );

        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


        fab = findViewById ( R.id.fab );
        recyclerView = findViewById ( R.id.recycler_view );

        recyclerView.setHasFixedSize ( true );
        recyclerView.setLayoutManager ( new LinearLayoutManager( AddItemActivity.this ) );



        categoryRef = CommonMethods.fetchFirebaseDatabaseReference(CATEGORY);
        itemDetailsRef = CommonMethods.fetchFirebaseDatabaseReference (Constant.PRODUCT_DETAILS_TABLE);
        itemStorageRef = CommonMethods.fetchFirebaseStorageReference ( Constant.ITEMDETAILS_TABLE );


        if (!"".equals(SellerProfileActivity.saved_id) && SellerProfileActivity.saved_id!=null && !"".equals(SellerProfileActivity.saved_customerPhonenumber) || SellerProfileActivity.saved_customerPhonenumber!=null && !"".equals(SellerProfileActivity.storeImage) && SellerProfileActivity.storeImage!=null ) {
            sellerIdIntent= SellerProfileActivity.saved_id;
            storeNameIntent =SellerProfileActivity.storeName;
            storeImage=SellerProfileActivity.storeImage;
        }
        else if (!"".equals(DashBoardActivity.saved_id) && DashBoardActivity.saved_id!=null && !"".equals(DashBoardActivity.saved_customerPhonenumber) || DashBoardActivity.saved_customerPhonenumber!=null && !"".equals(DashBoardActivity.storeImage) && DashBoardActivity.storeImage!=null  )
        {
            sellerIdIntent= DashBoardActivity.saved_id;
            storeNameIntent =DashBoardActivity.storeName;
            storeImage=DashBoardActivity.storeImage;
        }

        if (storeNameIntent != null && !"".equals ( storeNameIntent )) {
            textViewUsername.setText ( storeNameIntent );
        }
        if (storeImage != null && !"".equals ( storeImage )) {
            Picasso.get().load ( String.valueOf ( Uri.parse ( storeImage ) ) ).into ( imageView );
        }

        fetchItem = itemDetailsRef.orderByChild("sellerId").equalTo(sellerIdIntent);

        fetchItem.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0){

                    itemDetailList.clear();
                    itemNameList.clear();

                    for (DataSnapshot itemSnapshot : snapshot.getChildren()){
                        ItemDetails itemDetails = itemSnapshot.getValue(ItemDetails.class);
                        itemDetailList.add(itemDetails);
                        itemNameList.add(itemDetails.getItemName());

                    }


                    if (itemDetailList.size() > 0){
                        itemAdapter = new ItemAdapter(AddItemActivity.this, itemDetailList);
                        recyclerView.setAdapter(itemAdapter);
                        itemAdapter.notifyDataSetChanged();
                    }

                  if (itemAdapter != null){
    itemAdapter.setOnItemclickListener(new ItemAdapter.OnItemClicklistener() {
        @Override
        public void Onitemclick(int Position) {
            final ItemDetails itemdetailsPosition = itemDetailList.get(Position);

            {
                CharSequence[] items = {"Update the Status of an item", "Update Item"};
                androidx.appcompat.app.AlertDialog.Builder dialog = new androidx.appcompat.app.AlertDialog.Builder(AddItemActivity.this);
                dialog.setTitle("Choose an action");

                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int item) {

                        if (item == 0) {
                            final String Item_id = String.valueOf(itemdetailsPosition.getItemId());
                            final AlertDialog.Builder builder = new AlertDialog.Builder(AddItemActivity.this);
                            builder.setMessage("Select the item status for " + itemdetailsPosition.getItemName());
                            builder.setCancelable(true);
                            builder.setNegativeButton("InActive", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (!((Activity) AddItemActivity.this).isFinishing ()) {
                                        itemDetailsRef.child(Item_id).child("itemStatus").setValue(Constant.INACTIVE_STATUS);
                                        Toast.makeText(AddItemActivity.this, "Item Status Updated as InActive", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.setPositiveButton("Active", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (!((Activity) AddItemActivity.this).isFinishing ()) {
                                        itemDetailsRef.child(Item_id).child("itemStatus").setValue(Constant.ACTIVE_STATUS);
                                        Toast.makeText(AddItemActivity.this, "Item Status Updated as Active", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else if (item == 1) {

                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddItemActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.item_update, null);
                            dialogBuilder.setView(dialogView);

                            EditText updateitemName = dialogView.findViewById(R.id.update_file_name);
                            EditText updateItemPrice = dialogView.findViewById(R.id.update_item_price);
                            EditText updateItemMrpPrice = dialogView.findViewById(R.id.update_MRPprice);
                            EditText updateItemTaxPercent = dialogView.findViewById(R.id.update_tax);
                            EditText updateItemLimitation = dialogView.findViewById(R.id.update_itemLimitation);
                            EditText updateItemBrand = dialogView.findViewById(R.id.update_itembrand);
                            EditText updateDescription = dialogView.findViewById(R.id.update_itemdescription);
                            ImageView cancel = dialogView.findViewById(R.id.Cancel_subcategory);
                            Button buttonUpdate  = dialogView.findViewById(R.id.update_b_Upload);


                            final AlertDialog b = dialogBuilder.create();
                            b.show();
                            b.setCancelable(false);


                            int price = (itemdetailsPosition.getItemPrice());
                            int mrpprice = (itemdetailsPosition.getMRP_Price());
                            int taxprice = (itemdetailsPosition.getTax());
                            int limitation =(itemdetailsPosition.getItemMaxLimitation());


                            updateitemName.setText(itemdetailsPosition.getItemName());
                            updateItemPrice.setText(String.valueOf(price));
                            updateItemMrpPrice.setText(String.valueOf(mrpprice));
                            updateItemTaxPercent.setText(String.valueOf(taxprice));
                            updateItemLimitation.setText(String.valueOf(limitation));
                            updateItemBrand.setText(itemdetailsPosition.getItemBrand());
                            updateDescription.setText(itemdetailsPosition.getItemDescription());


                            int itemId = itemdetailsPosition.getItemId();

                          buttonUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //  itemDetailsRef.child(String.valueOf(itemId)).child("itemPrice").setValue((Integer.parseInt(price_updateEdit)));


                                     String itemName_updatename = updateitemName.getText().toString();
                                     String itemprice_updatePrice = updateItemPrice.getText().toString();
                                     String itemmrpprice = updateItemMrpPrice.getText().toString();
                                     String itemtaxprice = updateItemTaxPercent.getText().toString();
                                     String itemlimitation = updateItemLimitation.getText().toString();
                                     String  itemBrandupdate = updateItemBrand.getText().toString();
                                     String itemDescription = updateDescription.getText().toString();



                                     if ("".equals ( itemName_updatename )) {
                                             updateitemName.setError (Constant. REQUIRED_MSG );
                                            Toast.makeText(AddItemActivity.this, "Please enter Itemname ", Toast.LENGTH_SHORT).show();

                                            return;
                                        }
                                     else if (itemName_updatename.length() < 3){
                                         updateitemName.setError(Constant. REQUIRED_MSG );
                                         Toast.makeText(AddItemActivity.this, "Please enter itemName greater than 3", Toast.LENGTH_SHORT).show();
                                         return;
                                     }

                                     else if ((!android.text.TextUtils.isEmpty ( itemName_updatename )
                                                && !TextUtils.validateAlphaNumericCharcters ( itemName_updatename ))) {
                                              updateitemName.setError ( "Invalid Item Name" );
                                             Toast.makeText(AddItemActivity.this, "Invalid Item Name", Toast.LENGTH_SHORT).show();

                                            return;

                                        }  else if ("".equals ( itemprice_updatePrice )) {
                                             updateItemPrice.setError ( Constant.REQUIRED_MSG );
                                            Toast.makeText(AddItemActivity.this, "Enter  Item Price", Toast.LENGTH_SHORT).show();

                                            return;
                                        } else if ("0".equalsIgnoreCase(itemprice_updatePrice) || !TextUtils.isValidPrice(itemprice_updatePrice)) {
                                             updateItemPrice.setError("Enter valid Price");
                                            Toast.makeText(AddItemActivity.this, "Enter valid Item Price", Toast.LENGTH_SHORT).show();

                                            return;
                                        }else if (itemprice_updatePrice.startsWith("0")) {
                                             updateItemPrice.setError("fixed price should not starts with (0)");
                                            Toast.makeText(AddItemActivity.this, "fixed price should not starts with (0)", Toast.LENGTH_SHORT).show();

                                            if (itemprice_updatePrice.length() > 0) {
                                                updateItemPrice.setText(itemprice_updatePrice.substring(1));
                                                return;
                                            } else {
                                                updateItemPrice.setText("");
                                                return;
                                            }
                                        } else if ("".equals ( itemmrpprice )) {
                                             updateItemMrpPrice.setError ( Constant.REQUIRED_MSG );
                                            Toast.makeText(AddItemActivity.this, "Enter Mrp Price", Toast.LENGTH_SHORT).show();

                                            return;
                                        }else if ("0".equalsIgnoreCase(itemmrpprice) || !TextUtils.isValidPrice(itemmrpprice)) {
                                             updateItemMrpPrice.setError("Enter valid Price");
                                            Toast.makeText(AddItemActivity.this, "Enter valid Mrp Price", Toast.LENGTH_SHORT).show();

                                            return;
                                        }
                                        else if (itemmrpprice.startsWith("0")) {
                                             updateItemMrpPrice.setError("Mrp price should not starts with (0)");
                                            Toast.makeText(AddItemActivity.this, "Mrp price should not starts with (0)", Toast.LENGTH_SHORT).show();

                                            if (itemmrpprice.length() > 0) {
                                                updateItemMrpPrice.setText(itemmrpprice.substring(1));
                                                return;
                                            } else {
                                                updateItemMrpPrice.setText("");
                                                return;
                                            }
                                        } else  if(Integer.parseInt(itemprice_updatePrice)>Integer.parseInt(itemmrpprice))
                                        {
                                            updateItemPrice.setError("Fixed price should be less than MRP Price");
                                            Toast.makeText(AddItemActivity.this, "Fixed price should be less than MRP Price", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        else if ("".equals ( itemtaxprice )) {
                                             updateItemTaxPercent.setError ( Constant.REQUIRED_MSG );
                                            Toast.makeText(AddItemActivity.this, "Enter tax percentage ", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        else if ("0".equalsIgnoreCase(itemtaxprice) || !TextUtils.isValidPrice(itemtaxprice)) {
                                             updateItemTaxPercent.setError("Enter valid Price");
                                            Toast.makeText(AddItemActivity.this, "Enter valid tax Price", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        else if (itemtaxprice.startsWith("0")) {
                                             updateItemTaxPercent.setError("tax price should not starts with (0)");
                                            Toast.makeText(AddItemActivity.this, "tax price should not starts with (0)", Toast.LENGTH_SHORT).show();
                                            if (itemtaxprice.length() > 0) {
                                                updateItemTaxPercent.setText(updateItemTaxPercent.getText().toString().trim().substring(1));
                                                return;
                                            } else {
                                                updateItemTaxPercent.setText("");
                                                return;
                                            }
                                        }
                                        else if ("".equals ( itemlimitation )) {
                                             updateItemLimitation.setError ( Constant.REQUIRED_MSG );
                                            Toast.makeText(AddItemActivity.this, "Enter Item Limitation", Toast.LENGTH_SHORT).show();

                                            return;
                                        } else if ("0".equalsIgnoreCase(itemlimitation) || !TextUtils.isValidItemLimitation(itemlimitation)) {
                                             updateItemLimitation.setError("Enter valid Item Limitation");
                                            Toast.makeText(AddItemActivity.this, "Enter valid Item Limitation", Toast.LENGTH_SHORT).show();

                                            return;
                                        } else if (itemlimitation.startsWith("0")) {
                                             updateItemLimitation.setError("item limitation must be gereater than 0");
                                            Toast.makeText(AddItemActivity.this, "item limitation must be gereater than 0", Toast.LENGTH_SHORT).show();

                                            if (itemlimitation.length() > 0) {
                                                updateItemLimitation.setText(itemlimitation.substring(1));
                                                return;
                                            } else {
                                                updateItemLimitation.setText("");
                                                return;
                                            }
                                        }
                                        else if ("".equals ( itemBrandupdate )) {
                                             updateItemBrand.setError (Constant. REQUIRED_MSG );
                                            Toast.makeText(AddItemActivity.this, "Please enter itembrand ", Toast.LENGTH_SHORT).show();

                                            return;
                                        }
                                     else if (itemBrandupdate.length() < 3) {
                                         updateItemBrand.setError (Constant. REQUIRED_MSG );
                                         Toast.makeText(AddItemActivity.this, "Please enter itemBrand greater than 3 ", Toast.LENGTH_SHORT).show();

                                         return;
                                     }

                                        else if ((!android.text.TextUtils.isEmpty ( itemBrandupdate )
                                                && !TextUtils.validateAlphaNumericCharcters ( itemBrandupdate ))) {
                                             updateItemBrand.setError("Invalid Item Name");
                                             Toast.makeText(AddItemActivity.this, "Invalid Item brand Name", Toast.LENGTH_SHORT).show();
                                             return;

                                         }
                                        else if ("".equals ( itemDescription )) {
                                             updateDescription.setError (Constant. REQUIRED_MSG );
                                            Toast.makeText(AddItemActivity.this, "Please enter item description ", Toast.LENGTH_SHORT).show();

                                            return;
                                        }
                                     else if (itemDescription.length() < 3) {
                                         updateDescription.setError (Constant. REQUIRED_MSG );
                                         Toast.makeText(AddItemActivity.this, "Please enter item description greater than 3 ", Toast.LENGTH_SHORT).show();

                                         return;
                                     }

                                        else {

                                         fetchItem.addListenerForSingleValueEvent(new ValueEventListener() {
                                             @Override
                                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                 if (snapshot.exists()){
                                                     itemnamearraylist.clear();
                                                     for (DataSnapshot itemsnap :snapshot.getChildren()){
                                                         ItemDetails itemDetails = itemsnap.getValue(ItemDetails.class);
                                                         if (itemDetails.getItemId() != itemId) {
                                                             itemnamearraylist.add(itemDetails.getItemName());
                                                         }

                                                     }
                                                     Boolean ischeck = false ;
                                                     for (int i = 0; i < itemnamearraylist.size(); i++) {
                                                         if (itemName_updatename.equals(itemnamearraylist.get(i))) {
                                                             ischeck = true;
                                                         }
                                                     }
                                                     if (ischeck){
                                                         Toast.makeText(AddItemActivity.this, "This Item name already exists", Toast.LENGTH_SHORT).show();
                                                         updateitemName.setError (Constant. REQUIRED_MSG );
                                                     }

                                                     if (!ischeck) {

                                                         itemDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                             @Override
                                                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                 itemDetails = new ItemDetails();

                                                                 itemDetails.setCategoryName(itemdetailsPosition.getCategoryName());
                                                                 itemDetails.setCategoryid(itemdetailsPosition.getCategoryid());
                                                                 itemDetails.setGiftAmount(itemdetailsPosition.getGiftAmount());
                                                                 itemDetails.setItemApprovalStatus(itemdetailsPosition.getItemApprovalStatus());
                                                                 itemDetails.setItemAvailableQuantity(itemdetailsPosition.getItemAvailableQuantity());
                                                                 itemDetails.setItemBrand(itemBrandupdate);
                                                                 itemDetails.setItemBuyQuantity(itemdetailsPosition.getItemBuyQuantity());
                                                                 itemDetails.setItemCounter(itemdetailsPosition.getItemCounter());
                                                                 itemDetails.setItemDescription(itemDescription);
                                                                 itemDetails.setItemFeatures(itemdetailsPosition.getItemFeatures());
                                                                 itemDetails.setItemId(itemdetailsPosition.getItemId());
                                                                 itemDetails.setItemImage(itemdetailsPosition.getItemImage());
                                                                 itemDetails.setItemMaxLimitation(Integer.parseInt(itemlimitation));
                                                                 itemDetails.setItemMinLimitation(itemdetailsPosition.getItemMinLimitation());
                                                                 itemDetails.setItemName(itemName_updatename);
                                                                 itemDetails.setItemPrice(Integer.parseInt(itemprice_updatePrice));
                                                                 itemDetails.setItemQuantity(itemdetailsPosition.getItemQuantity());
                                                                 itemDetails.setItemStatus(itemdetailsPosition.getItemStatus());
                                                                 itemDetails.setMRP_Price(Integer.parseInt(itemmrpprice));
                                                                 itemDetails.setSellerId(itemdetailsPosition.getSellerId());
                                                                 itemDetails.setStoreAdress(itemdetailsPosition.getStoreAdress());

                                                                 if(itemdetailsPosition.getDescriptionUrl() !=null){
                                                                     itemDetails.setDescriptionUrl(itemdetailsPosition.getDescriptionUrl());
                                                                 }
                                                                 if (itemdetailsPosition.getStoreLogo()!=null){
                                                                     itemDetails.setStoreLogo(itemdetailsPosition.getStoreLogo());
                                                                 }
                                                                 itemDetails.setStoreName(itemdetailsPosition.getStoreName());
                                                                 itemDetails.setStorePincode(itemdetailsPosition.getStorePincode());
                                                                 itemDetails.setSubCategoryName(itemdetailsPosition.getSubCategoryName());

                                                                 itemDetails.setTax(Integer.parseInt(itemtaxprice));


                                                                 int basePrice = 0;
                                                                 basePrice = (100 * Integer.parseInt(itemprice_updatePrice)) / (100 + Integer.parseInt(itemtaxprice));

                                                                 itemDetails.setBasePrice(basePrice);
                                                                 int taxPriceAmount;
                                                                 taxPriceAmount = Integer.parseInt(itemprice_updatePrice) - basePrice;
                                                                 itemDetails.setTaxPrice(taxPriceAmount);

                                                                 itemDetails.setTotalItemQtyPrice(itemdetailsPosition.getTotalItemQtyPrice());
                                                                 itemDetails.setWishList(itemdetailsPosition.getWishList());

                                                                 if (itemdetailsPosition.getImageUriList() !=null){
                                                                     itemDetails.setImageUriList(itemdetailsPosition.getImageUriList());
                                                                 }


                                                                 itemDetailsRef.child(String.valueOf(itemId)).setValue(itemDetails);
                                                                 b.dismiss();
                                                                 Toast.makeText(AddItemActivity.this, "Updated Sucessfully", Toast.LENGTH_SHORT).show();


                                                             }

                                                             @Override
                                                             public void onCancelled(@NonNull DatabaseError error) {

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

                                }

                            });

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    b.dismiss();
                                }
                            });
                        }
                    }
                });

                dialog.show();
            }

        }
    });

}
                }else {
                    Toast.makeText(AddItemActivity.this, "No items available", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder ( AddItemActivity.this );
                final LayoutInflater inflater = getLayoutInflater ();
                final View alertdialogView = inflater.inflate ( R.layout.add_item_dialog, null );
                alertDialogBuilder.setView ( alertdialogView );

                b_choosefile = alertdialogView.findViewById ( R.id.choose_image );
                b_upload = alertdialogView.findViewById ( R.id.b_Upload );
                spinnerCategory = alertdialogView.findViewById ( R.id.e_catagory );

                image = alertdialogView.findViewById ( R.id.image );
                progressBar = alertdialogView.findViewById ( R.id.progressBar );
                selectedImageRecyclerView = alertdialogView.findViewById(R.id.selectedrecycler);
                multipleimagesview = alertdialogView.findViewById(R.id.selectedimages);
                //  spinnerSubCategory = alertdialogView.findViewById ( R.id.sub_catagory_spinner );
                name = alertdialogView.findViewById ( R.id.file_name );
                fixedprice = alertdialogView.findViewById ( R.id.item_price );
               // quantityUnit_spinner = alertdialogView.findViewById ( R.id.quantity_units );
                categoryNameEditText = alertdialogView.findViewById ( R.id.category );
                subcategoryNameEditText = alertdialogView.findViewById ( R.id.subcategory );
                itemTypeTxt=alertdialogView.findViewById(R.id.itemfeaturetype);
                brand = alertdialogView.findViewById ( R.id.itembrand );
                quantity = alertdialogView.findViewById ( R.id.quantity );
                description = alertdialogView.findViewById ( R.id.itemdescription );
                mrpPrice = alertdialogView.findViewById ( R.id.MRPprice );
                taxprice = alertdialogView.findViewById(R.id.tax);
                features = alertdialogView.findViewById ( R.id.itemfeature );
                itemLimitation = alertdialogView.findViewById ( R.id.itemLimitation );
                ImageView cancel = alertdialogView.findViewById ( R.id.newImage );
                autotextview= alertdialogView.findViewById(R.id.autotextview);
                b_multipleimages = alertdialogView.findViewById(R.id.choosemultipleimage);
                b = alertDialogBuilder.create ();
                b.show ();

               try {
                   ArrayAdapter<String> adapter=new ArrayAdapter<String>(AddItemActivity.this,android.R.layout.simple_spinner_item,Subcategory_list);

                   autotextview.setThreshold(2);
                   autotextview.setAdapter(adapter);

               }catch (Exception e){

               }


               b_multipleimages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bitmapList = new ArrayList<>();
                        pickimageStringList = new ArrayList<>();
                        pickimageList = new ArrayList<>();
                        openpickFileChooser();
                        countIndicator = 0;
                        startActivityForResult(intentImage, 100);

                    }
                });

               categoryRef.addValueEventListener ( new ValueEventListener () {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        categoryList.clear ();
                        categoryNamestring.clear ();
                        categoryNamestring.add ( "Select Category");


                        for ( DataSnapshot categorySnap : dataSnapshot.getChildren () ) {

                            CategoryDetails categoryDetails = categorySnap.getValue ( CategoryDetails.class );
                            List<UserDetails> sellerList = categoryDetails.getSellerList ();

                            Iterator sellerListIterator = sellerList.listIterator ();

                            while (sellerListIterator.hasNext ()) {
                                UserDetails seller = (UserDetails) sellerListIterator.next ();

                                if (sellerIdIntent.equalsIgnoreCase ( seller.getUserId () )) {

                                    sellerLogo = seller.getStoreLogo ();
                                    storeAddress = seller.getAddress ();
                                    sellerPinCode = seller.getPincode ();
                                    categoryList.add ( categoryDetails );
                                    categoryNamestring.add ( categoryDetails.getCategoryName () );
                                }
                            }
                        }
                        TextUtils.removeDuplicates(categoryNamestring);
                        if (categoryNamestring != null) {
                            categoryArrayAdapter = new ArrayAdapter<>
                                    ( AddItemActivity.this, android.R.layout.simple_spinner_item, categoryNamestring );
                            categoryArrayAdapter.setDropDownViewResource ( android.R.layout.simple_spinner_dropdown_item );
                            spinnerCategory.setAdapter ( categoryArrayAdapter );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                        selectedCategoryName = parent.getItemAtPosition(position).toString();

                        categoryNameEditText.setText(selectedCategoryName);

                        if (categoryNameEditText.getText().toString() != "Select Category" && !categoryNameEditText.equals("")) {

                            categoryRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot categoryID : dataSnapshot.getChildren()) {
                                        CategoryDetails categoryDetails = categoryID.getValue(CategoryDetails.class);

                                        if (categoryDetails.getCategoryName().equalsIgnoreCase(categoryNameEditText.getText().toString())) {
                                            SelectedcategoryID = categoryDetails.getCategoryid();
                                          //  Toast.makeText(AddItemActivity.this, ""+SelectedcategoryID, Toast.LENGTH_SHORT).show();
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
                    public void onNothingSelected(AdapterView<?> parent) {

                    }

                });








                b_choosefile.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        openFileChooser ();
                        startActivityForResult ( intentImage, 0 );
                    }
                });



                b_upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemName = name.getText ().toString ().trim ();
                        itemPrice = fixedprice.getText ().toString ().trim ();
                        itemBrand = brand.getText ().toString ().trim ();
                        quantityString = quantity.getText ().toString ().trim ();
                        itemQuantityUnitsString = selectedItemQuantityunits;
                        itemDescription = description.getText ().toString ();
                        itemMRPprice = mrpPrice.getText ().toString ().trim ();
                        itemtaxprice = taxprice.getText().toString().trim();
                        itemFeatures = features.getText ().toString ().trim ();
                    //    subcategory = features.getText().toString().trim();
                        itemLimitationString = itemLimitation.getText ().toString ().trim ();
                        UploadFile ();
                    }
                });

                cancel.setOnClickListener ( new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {

                        b.dismiss ();
                    }
                });


            }
        });
    }




    private void openpickFileChooser() {
        intentImage = new Intent();
        intentImage.setType("image/*");

        intentImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intentImage.setAction(Intent.ACTION_GET_CONTENT);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId ();

        if (id == R.id.sellerProfile) {

            Intent intent = new Intent ( AddItemActivity.this, SellerProfileActivity.class );
            intent.setFlags ( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
            startActivity ( intent );
        } else if (id == R.id.addItem) {

            Intent intent = new Intent ( AddItemActivity.this, AddItemActivity.class );
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

            Intent intent = new Intent ( AddItemActivity.this, OrderHistoryActivity.class );
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

            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog ( AddItemActivity.this );
            bottomSheetDialog.setContentView ( R.layout.logout_confirmation );
            Button logout = bottomSheetDialog.findViewById ( R.id.logout );
            Button stayinapp = bottomSheetDialog.findViewById ( R.id.stayinapp );

            bottomSheetDialog.show ();
            bottomSheetDialog.setCancelable ( false );

            logout.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {

                    if (!((Activity) AddItemActivity.this).isFinishing()) {

                        SharedPreferences sharedPreferences = getSharedPreferences ( "LOGIN", MODE_PRIVATE );
                        SharedPreferences.Editor editor = sharedPreferences.edit ();
                        editor.clear ();
                        editor.commit ();

                        Intent intent = new Intent ( AddItemActivity.this, OtpRegister.class );
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


    private void UploadFile() {

        if (spinnerCategory.getSelectedItemPosition () == 0) {
            Toast.makeText ( this, "Select the category", Toast.LENGTH_SHORT ).show ();
        }
        else if (autotextview.getText().toString().equals("")) {
            autotextview.setError(Constant. REQUIRED_MSG );
            Toast.makeText(this, "Please enter subcategory ", Toast.LENGTH_SHORT).show();
            return;
        }else if (autotextview.getText().toString().length() < 3){
            autotextview.setError(Constant. REQUIRED_MSG );
            Toast.makeText(this, "Subcategory name must be greater than 3", Toast.LENGTH_SHORT).show();
            return;
        }
         else if ("".equals ( itemName )) {
            name.setError (Constant. REQUIRED_MSG );
            Toast.makeText(this, "Please enter Itemname ", Toast.LENGTH_SHORT).show();

            return;
        }
        else if (itemName.length() < 3){
            name.setError(Constant. REQUIRED_MSG );
            Toast.makeText(this, "Subcategory name must be greater than 3", Toast.LENGTH_SHORT).show();
            return;
        }


         else if ((!android.text.TextUtils.isEmpty ( itemName )
                && !TextUtils.validateAlphaNumericCharcters ( itemName ))) {
            name.setError ( "Invalid Item Name" );
            Toast.makeText(this, "Invalid Item Name", Toast.LENGTH_SHORT).show();

            return;

        }
         /*else if ("".equals ( quantityString )) {
            quantity.setError (Constant. REQUIRED_MSG );

            return;
        }else if (quantity.getText().toString().startsWith("0")) {
            quantity.setError("quantity  should not starts with (0)");
            if (quantity.getText().toString().trim().length() > 0) {
                quantity.setText(quantity.getText().toString().trim().substring(1));
                return;
            } else {
                quantity.setText("");
                return;
            }
        }*/
        else if ("".equals ( itemPrice )) {
            fixedprice.setError ( Constant.REQUIRED_MSG );
            Toast.makeText(this, "Enter  Item Price", Toast.LENGTH_SHORT).show();

            return;
        } else if ("0".equalsIgnoreCase(fixedprice.getText().toString()) || !TextUtils.isValidPrice(fixedprice.getText().toString())) {
            fixedprice.setError("Enter valid Price");
            Toast.makeText(this, "Enter valid Item Price", Toast.LENGTH_SHORT).show();

            return;
        }else if (fixedprice.getText().toString().startsWith("0")) {
            fixedprice.setError("fixed price should not starts with (0)");
            Toast.makeText(this, "fixed price should not starts with (0)", Toast.LENGTH_SHORT).show();

            if (fixedprice.getText().toString().trim().length() > 0) {
                fixedprice.setText(fixedprice.getText().toString().trim().substring(1));
                return;
            } else {
                fixedprice.setText("");
                return;
            }
        } else if ("".equals ( itemMRPprice )) {
            mrpPrice.setError ( Constant.REQUIRED_MSG );
            Toast.makeText(this, "Enter Mrp Price", Toast.LENGTH_SHORT).show();

            return;
        }else if ("0".equalsIgnoreCase(mrpPrice.getText().toString()) || !TextUtils.isValidPrice(mrpPrice.getText().toString())) {
            mrpPrice.setError("Enter valid Price");
            Toast.makeText(this, "Enter valid Mrp Price", Toast.LENGTH_SHORT).show();

            return;
        }
         else if (mrpPrice.getText().toString().startsWith("0")) {
            mrpPrice.setError("Mrp price should not starts with (0)");
            Toast.makeText(this, "Mrp price should not starts with (0)", Toast.LENGTH_SHORT).show();

            if (mrpPrice.getText().toString().trim().length() > 0) {
                mrpPrice.setText(mrpPrice.getText().toString().trim().substring(1));
                return;
            } else {
                mrpPrice.setText("");
                return;
            }
        } else  if(Integer.parseInt(fixedprice.getText().toString())>Integer.parseInt(mrpPrice.getText().toString()))
        {
            fixedprice.setError("Fixed price should be less than MRP Price");
            Toast.makeText(this, "Fixed price should be less than MRP Price", Toast.LENGTH_SHORT).show();
            return;
        }
        else if ("".equals ( itemtaxprice )) {
            taxprice.setError ( Constant.REQUIRED_MSG );
            Toast.makeText(this, "Enter tax Percentage ", Toast.LENGTH_SHORT).show();
            return;
        }
        else if ("0".equalsIgnoreCase(taxprice.getText().toString()) || !TextUtils.isValidPrice(taxprice.getText().toString())) {
            taxprice.setError("Enter valid Percentage");
            Toast.makeText(this, "Enter valid tax Percentage", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (taxprice.getText().toString().startsWith("0")) {
            taxprice.setError("tax Percentage should not starts with (0)");
            Toast.makeText(this, "tax Percentage should not starts with (0)", Toast.LENGTH_SHORT).show();
            if (taxprice.getText().toString().trim().length() > 0) {
                taxprice.setText(taxprice.getText().toString().trim().substring(1));
                return;
            } else {
                taxprice.setText("");
                return;
            }
        }
        else if ("".equals ( itemLimitationString )) {
            itemLimitation.setError ( Constant.REQUIRED_MSG );
            Toast.makeText(this, "Enter Item Limitation", Toast.LENGTH_SHORT).show();

            return;
        } else if ("0".equalsIgnoreCase(itemLimitation.getText().toString()) || !TextUtils.isValidItemLimitation(itemLimitation.getText().toString())) {
            itemLimitation.setError("Enter valid Item Limitation");
            Toast.makeText(this, "Enter valid Item Limitation", Toast.LENGTH_SHORT).show();

            return;
        } else if (itemLimitation.getText().toString().startsWith("0")) {
            itemLimitation.setError("item limitation must be gereater than 0");
            Toast.makeText(this, "item limitation must be greater than 0", Toast.LENGTH_SHORT).show();

            if (itemLimitation.getText().toString().trim().length() > 0) {
                itemLimitation.setText(itemLimitation.getText().toString().trim().substring(1));
                return;
            } else {
                itemLimitation.setText("");
                return;
            }
        }
        else if ("".equals ( itemBrand )) {
            brand.setError (Constant. REQUIRED_MSG );
            Toast.makeText(this, "Please enter itembrand ", Toast.LENGTH_SHORT).show();

            return;
        }
        else if (itemBrand.length() < 3){
            brand.setError(Constant. REQUIRED_MSG );
            Toast.makeText(this, "Item Brand   must be greater than 3", Toast.LENGTH_SHORT).show();
            return;
        }


        else if ((!android.text.TextUtils.isEmpty ( itemBrand ) && !TextUtils.validateAlphaNumericCharcters ( itemBrand ))) {
            brand.setError ( "Invalid Item Name" );
            Toast.makeText(this, "Invalid Item brand Name", Toast.LENGTH_SHORT).show();
            return;

        }

        else if ("".equals ( itemDescription )) {
            description.setError (Constant. REQUIRED_MSG );
            Toast.makeText(this, "Please enter item description ", Toast.LENGTH_SHORT).show();

            return;
        }
        else if (itemDescription.length() < 3){
            description.setError(Constant. REQUIRED_MSG );
            Toast.makeText(this, "Description field must be greater than 3", Toast.LENGTH_SHORT).show();
            return;
        }

        else if (mimageuri != null) {
          Boolean ischeck = false ;
            for (int i = 0; i < itemNameList.size(); i++) {
                if (itemName.equals(itemNameList.get(i))) {
                    ischeck = true;
                    //Toast.makeText(this, "" + itemNameList.get(i), Toast.LENGTH_SHORT).show();
                }

            }
            if (ischeck){
                Toast.makeText(this, "This Item name already exists", Toast.LENGTH_SHORT).show();
                name.setError (Constant. REQUIRED_MSG );

            }


            if (!ischeck){




            if (itemLimitationString == null || "".equals(itemLimitationString)) {
                itemLimitationString = "10";
            }
            if (itemBrand.equals("")) {
                itemBrand = "No Brand";
            }

            itemDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    b_choosefile.setVisibility(View.INVISIBLE);
                    b_upload.setVisibility(View.INVISIBLE);
                    itemDetails = new ItemDetails();
                    itemDetails.setItemName(itemName);
                    if ("".equals(quantity.getText().toString())) {
                     //   Toast.makeText(AddItemActivity.this, "valid quantity", Toast.LENGTH_SHORT).show();
                    } else {
                        itemDetails.setItemQuantity(Integer.parseInt(quantity.getText().toString().trim()));
                    }
                    itemDetails.setItemStatus(Constant.ACTIVE_STATUS);
                    itemDetails.setItemApprovalStatus("Approved");
                    itemDetails.setStoreName(storeNameIntent);
                    itemDetails.setSellerId(sellerIdIntent);
                    itemDetails.setItemRating(itemRating);
                    itemDetails.setItemReview(itemReview);
                    itemDetails.setCategoryName(categoryNameEditText.getText().toString());
                    itemDetails.setCategoryid(SelectedcategoryID);
                    itemDetails.setSubCategoryName(autotextview.getText().toString());
                    itemDetails.setItemPrice(Integer.parseInt(itemPrice));
                    itemDetails.setItemBrand(itemBrand);
                    itemDetails.setQuantityUnits(itemQuantityUnitsString);
                    itemDetails.setMRP_Price(Integer.parseInt(itemMRPprice));
                    itemDetails.setTax(Integer.parseInt(itemtaxprice));
                    itemDetails.setItemDescription(itemDescription);
                    itemDetails.setStoreAdress(storeAddress);
                    itemDetails.setItemFeatures(itemFeatures);


                    int basePrice = 0;
                    basePrice = (100 * Integer.parseInt(itemPrice)) / (100 + Integer.parseInt(itemtaxprice));

                    itemDetails.setBasePrice(basePrice);
                    int taxPriceAmount;
                    taxPriceAmount = Integer.parseInt(itemPrice) - basePrice;
                    itemDetails.setTaxPrice(taxPriceAmount);


                    if ("".equals(itemLimitationString)) {
                     //   Toast.makeText(AddItemActivity.this, "valid limitation", Toast.LENGTH_SHORT).show();
                    } else {
                        itemDetails.setItemMaxLimitation(Integer.parseInt(itemLimitationString));
                    }
                    itemDetails.setItemStatus(Constant.ACTIVE_STATUS);
                    itemDetails.setItemApprovalStatus("Approved");
                    itemDetails.setStoreName(storeNameIntent);
                    itemDetails.setSellerId(sellerIdIntent);
                    itemDetails.setItemRating(itemRating);
                    itemDetails.setItemReview(itemReview);
                    itemDetails.setCategoryName(categoryNameEditText.getText().toString());
                    itemDetails.setSubCategoryName(autotextview.getText().toString());


                    if (sellerLogo != null) {
                        itemDetails.setStoreLogo(sellerLogo);
                    } else {
                        itemDetails.setStoreLogo("");
                    }
                    itemDetails.setStorePincode(sellerPinCode);

                    StorageReference imageFileStorageRef = itemStorageRef.child(Constant.ITEM_IMAGE_STORAGE
                            + System.currentTimeMillis() + "." + getExtenstion(mimageuri));

                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mimageuri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] data = baos.toByteArray();

                    final SweetAlertDialog pDialog = new SweetAlertDialog(AddItemActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#1898ED"));
                    pDialog.setTitleText("Uploading Image.....");
                    pDialog.setCancelable(false);
                    pDialog.show();


                    mItemStorageTask = imageFileStorageRef.putBytes(data).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @SuppressLint("ResourceType")
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressBar.setProgress(0);
                                        }
                                    }, 5000);

                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!urlTask.isSuccessful()) ;
                                    final Uri downloadUrl = urlTask.getResult();


                                    RequestOptions requestOptions = new RequestOptions();
                                    requestOptions.placeholder(R.mipmap.ic_launcher_new);
                                    requestOptions.error(R.mipmap.ic_launcher_new);
                                    Glide.with(AddItemActivity.this).setDefaultRequestOptions(requestOptions).load(itemDetails.getItemImage()).fitCenter().into(image);


                                    if (clipDataCount > 0) {
                                        for (uploadCount = 0; uploadCount < pickimageList.size(); uploadCount++) {

                                            Bitmap bmp = null;
                                            try {
                                                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), pickimageList.get(uploadCount));
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bmp.compress(Bitmap.CompressFormat.PNG, 25, baos);
                                            byte[] data = baos.toByteArray();

                                            Uri individualImage = pickimageList.get(uploadCount);
                                            StorageReference ImageFolder = FirebaseStorage.getInstance().getReference().child("ItemImages");
                                            final StorageReference imageName = ImageFolder.child("Image" + individualImage.getLastPathSegment());

                                            imageName.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    String url = String.valueOf(taskSnapshot);
                                                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                                    while (!urlTask.isSuccessful()) ;
                                                    Uri url1 = urlTask.getResult();


                                                    pickimageStringList.add(url1.toString());

                                                    multipleimagesview.setImageResource(R.drawable.b_chooseimage);

                                                    itemDetailsRef.child(String.valueOf(itemDetails.getItemId())).child("imageUriList").setValue(pickimageStringList);
                                                    if (pickimageStringList != null && !pickimageStringList.isEmpty()) {


                                                        itemDetailsRef.child(String.valueOf(itemDetails.getItemId())).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {

                                                                    ItemDetails itemDetails = dataSnapshot.getValue(ItemDetails.class);
                                                                    itemDetails.getImageUriList();
                                                                    if (clipDataCount == itemDetails.getImageUriList().size()) {
                                                                        Toast.makeText(AddItemActivity.this, " Item Sucessfully Uploaded", Toast.LENGTH_SHORT).show();
                                                                        pDialog.dismiss();
                                                                        b.dismiss();
                                                                        Intent intent = new Intent(AddItemActivity.this, AddItemActivity.class);
                                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                                        startActivity(intent);
                                                                    } else {
                                                                        if (!((Activity) AddItemActivity.this).isFinishing()) {
                                                                            pDialog.show();
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

                                            });


                                        }
                                    } else {
                                        pDialog.dismiss();
                                        b.dismiss();
                                        Intent intent = new Intent(AddItemActivity.this, AddItemActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                        startActivity(intent);
                                    }


                                    itemDetails.setItemImage(downloadUrl.toString());
                                    itemDetails.setWishList(Constant.BOOLEAN_FALSE);
                                    onTransaction(itemDetailsRef);


                                    if (!((Activity) AddItemActivity.this).isFinishing()) {
                                        b.dismiss();
                                    }
                                    b_choosefile.setVisibility(View.VISIBLE);
                                    b_upload.setVisibility(View.VISIBLE);

                                    name.setText("");
                                    fixedprice.setText("");
                                    brand.setText("");
                                    quantity.setText("");
                                    description.setText("");
                                    mrpPrice.setText("");
                                    features.setText("");
                                    spinnerCategory.setSelection(0);
                                    //    spinnerSubCategory.setSelection(0);
                                    itemLimitation.setText("");
                                    image.setImageResource(R.drawable.b_chooseimage);
                                    categoryNameEditText.setText("");
                                    mimageuri = null;
                                    b.dismiss();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddItemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress((int) progress);
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        } else {
            Toast.makeText ( AddItemActivity.this, Constant.PLEASE_SELECT_IMAGE, Toast.LENGTH_LONG ).show ();
            b_choosefile.setVisibility ( View.VISIBLE );
            b_upload.setVisibility ( View.VISIBLE );
        }
    }


    private String getExtenstion(Uri uri) {
        ContentResolver contentResolver = getContentResolver ();
        MimeTypeMap mime = MimeTypeMap.getSingleton ();
        return mime.getExtensionFromMimeType ( contentResolver.getType ( uri ) );
    }

    private void openFileChooser() {
        intentImage = new Intent ();
        intentImage.setType ( "image/*" );
        intentImage.setAction ( Intent.ACTION_GET_CONTENT );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult ( requestCode, resultCode, data );

        switch (requestCode) {
            case 0:
                PICK_IMAGE_REQUEST = 0;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData () != null) {
                    mimageuri = data.getData ();
                    Glide.with ( AddItemActivity.this ).load ( mimageuri ).into ( image );
                }
                break;


                case 1:
                PICK_IMAGE_REQUEST = 1;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData () != null) {
                    mimageuriUpdate = data.getData ();
                    Glide.with ( AddItemActivity.this ).load ( mimageuriUpdate ).into ( image_Item );
                }
                break;

            case 2:
                PICK_IMAGE_REQUEST = 2;
                if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    updateImageUri = data.getData();
                    Glide.with(AddItemActivity.this).load(updateImageUri).into(updatedImage);
                }
                break;


            case 100:
                PICK_IMAGE_REQUEST = 100;

                if (requestCode == PICK_IMAGE_REQUEST) {
                    if (resultCode == RESULT_OK) {
                        if (data.getData() != null) {

                            clipDataCount = 1;
                            multipleimageUri = data.getData();
                            pickimageList.add(multipleimageUri);

                            try {
                                InputStream is = getContentResolver().openInputStream(multipleimageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(is);
                                bitmapList.add(bitmap);


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        } else if (data.getClipData() != null) {

                            clipDataCount = data.getClipData().getItemCount();
                            Log.e("clipDataCount",""+clipDataCount);

                            int currentImageSelectCount = 0;


                         Log.e("ClipDatacount", String.valueOf(clipDataCount));
                                while (currentImageSelectCount < clipDataCount) {

                                    multipleimageUri = data.getClipData().getItemAt(currentImageSelectCount).getUri();
                                    pickimageList.add(multipleimageUri);
                                    currentImageSelectCount = currentImageSelectCount + 1;

                                }

                                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                multipleimageUri = data.getClipData().getItemAt(i).getUri();
                                try {
                                    InputStream is = getContentResolver().openInputStream(multipleimageUri);
                                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                                    bitmapList.add(bitmap);

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }


                        } else {
                            multipleimageUri = data.getData();
                            try {


                                InputStream is = getContentResolver().openInputStream(multipleimageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(is);
                                bitmapList.add(bitmap);


                            } catch (FileNotFoundException e) {
                                e.printStackTrace();

                            }
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (final Bitmap b : bitmapList) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {


                                            if (countIndicator == 2) {


                                                System.out.println("SIZEEEOFSLIDER  " + bitmapList.size());


                                                ImageSelectedAdapter imageListAdapter = new ImageSelectedAdapter(AddItemActivity.this, bitmapList);
                                                RecyclerView.LayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());

                                                selectedimagesUpdate.setLayoutManager(RecyclerViewLayoutManager);
                                                if (bitmapList.size() != 0 && !bitmapList.isEmpty()) {
                                                    LinearLayoutManager HorizontalLayout
                                                            = new LinearLayoutManager(
                                                            AddItemActivity.this,
                                                            LinearLayoutManager.VERTICAL,
                                                            false);
                                                    selectedimagesUpdate.setLayoutManager(HorizontalLayout);
                                                    imageListAdapter.notifyDataSetChanged();
                                                    selectedimagesUpdate.setAdapter(imageListAdapter);

                                                    System.out.
                                                            println("SIZEEEOFSLIDER  " + bitmapList.size());

                                                }
                                                multipleSelectionImages.setImageBitmap(b);

                                            }

                                            if (countIndicator == 0) {


                                                ImageSelectedAdapter imageListAdapter = new ImageSelectedAdapter(AddItemActivity.this, bitmapList);
                                                RecyclerView.LayoutManager RecyclerViewLayoutManager = new LinearLayoutManager(getApplicationContext());

                                                selectedImageRecyclerView.setLayoutManager(RecyclerViewLayoutManager);
                                                if (bitmapList.size() != 0 && !bitmapList.isEmpty()) {
                                                    LinearLayoutManager HorizontalLayout = new LinearLayoutManager(
                                                            AddItemActivity.this,
                                                            LinearLayoutManager.HORIZONTAL,
                                                            false);
                                                    selectedImageRecyclerView.setLayoutManager(HorizontalLayout);
                                                    imageListAdapter.notifyDataSetChanged();
                                                    selectedImageRecyclerView.setAdapter(imageListAdapter);

                                                    System.out.println("SIZEEEOFSLIDER  " + bitmapList.size());

                                                }

                                                multipleimagesview.setImageBitmap(b);
                                            }


                                        }
                                    });
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        }).start();
                    } else {
                        Toast.makeText(this, "select Image", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }



    private void onTransaction(DatabaseReference postRef) {

        postRef.runTransaction(new Transaction.Handler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                increamentId = Math.toIntExact(mutableData.getChildrenCount());
                if (increamentId == null) {
                    itemDetails.setItemId(1);
                    mutableData.child(String.valueOf(1)).setValue(itemDetails);
                    return Transaction.success(mutableData);
                }
                else
                {
                    increamentId=increamentId+1;
                    itemDetails.setItemId(increamentId);
                    itemDetails.setItemId(increamentId);
                    mutableData.child(String.valueOf(increamentId)).setValue(itemDetails);
                    return Transaction.success(mutableData);
                }
            }
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot)
            {

            }
        });
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
        if (!((Activity) AddItemActivity.this).isFinishing()) {
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