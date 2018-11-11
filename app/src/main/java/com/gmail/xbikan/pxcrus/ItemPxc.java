package com.gmail.xbikan.pxcrus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.List;

/**
 * Класс для каждой позиции
 */

public class ItemPxc {

    private String articul;
    private String name;
    private String costUnit;
    private Integer packageMin;
    private String priceGroup;
    private float basicPrice;
    private float discountedPrice;
    private float discount;
    private String customerGroup;
    private boolean isInEuro;
    private boolean isFavourite;
    private boolean isSpecialPrice;
    private boolean hasSpecialQuant;
    private int specialQuant;

    public boolean getisSpecialPrice() {
        return isSpecialPrice;
    }

    public void setisSpecialPrice(boolean isSpecialPrice) {
        this.isSpecialPrice = isSpecialPrice;
    }

    public boolean getIsInEuro() {
        return isInEuro;
    }

    public void setIsInEuro(boolean isInEuro) {
        this.isInEuro = isInEuro;
    }

    public ItemPxc() {

    }

    public String getArticul() {
        return articul;
    }

    public void setArticul(String articul) {
        this.articul = articul;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCostUnit() {
        return costUnit;
    }

    public void setCostUnit(String costUnit) {
        this.costUnit = costUnit;
    }

    public Integer getPackageMin() {
        return packageMin;
    }

    public void setPackageMin(Integer packageMin) {
        this.packageMin = packageMin;
    }

    public String getPriceGroup() {
        return priceGroup;
    }

    public void setPriceGroup(String priceGroup) {
        this.priceGroup = priceGroup;
    }

    public float getBasicPrice() {
        return basicPrice;
    }

    public void setBasicPrice(float basicPrice) {
        this.basicPrice = basicPrice;
    }

    public float getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(float discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getCustomerGroup() {
        return customerGroup;
    }

    public void setCustomerGroup(String customerGroup) {
        this.customerGroup = customerGroup;
    }

    public boolean getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(boolean isFavourite) {
        this.isFavourite = isFavourite;
    }

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    static public ItemPxc create(Object serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson((JsonElement) serializedData, ItemPxc.class);
    }

    public boolean isHasSpecialQuant() {
        return hasSpecialQuant;
    }

    public void setHasSpecialQuant(boolean hasSpecialQuant) {
        this.hasSpecialQuant = hasSpecialQuant;
    }

    public int getSpecialQuant() {
        return specialQuant;
    }

    public void setSpecialQuant(int specialQuant) {
        this.specialQuant = specialQuant;
    }
}
