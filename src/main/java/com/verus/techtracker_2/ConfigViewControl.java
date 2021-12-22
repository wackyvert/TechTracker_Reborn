package com.verus.techtracker_2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfigViewControl {
    ResultSet resultSet;
    @FXML
    public DatePicker purchaseDate;
    @FXML
    public DatePicker renewalDate;
    @FXML
    public static TextArea configField;
    @FXML
    public static TextArea notesField;
    @FXML
    public static TextField serialBox;
    @FXML
    public static TextField idBox;
    @FXML
    public static TextField modelBox;
    @FXML
    public TableView pwTable;
    @FXML
    public TableView wrTable;
    @FXML
    public TableView ipTable;
    @FXML
    ObservableList<ObservableList> pwList = FXCollections.observableArrayList();
    @FXML
    ObservableList<ObservableList> wrList = FXCollections.observableArrayList();
    @FXML
    ObservableList<ObservableList> ipList = FXCollections.observableArrayList();
    @FXML
    public static void setConfigInfo(String model, String hardwareSiteId, String idtag, String serial, String purchase, String renewal, String config, String notes){
        configField.setText(config);
        notesField.setText(notes);
        serialBox.setText(serial);
        idBox.setText(idtag);
        modelBox.setText(model);
    }
    @FXML
    public void initialize() throws SQLException, ClassNotFoundException {
        String id = MainViewController.getCurrentID();
        DBControl.setTableView(id, pwTable, pwList);
        DBControl.setTableView(id, wrTable, wrList);
        DBControl.setTableView(id, ipTable, ipList);
        resultSet=DBControl.getRs("SELECT        dbo.tbCustomers.CustomerName AS Organization, \n" +
                "                         CASE WHEN HardwareType = 2 THEN 'Server' WHEN HardwareType = 1 THEN 'Workstation' WHEN HardwareType = 3 THEN 'Switch' WHEN HardwareType = 4 THEN 'Switch' WHEN HardwareType\n" +
                        "                          = 5 THEN 'Router' WHEN HardwareType = 6 THEN 'Firewall' WHEN HardwareType = 7 THEN 'Miscellaneous' WHEN HardwareType = 8 THEN 'Printer' WHEN HardwareType = 9 THEN 'Bridge'\n" +
                        "                          ELSE 'Managed Configuration' END AS configuration_type_name, { fn CONCAT(dbo.tbHardware.HardwareName, dbo.tbHardware.ModelNo) } AS model, dbo.tbHardwareSite.SerialNo AS serial_number, \n" +
                        "                          dbo.tbHardwareSite.PurchaseDate AS purchased_at, dbo.tbHardwareSite.InstallDate AS installed_at, \n" +
                        "                          dbo.tbHardwareSite.IDTag AS hardware_name, \n" +
                        "                         CASE WHEN tbHardwareSite.Inactive = 'TRUE' THEN 'Inactive' WHEN tbHardwareSite.Inactive = 'FALSE' THEN 'Active' END AS configuration_status_name, dbo.tbManufacturers.MftrName AS manufacturer_name, \n" +
                        "                         dbo.tbSites.SiteName AS location, dbo.tbCustomers.CustomerID AS ignore\n" +
                        "FROM            dbo.tbHardwareSite INNER JOIN\n" +
                        "                         dbo.tbHardware ON dbo.tbHardwareSite.HardwareID_FK = dbo.tbHardware.HardwareID INNER JOIN\n" +
                        "                         dbo.tbManufacturers ON dbo.tbHardware.ManufacturerID_FK = dbo.tbManufacturers.MftrID INNER JOIN\n" +
                        "                         dbo.tbSites ON dbo.tbHardwareSite.SiteID_FK = dbo.tbSites.SiteID INNER JOIN\n" +
                        "                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID\n" +
                        "WHERE CustomerID=" + id);
        while(resultSet.next()){
            configField.setText(resultSet.getString("purchased_at"));
        }


    }
}
