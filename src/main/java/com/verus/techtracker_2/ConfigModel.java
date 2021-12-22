package com.verus.techtracker_2;

public class ConfigModel {

    public String getHardware_name() {
        return hardware_name;
    }

    public void setHardware_name(String hardware_name) {
        this.hardware_name = hardware_name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getManufacturer_name() {
        return manufacturer_name;
    }

    public void setManufacturer_name(String manufacturer_name) {
        this.manufacturer_name = manufacturer_name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getPurchasedate() {
        return purchasedate;
    }

    public void setPurchasedate(String purchasedate) {
        this.purchasedate = purchasedate;
    }

    public String getRenewaldate() {
        return renewaldate;
    }

    public void setRenewaldate(String renewaldate) {
        this.renewaldate = renewaldate;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
    String hardwareid_fk;
    String config, purchasedate, renewaldate, serial;
    String hardware_name, notes, manufacturer_name, model;

    public String getHardwareid_fk() {
        return hardwareid_fk;
    }

    public void setHardwareid_fk(String hardwareid_fk) {
        this.hardwareid_fk = hardwareid_fk;
    }

    public ConfigModel(String hardware_name, String purchasedate, String hardwareid_fk, String renewaldate, String serial, String notes, String manufacturer_name, String model, String config){
        this.hardware_name=hardware_name;
        this.notes=notes;
        this.manufacturer_name=manufacturer_name;
        this.model=model;
        this.config=config;
        this.purchasedate=purchasedate;
        this.renewaldate=renewaldate;
        this.serial=serial;
        this.hardwareid_fk=hardwareid_fk;
    }

}
