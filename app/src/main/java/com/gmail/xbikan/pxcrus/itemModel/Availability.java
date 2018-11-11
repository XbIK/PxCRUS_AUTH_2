
package com.gmail.xbikan.pxcrus.itemModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Availability {


    private List<AllowedAction> allowedActions = null;

    private String itemStatus;

    private List<AdditionalStatusList> additionalStatusList = null;

    private BaseMessageStatus baseMessageStatus;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public List<AllowedAction> getAllowedActions() {
        return allowedActions;
    }


    public void setAllowedActions(List<AllowedAction> allowedActions) {
        this.allowedActions = allowedActions;
    }


    public String getItemStatus() {
        return itemStatus;
    }


    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public List<AdditionalStatusList> getAdditionalStatusList() {
        return additionalStatusList;
    }


    public void setAdditionalStatusList(List<AdditionalStatusList> additionalStatusList) {
        this.additionalStatusList = additionalStatusList;
    }

    public BaseMessageStatus getBaseMessageStatus() {
        return baseMessageStatus;
    }


    public void setBaseMessageStatus(BaseMessageStatus baseMessageStatus) {
        this.baseMessageStatus = baseMessageStatus;
    }


    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }


    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
