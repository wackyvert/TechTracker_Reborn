package com.verus.techtracker_2;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MainViewController {

    @FXML
    private Label welcomeText;
    @FXML
    private ListView<orgmodel> orgTbl2;

    public MainViewController() {

    }
    @FXML
    ObservableList<ObservableList> hardDataList = FXCollections.observableArrayList();
    @FXML
    ObservableList<ObservableList> softDataList = FXCollections.observableArrayList();

    @FXML
    private TableView<ObservableList> HardTbl;
    @FXML
    private TableView<ObservableList> SoftTbl;


    private static Connection conn1;

    private static Connection conn2;
    private static final String url = "jdbc:sqlserver://10.9.32.46:1433;database=TechTracker;integratedSecurity=true";








    ResultSet rs = null;
    public void loadSoftData(String id) throws SQLException, ClassNotFoundException {
        getConnection(conn2);
        SoftTbl.getItems().clear();
        SoftTbl.getColumns().clear();



        try{
            PreparedStatement ps = connect(conn2).prepareStatement("SELECT SiteName as 'Site Name', dbo.RTF2Text(Notes) as Notes FROM dbo.tbSites\n" +
                    "WHERE CustomerID_FK="+id);
            rs=ps.executeQuery();
            System.out.println(rs);
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col1 = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                SoftTbl.getColumns().addAll(col1);
                System.out.println("Column [" + i + "] ");
            }

            /**
             * ******************************
             * Data added to ObservableList *
             *******************************
             */
            dupe(softDataList);

            //FINALLY ADDED TO TableView
            SoftTbl.setItems(softDataList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    private void dupe(ObservableList list) throws SQLException {
        while (rs.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                if (rs.getString(i)==null) {
                    row.add(" ");
                }
                else {
                    row.add(rs.getString(i));
                }
            }
            System.out.println("Row [1] added " + row);
            list.add(row);

        }
    }

    public void loadHardData(String id) throws SQLException, ClassNotFoundException {
        getConnection(conn1);
        HardTbl.getItems().clear();
        HardTbl.getColumns().clear();



        try{
            PreparedStatement ps = connect(conn1).prepareStatement("SELECT        dbo.tbCustomers.CustomerName AS Organization, \n" +
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
                    "WHERE CustomerID="+id);
            rs=ps.executeQuery();
            System.out.println(rs);
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                HardTbl.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }

            /**
             * ******************************
             * Data added to ObservableList *
             *******************************
             */
            dupe(hardDataList);

            //FINALLY ADDED TO TableView
          HardTbl.setItems(hardDataList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    @FXML
    public void initialize() throws SQLException, ClassNotFoundException {
        System.out.println("IM BEING INITIALIZED!");

        orgTbl2.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {

                    if (newSelection != null) {
                        // assuming names of property accessor methods:
                        String customerName = newSelection.getCustomerName();
                        String notes = newSelection.getNotes();
                        String id = newSelection.getId();
                        boolean inactive = newSelection.isInactive();
                        // do whatever you need with the data:
                        System.out.println("Selected customer id: " + id);
                        setCustText(customerName, notes, inactive);
                        try {
                            loadHardData(id);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            loadSoftData(id);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
        ObservableList<orgmodel> organizationObservableList = FXCollections.observableArrayList();

        try {
            PreparedStatement ps = connect(conn1).prepareStatement("SELECT CustomerName,CustomerID, Inactive, dbo.RTF2Text(Notes) FROM dbo.tbCustomers ORDER BY CustomerName");
            ResultSet rs = ps.executeQuery();
            System.out.println(rs);

            while (rs.next()) {
                orgmodel org = new orgmodel(rs.getString(2), rs.getString(1), rs.getBoolean(3), rs.getString(4));
                organizationObservableList.add(org);
            }
            orgTbl2.setItems(organizationObservableList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
        // display customer name in listview:
        orgTbl2.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(orgmodel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.getCustomerName());
                }
            }
        });

    }



    private static Connection connect(Connection connection) throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Error: " + cnfe.getMessage());
        } catch (InstantiationException ie) {
            System.err.println("Error: " + ie.getMessage());
        } catch (IllegalAccessException iae) {
            System.err.println("Error: " + iae.getMessage());
        }

        connection = DriverManager.getConnection(url);
        return connection;
    }

    public static Connection getConnection(Connection connection) throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed())
            return connection;
        connection = connect(connection);
        return connection;
    }

    @FXML
    TextField custNameBox = new TextField();
    @FXML
    TextArea CustNotesBox = new TextArea();
    @FXML
    CheckBox InactiveBox = new CheckBox();

    public void setCustText(String name, String notes, boolean inactive) {
        System.out.println(name);
        System.out.println(notes);
        System.out.println(inactive);
        custNameBox.setText(name);
        CustNotesBox.setText(notes);
        if (inactive) {
            InactiveBox.setSelected(true);
        } else {
            InactiveBox.setSelected(false);
        }
    }

}