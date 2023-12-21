package com.smiligence.etmsellerapk.bean;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetails {
    String categoryid;
    String categoryName;
    String categoryImage;
    String subCategoryId;
    String subCategoryName;
    String subCategoryImage;
    String categoryCreatedDate;
    String subCategoryCreatedDate;
    String categoryPriority;
    private Boolean value;

    List<UserDetails> SellerList = new ArrayList<>();


    public List<UserDetails> getSellerList() {
        return SellerList;
    }

    public void setSellerList(List<UserDetails> sellerList) {
        SellerList = sellerList;
    }

    public String getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(String categoryid) {
        this.categoryid = categoryid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getSubCategoryImage() {
        return subCategoryImage;
    }

    public void setSubCategoryImage(String subCategoryImage) {
        this.subCategoryImage = subCategoryImage;
    }

    public String getCategoryCreatedDate() {
        return categoryCreatedDate;
    }

    public void setCategoryCreatedDate(String categoryCreatedDate) {
        this.categoryCreatedDate = categoryCreatedDate;
    }

    public String getSubCategoryCreatedDate() {
        return subCategoryCreatedDate;
    }

    public void setSubCategoryCreatedDate(String subCategoryCreatedDate) {
        this.subCategoryCreatedDate = subCategoryCreatedDate;
    }

    public String getCategoryPriority() {
        return categoryPriority;
    }

    public void setCategoryPriority(String categoryPriority) {
        this.categoryPriority = categoryPriority;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
