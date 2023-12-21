package com.smiligence.etmsellerapk.bean;

import java.util.ArrayList;

public class OrderDetails  implements Comparable< OrderDetails>{

    String orderDay;
    String orderStatus;
    String orderDeliverCodeForSeller;
    int totalAmount;
    int totalItem;
    int singletotalitemAmount;
    String orderTime;
    String storeName;
    String paymentId;
    String orderIdfromPaymentGateway;
    String orderId;
    int paymentamount;
    String paymentDate;
    String paymentType;
    String fullName;
    String shippingPincode;
    String storePincode;
    String shippingaddress;
    ItemDetails itemDetails;
    String assignedTo;
    String phoneNumber;
    String customerName;
    String customerId;
    String customerPhoneNumber;
    String categoryType;
    String orderDeliveryType;
    int tipAmount;
    String orderCreateDate;
    String notificationStatusForSeller;
    String formattedDate;
    ArrayList<ItemDetails> itemDetailList=new ArrayList<ItemDetails> ();
     String orderNumberForFinancialYearCalculation ;


    public String getOrderNumberForFinancialYearCalculation() {
        return orderNumberForFinancialYearCalculation;
    }

    public void setOrderNumberForFinancialYearCalculation(String orderNumberForFinancialYearCalculation) {
        this.orderNumberForFinancialYearCalculation = orderNumberForFinancialYearCalculation;
    }




    public ArrayList<ItemDetails> getItemDetailList() {
        return itemDetailList;
    }

    public void setItemDetailList(ArrayList<ItemDetails> itemDetailList) {
        this.itemDetailList = itemDetailList;
    }

    public int getSingletotalitemAmount() {
        return singletotalitemAmount;
    }

    public void setSingletotalitemAmount(int singletotalitemAmount) {
        this.singletotalitemAmount = singletotalitemAmount;
    }

    public String getOrderDay() {
        return orderDay;
    }

    public void setOrderDay(String orderDay) {
        this.orderDay = orderDay;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDeliverCodeForSeller() {
        return orderDeliverCodeForSeller;
    }

    public void setOrderDeliverCodeForSeller(String orderDeliverCodeForSeller) {
        this.orderDeliverCodeForSeller = orderDeliverCodeForSeller;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderIdfromPaymentGateway() {
        return orderIdfromPaymentGateway;
    }

    public void setOrderIdfromPaymentGateway(String orderIdfromPaymentGateway) {
        this.orderIdfromPaymentGateway = orderIdfromPaymentGateway;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getPaymentamount() {
        return paymentamount;
    }

    public void setPaymentamount(int paymentamount) {
        this.paymentamount = paymentamount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShippingPincode() {
        return shippingPincode;
    }

    public void setShippingPincode(String shippingPincode) {
        this.shippingPincode = shippingPincode;
    }

    public String getStorePincode() {
        return storePincode;
    }

    public void setStorePincode(String storePincode) {
        this.storePincode = storePincode;
    }

    public String getShippingaddress() {
        return shippingaddress;
    }

    public void setShippingaddress(String shippingaddress) {
        this.shippingaddress = shippingaddress;
    }

    public ItemDetails getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ItemDetails itemDetails) {
        this.itemDetails = itemDetails;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getOrderDeliveryType() {
        return orderDeliveryType;
    }

    public void setOrderDeliveryType(String orderDeliveryType) {
        this.orderDeliveryType = orderDeliveryType;
    }

    public int getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(int tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getOrderCreateDate() {
        return orderCreateDate;
    }

    public void setOrderCreateDate(String orderCreateDate) {
        this.orderCreateDate = orderCreateDate;
    }

    public String getNotificationStatusForSeller() {
        return notificationStatusForSeller;
    }

    public void setNotificationStatusForSeller(String notificationStatusForSeller) {
        this.notificationStatusForSeller = notificationStatusForSeller;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    @Override
    public int compareTo(OrderDetails orderDetails) {
        return 0;
    }
}
