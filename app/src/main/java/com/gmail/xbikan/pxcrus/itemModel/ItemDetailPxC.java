
package com.gmail.xbikan.pxcrus.itemModel;

import java.util.HashMap;
import java.util.Map;


public class ItemDetailPxC {

    private String line1;

    private String line2;

    private Availability availability;

    private String productId;

    private String currency;

    private Integer unit;

    private String price;

    private Boolean erroneous;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public String getLine1() {
        return line1;
    }


    public void setLine1(String line1) {
        this.line1 = line1;
    }


    public String getLine2() {
        return line2;
    }


    public void setLine2(String line2) {
        this.line2 = line2;
    }


    public Availability getAvailability() {
        return availability;
    }


    public void setAvailability(Availability availability) {
        this.availability = availability;
    }


    public String getProductId() {
        return productId;
    }


    public void setProductId(String productId) {
        this.productId = productId;
    }


    public String getCurrency() {
        return currency;
    }


    public void setCurrency(String currency) {
        this.currency = currency;
    }


    public Integer getUnit() {
        return unit;
    }


    public void setUnit(Integer unit) {
        this.unit = unit;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Boolean getErroneous() {
        return erroneous;
    }

    public void setErroneous(Boolean erroneous) {
        this.erroneous = erroneous;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
