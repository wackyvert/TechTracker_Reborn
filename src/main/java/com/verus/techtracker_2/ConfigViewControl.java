package com.verus.techtracker_2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ConfigViewControl {
   @FXML
    TextArea configField = new TextArea();
   @FXML
   TextArea notesField = new TextArea();
   @FXML
   TextField serialBox = new TextField();
   @FXML
   TextField modelBox = new TextField();
   @FXML
   TextField idBox = new TextField();
   //******QUERIES******\\
    public final String passwordQuery ="SELECT\n" +
           "  tbHardwareSite.IDTag AS Name\n" +
           " ,tbUserPass.Username AS Username\n" +
           " ,tbUserPass.Password AS Password\n" +
           " ,tbUserPassTypes.UserPassTypeName AS Type\n" +
           " ,tbHardwareSite.HardwareSiteID AS [Hardware ID]\n" +
           "FROM dbo.tbUserPass\n" +
           "INNER JOIN dbo.tbUserPassTypes\n" +
           "  ON tbUserPass.UserPassTypeID_FK = tbUserPassTypes.UserPassTypeID\n" +
           "INNER JOIN dbo.tbHardwareUserPass\n" +
           "  ON tbHardwareUserPass.UserPassID_FK = tbUserPass.UserPassID\n" +
           "INNER JOIN dbo.tbHardwareSite\n" +
           "  ON tbHardwareUserPass.HardwareID_FK = tbHardwareSite.HardwareSiteID\n" +
           "WHERE tbHardwareSite.HardwareSiteID = "+MainViewController.gethwid()+"\n";

    public final String wrQuery = "SELECT\n" +
            "  tbWarranties.EffectiveDate\n" +
            " ,tbWarranties.ExpDate\n" +
            " ,dbo.rtf2text(tbWarranties.Notes)\n" +
            " ,tbWarranties.WarrantyDesc\n" +
            " ,tbHardwareSite.HardwareSiteID\n" +
            "FROM dbo.tbWarranties\n" +
            "INNER JOIN dbo.tbHardwareSite\n" +
            "  ON tbWarranties.HardwareID_FK = tbHardwareSite.HardwareSiteID\n" +
            "WHERE HardwareSiteID = "+MainViewController.gethwid();
    public final String ipQuery ="SELECT\n" +
            "  tbIPAddr.IPAddress\n" +
            " ,tbIPAddr.SubnetMask\n" +
            " ,tbIPAddr.DefaultGateway\n" +
            " ,tbIPAddr.RemoteGateway\n" +
            " ,tbIPAddr.InterfaceName\n" +
            " ,tbHardwareSite.HardwareSiteID\n" +
            " ,tbHardwareSite.IDTag\n" +
            "FROM dbo.tbIPAddr\n" +
            "INNER JOIN dbo.tbHardwareSite\n" +
            "  ON tbIPAddr.HardwareID_FK = tbHardwareSite.HardwareSiteID\n WHERE tbHardwareSite.HardwareSiteID = "+MainViewController.gethwid()+"\n";
    //public final String ipQuery;
    ResultSet resultSet;
    @FXML
    public DatePicker purchaseDate;
    @FXML
    public DatePicker renewalDate;



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



    public static final LocalDate LOCAL_DATE (String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        return localDate;
    }

    @FXML
    public void initialize() throws SQLException, ClassNotFoundException {

        pwTable.getItems().clear();
        wrTable.getItems().clear();
        ipTable.getItems().clear();
        notesField.clear();
        configField.clear();

        System.out.println("setting text");



        ResultSet rs1 =  DBControl.getRs("SELECT      dbo.tbHardwareSite.hardwaresiteid,  dbo.tbCustomers.CustomerName AS Organization, \n" +
                "                         CASE WHEN HardwareType = 2 THEN 'Server' WHEN HardwareType = 1 THEN 'Workstation' WHEN HardwareType = 3 THEN 'Switch' WHEN HardwareType = 4 THEN 'Switch' WHEN HardwareType\n" +
                "                          = 5 THEN 'Router' WHEN HardwareType = 6 THEN 'Firewall' WHEN HardwareType = 7 THEN 'Miscellaneous' WHEN HardwareType = 8 THEN 'Printer' WHEN HardwareType = 9 THEN 'Bridge'\n" +
                "                          ELSE 'Managed Configuration' END AS configuration_type_name, dbo.tbManufacturers.MftrName+' '+dbo.tbHardware.HardwareName+' '+ dbo.tbHardware.ModelNo  AS model, dbo.tbHardwareSite.SerialNo AS serial_number, \n" +
                "                          dbo.tbHardwareSite.PurchaseDate AS purchased_at, dbo.tbHardwareSite.InstallDate AS installed_at, \n" +
                "                          dbo.tbHardwareSite.IDTag AS hardware_name, dbo.tbHardwareSite.ExpDate, \n" +
                "                         CASE WHEN tbHardwareSite.Inactive = 'TRUE' THEN 'Inactive' WHEN tbHardwareSite.Inactive = 'FALSE' THEN 'Active' END AS configuration_status_name, dbo.tbManufacturers.MftrName AS manufacturer_name, \n" +
                "                         dbo.tbSites.SiteName AS location, dbo.tbCustomers.CustomerID AS ignore, dbo.rtf2Text(dbo.tbHardwareSite.Notes) as Notes, dbo.rtf2Text(dbo.tbHardwareSite.switchrouterconfig) AS config\n" +
                "FROM            dbo.tbHardwareSite INNER JOIN\n" +
                "                         dbo.tbHardware ON dbo.tbHardwareSite.HardwareID_FK = dbo.tbHardware.HardwareID INNER JOIN\n" +
                "                         dbo.tbManufacturers ON dbo.tbHardware.ManufacturerID_FK = dbo.tbManufacturers.MftrID INNER JOIN\n" +
                "                         dbo.tbSites ON dbo.tbHardwareSite.SiteID_FK = dbo.tbSites.SiteID INNER JOIN\n" +
                "                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID\n" +
                "WHERE hardwaresiteid = "+MainViewController.gethwid());
        while (rs1.next()) {
            String serial = rs1.getString("serial_number");
            String model = rs1.getString("model");
            String config = rs1.getString("config");
            String notes = rs1.getString("notes");
            String idtag = rs1.getString("hardware_name");
            String purchasedate = rs1.getString("purchased_at");
            String renewaldate = rs1.getString("ExpDate");
            idBox.setText(idtag);
            configField.setText(config);
            notesField.setText(notes);
            serialBox.setText(serial);
            modelBox.setText(model);
            try{
                purchaseDate.setValue(LOCAL_DATE(purchasedate.substring(0,10)));
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
            try{
                renewalDate.setValue(LOCAL_DATE(renewaldate.substring(0,10)));
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }
        }



        DBControl.setTableView( pwTable, pwList, passwordQuery);
        DBControl.setTableView( ipTable, ipList, ipQuery);
        DBControl.setTableView(wrTable, wrList, wrQuery);
    }


    }

