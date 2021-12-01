package com.verus.techtracker_2;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

public class orgmodel {
    String CustomerID;
    String CustomerName;
    boolean Inactive;
    String notes;
    StringProperty CustomerIDProp;
    StringProperty CustomerNameProp;
    BooleanProperty inactiveProp;
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    public String getId() {
        return CustomerID;
    }

    public void setId(String CustomerID) {
        this.CustomerID = CustomerID;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setName(String CustomerName) {
        this.CustomerName = CustomerName;
    }

    public boolean isInactive() {
        return Inactive;
    }

    public void setInactive(boolean Inactive) {
        this.Inactive = Inactive;
    }

    public orgmodel(String CustomerID, String CustomerName, boolean inactive, String notes) {
        this.CustomerID = CustomerID;
        this.CustomerName=CustomerName;
        this.Inactive=inactive;
        this.notes=notes;
    }
}
