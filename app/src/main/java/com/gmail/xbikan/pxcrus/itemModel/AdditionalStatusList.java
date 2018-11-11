
package com.gmail.xbikan.pxcrus.itemModel;

import java.util.HashMap;
import java.util.Map;



public class AdditionalStatusList {


    private String messageKey;

    private String type;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public String getMessageKey() {
        return messageKey;
    }


    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
