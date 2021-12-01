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

    String hardware_name, notes, manufacturer_name, model;
    public ConfigModel(String hardware_name, String notes, String manufacturer_name, String model){
        this.hardware_name=hardware_name;
        this.notes=notes;
        this.manufacturer_name=manufacturer_name;
        this.model=model;
    }

}
