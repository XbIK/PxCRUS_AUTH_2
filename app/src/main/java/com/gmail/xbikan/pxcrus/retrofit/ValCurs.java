package com.gmail.xbikan.pxcrus.retrofit;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by xbika on 11-Feb-18.
 */
@Root (name = "ValCurs",strict = true)
public class ValCurs {
    @Attribute (name = "Date")
    private String date;
    @Attribute (name = "name")
    private String name;

    @ElementList (name = "Valute",inline = true)
    private List<Valute> valute = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Valute> getValute() {
        return valute;
    }

    public void setValute(List<Valute> valute) {
        this.valute = valute;
    }
}
