package com.gmail.xbikan.pxcrus.retrofit;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by xbika on 11-Feb-18.
 */
@Root (name = "Valute")
public class Valute {
    @Attribute (name = "ID")
    private String iD;
    @Element (name = "NumCode")
    private String numCode;
    @Element (name = "CharCode")
    private String charCode;
    @Element (name = "Nominal")
    private String nominal;
    @Element (name = "Name")
    private String name;
    @Element (name = "Value")
    private String value;

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getNumCode() {
        return numCode;
    }

    public void setNumCode(String numCode) {
        this.numCode = numCode;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
