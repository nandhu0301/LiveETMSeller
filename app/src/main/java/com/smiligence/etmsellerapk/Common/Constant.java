package com.smiligence.etmsellerapk.Common;

public class Constant {


    public static String CUSTOMER_NAME_PATTERN = "^[a-zA-z]+([\\s][a-zA-Z]+)*$";
    public static String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+(\\.+[a-z]+)?";
    public static final String ITEM_PRICE_PATTERN="[0-9]*$";
    public  static  String IFSC_PATTERN="^[A-Z]{4}0[A-Z0-9]{6}$";
    public static  final   String GST_NUMBER_PATTERN="[0-9a-zA-Z]{15}";
    public static String ITEM_NAME_PATTERN = "^[a-zA-Z0-9]+([\\s][a-zA-Z0-9]+)*$";
    public static final String ITEM_LIMITATION_PATTERN="[0-9]*$";
    public static  String BILLED_DATE_COLUMN="paymentDate";
    public static String DATE_FORMAT_YYYYMD = "yyyy-M-d";
    public static String FORMATED_BILLED_DATE="formattedDate";
    public static String DATE_TIME_FORMAT = "MMMM dd, yyyy HH:mm:ss";
    public static String DATE_FORMAT = "MMMM dd, yyyy";
    public static String DATE_MONTH_FORMAT = "dd-MM-YYYY";
    public static String MAINTAIN_ITEMS="Maintain Items";
    public static String STORE_PROFILE="Store Profile";
    public static String CATEGORY  ="Category";
    public  static  String PRODUCT_DETAILS_TABLE="ProductDetails";
    public  static  String ITEMDETAILS_TABLE="ItemDetails";
    public static String ITEM_IMAGE_STORAGE = "ItemDetails/";
    public static String REQUIRED_MSG = "Required";
    public static String ACTIVE_STATUS = "Active";
    public static boolean BOOLEAN_FALSE = false;
    public static boolean BOOLEAN_TRUE = true;
    public static String PLEASE_SELECT_IMAGE = "Please select an Image!!";
    public static  String INACTIVE_STATUS="Inactive";
    public static String DASHBOARD="DashBoard";
    public static String AddDESCRIPTION="Description";
    public static String STORE_TIMINGS="Store Timings";
    public static String CONTACT_SUPPORT="Contact Support";
    public static String ORDER_DETAILS_FIREBASE_TABLE = "OrderDetails";
    public static String ITEM_NAME_COLUMN = "itemName";
    public static String REQUIRED = "Required";
    public static String MAINTAIN_ORDERS="Maintain Orders";
    public static String PAYMENTS = "Payments";
    public static String SELLERLOGIN_DETAILS="SellerLoginDetails";
    public static String SELLER_DETAILS_TABLE = "SellerLoginDetails";
    public static String SELLER_PAYMENTS="SellerPayments";
    public static String PAYMENT_SETTLEMENT="Payment Settlement";
}
