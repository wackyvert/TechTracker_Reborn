package com.verus.techtracker_2;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.*;

public class MainViewController {
    private static final String url = "jdbc:sqlserver://10.9.32.46:1433;database=TechTracker;integratedSecurity=true";
    private static String currentID;
    private static Connection conn1;
    private static Connection conn2;
    private static String currentHardwareID_fk;
    @FXML
    Scene scene;
    @FXML
    TextField custNameBox = new TextField();
    @FXML
    TextArea CustNotesBox = new TextArea();
    @FXML
    CheckBox InactiveBox = new CheckBox();
    @FXML
    Button refreshButton = new Button();
    @FXML
    ObservableList<ObservableList> hardDataList = FXCollections.observableArrayList();
    @FXML
    ObservableList<ObservableList> softDataList = FXCollections.observableArrayList();
    ResultSet rs = null;
    @FXML
    private TextField nameFilterBox;
    @FXML
    private Button searchButton;
    @FXML
    private TextField snFilterBox;
    @FXML
    private ListView<ConfigModel> configList;
    @FXML
    private TextField typeFilterBox;
    @FXML
    private TextField ConfigCount, WarrantyCount, SoftwareCount, PWCount, WebAppCount, IpCount;
    @FXML
    private ListView<orgmodel> orgTbl2;
    @FXML
    private TableView<ObservableList> HardTbl;
    @FXML
    private TableView<ObservableList> SoftTbl;
    @FXML
    private CheckBox MigratedBox;
    @FXML
    private CheckBox CountsBox;


    public static String getCurrentID() {

        return currentID;
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

    public void loadCounts(String id) throws SQLException, ClassNotFoundException {
        getConnection(conn2);
        try {
            PreparedStatement ps = connect(conn2).prepareStatement("SELECT A.CUSTOMERID,A.CUSTOMERNAME,A.INACTIVE,ISNULL(B.SITECOUNT,0) AS 'Site Count',ISNULL(B.HARDWARE_COUNT,0)  AS 'Configuration Count', ISNULL (PW_COUNT, 0) AS 'Password Count',ISNULL(IP_CNT,0) AS 'IP Count',\n" +
                    "ISNULL(APP_CNT, 0) AS 'Application Count',ISNULL(b.WRNT_CNT,0) AS 'Warranty Count',b.ITGCountsMatch AS 'counts match', b.MigratedToITG AS 'Migrated to ITG', ISNULL(b.WEBAPP_CNT,0) AS 'webapp count',\n" +
                    "ISNULL(HWPW_CNT,0) AS 'HW PW Count',ISNULL(WAPW_CNT,0) AS 'WA PW Count',  ISNULL(B.SW_COUNT,0) AS 'Software Count'\n" +
                    "FROM DBO.tbCustomers A\n" +
                    "LEFT JOIN\n" +
                    "(\n" +
                    "       SELECT C.CUSTOMERID,C.CUSTOMERNAME,SITECOUNT, hardware_count, CASE WHEN software_count IS NULL THEN 0 ELSE software_count END AS SW_COUNT, CASE WHEN warranty_count IS NULL THEN 0 ELSE warranty_count END AS WRNT_CNT,\n" +
                    "\t   CASE WHEN webapp_count IS NULL THEN 0 ELSE webapp_count END AS WEBAPP_CNT,CASE WHEN hwPwCount IS NULL THEN 0 ELSE hwPwCount END AS HWPW_CNT, CASE WHEN ip_count IS NULL THEN 0 ELSE ip_count END AS IP_CNT,\n" +
                    "\t   CASE WHEN waPwCount IS NULL THEN 0 ELSE waPwCount END AS WAPW_CNT, SUM(ISNULL(webapp_count, 0)+ISNULL(software_count, 0)) AS APP_CNT, SUM (ISNULL(waPwCount,0)+ISNULL(hwPwCount, 0)) AS PW_COUNT,c.MigratedToITG, c.ITGCountsMatch\n" +
                    "FROM dbo.tbCustomers C\n" +
                    "LEFT JOIN\n" +
                    "                (SELECT COUNT(SITEID) AS SITECOUNT,CUSTOMERID_FK\n" +
                    "                FROM TBSITES\n" +
                    "                GROUP BY CUSTOMERID_FK)S\n" +
                    "ON C.CUSTOMERID = S.CUSTOMERID_FK\n" +
                    "\n" +
                    "\n" +
                    "LEFT JOIN\n" +
                    "                (SELECT COUNT(HardwareSiteID) AS hardware_count,--count(UserPassID_fk) as hw_passcount, \n" +
                    "                customerid_fk\n" +
                    "                FROM tbHardwareSite hs\n" +
                    "                --join tbHardwareUserPass hp on hs.HardwareID_FK = hp.HardwareID_FK\n" +
                    "                LEFT join tbsites s on hs.siteid_fk = s.siteid\n" +
                    "                GROUP BY customerid_fk)h\n" +
                    "on c.customerid = h.customerid_fk\n" +
                    "\n" +
                    "\n" +
                    "LEFT join\n" +
                    "(\n" +
                    "select count(password) as hwPwCount,customerid\n" +
                    "from\n" +
                    "(\n" +
                    "SELECT CASE WHEN dbo.tbUserPass.Username IS NULL THEN 'No Username' ELSE dbo.tbUserPass.Username END AS username,\n" +
                    "CASE WHEN dbo.tbUserPassTypes.UserPassTypeName IS NULL THEN 'NO Username' else CONCAT(dbo.tbUserPassTypes.UserPassTypeName, ' - ',dbo.tbUserPass.Username) END as Name, \n" +
                    "CASE WHEN Password IS NULL THEN 'No Password' ELSE Password END AS password, dbo.tbHardwareSite.IDTag AS configuration, \n" +
                    "dbo.tbCustomers.CustomerID, dbo.tbCustomers.CustomerName AS organization, dbo.tbUserPassTypes.UserPassTypeName AS category\n" +
                    "FROM dbo.tbHardwareUserPass LEFT JOIN\n" +
                    "    dbo.tbHardwareSite ON dbo.tbHardwareUserPass.HardwareID_FK = dbo.tbHardwareSite.HardwareSiteID LEFT JOIN\n" +
                    "dbo.tbUserPass ON dbo.tbHardwareUserPass.UserPassID_FK = dbo.tbUserPass.UserPassID LEFT JOIn\n" +
                    "dbo.tbUserPassTypes ON dbo.tbUserPass.UserPassTypeID_FK = dbo.tbUserPassTypes.UserPassTypeID LEFT JOIN\n" +
                    "dbo.tbSites ON dbo.tbHardwareSite.SiteID_FK = dbo.tbSites.SiteID LEFT JOIN\n" +
                    "    dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID\n" +
                    "--WHERE CustomerID = 329\n" +
                    ")aa\n" +
                    "group by customerid\n" +
                    ") hwp on c.customerid = hwp.customerid\n" +
                    "\n" +
                    "LEFT join(\n" +
                    "select count(password) as waPwCount,customerid\n" +
                    "from\n" +
                    "(\n" +
                    "SELECT        dbo.tbUserPass.Username, dbo.tbUserPass.Password, dbo.tbCustomers.CustomerID, dbo.tbCustomers.CustomerName, CASE WHEN dbo.tbWebAppsSite.IDTag LIKE '%0.%' THEN CONCAT(WebAppName, ' - ', IDTag) ELSE CONCAT(IDTag, ' - ', WebAppName) \n" +
                    "                                             END AS name\n" +
                    "                    FROM            dbo.tbUserPass LEFT JOIN\n" +
                    "                                             dbo.tbWebAppUserPass ON dbo.tbUserPass.UserPassID = dbo.tbWebAppUserPass.UserPassID_FK LEFT JOIN\n" +
                    "                                             dbo.tbWebAppsSite ON dbo.tbWebAppUserPass.WebAppID_FK = dbo.tbWebAppsSite.WebAppSiteID LEFT JOIN\n" +
                    "                                             dbo.tbWebApps ON dbo.tbWebAppsSite.WebAppID_FK = dbo.tbWebApps.WebAppID LEFT JOIN\n" +
                    "                                             dbo.tbSites ON dbo.tbWebAppsSite.SiteID_FK = dbo.tbSites.SiteID LEFT JOIN\n" +
                    "                                           dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID\n" +
                    "                    )wapw\n" +
                    "                                                                                                                                                                                group by customerid\n" +
                    ") wpw on c.customerid = wpw.CustomerID\n" +
                    "LEFT join\n" +
                    "                (SELECT COUNT(SoftwareSiteID) AS software_count,customerid_fk\n" +
                    "                FROM tbSoftwareSite ss\n" +
                    "                LEFT join tbsites s on ss.siteid_fk = s.siteid\n" +
                    "                GROUP BY customerid_fk)L\n" +
                    "on c.customerid = L.customerid_fk\n" +
                    "LEFT join\n" +
                    "                (SELECT COUNT(IPAddrID) AS ip_count,customerid_fk\n" +
                    "                FROM tbIPAddr ip\n" +
                    "                LEFT join tbsites s on ip.siteid_fk = s.siteid\n" +
                    "                GROUP BY customerid_fk)ipa\n" +
                    "on c.customerid = ipa.customerid_fk\n" +
                    "\n" +
                    "--join\n" +
                    "--             (select count(userpassid_fk) as hwpw_count,h.HardwareSiteID,customerid_fk\n" +
                    "--             from tbHardwareUserPass up\n" +
                    "                --join tbhardwaresite h on up.HardwareID_FK = h.hardwaresiteid\n" +
                    "                --join tbsites s on h.siteid_fk = s.siteid\n" +
                    "                --group by h.HardwareSiteID)hwp\n" +
                    "--on c.customerid = hwp.HardwareSiteID\n" +
                    "LEFT JOIN\n" +
                    "                (SELECT COUNT(WarrantyID) AS warranty_count,--count(UserPassID_fk) as hw_passcount, \n" +
                    "                customerid_fk\n" +
                    "                FROM tbWarranties w\n" +
                    "                LEFT join tbsites s on w.siteid_fk = s.siteid\n" +
                    "                GROUP BY customerid_fk)wr\n" +
                    "on c.customerid = wr.customerid_fk\n" +
                    "LEFT JOIN\n" +
                    "                (SELECT COUNT(WebAppSiteID) AS webapp_count,--count(UserPassID_fk) as hw_passcount, \n" +
                    "                customerid_fk\n" +
                    "                FROM tbWebAppsSite wa\n" +
                    "                \n" +
                    "                LEFT join tbsites s on wa.siteid_fk = s.siteid\n" +
                    "                GROUP BY customerid_fk)wa\n" +
                    "on c.customerid = wa.customerid_fk\n" +
                    "\n" +
                    "group by c.CustomerID, c.CustomerName, s.SITECOUNT, h.hardware_count, l.software_count, wr.warranty_count, wa.webapp_count, hwp.hwPwCount, ipa.ip_count,wpw.waPwCount, c.MigratedToITG, c.ITGCountsMatch\n" +
                    "--ORDER BY CustomerNAME\n" +
                    "\n" +
                    ")B\n" +
                    "ON A.CustomerID = B.CUSTOMERID\n" +
                    "WHERE A.CustomerID =" + id);
            rs = ps.executeQuery();
            System.out.println(rs);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
        while (rs.next()) {
            ConfigCount.setText(rs.getString("Configuration Count"));
            SoftwareCount.setText(rs.getString("Application Count"));
            PWCount.setText(rs.getString("Password Count"));
            IpCount.setText(rs.getString("IP Count"));
            WarrantyCount.setText(rs.getString("Warranty Count"));
        }
    }

    public void loadSoftData(String id) throws SQLException, ClassNotFoundException {
        getConnection(conn2);
        SoftTbl.getItems().clear();
        SoftTbl.getColumns().clear();


        try {
            PreparedStatement ps = connect(conn2).prepareStatement("SELECT SiteName as 'Site Name', dbo.RTF2Text(Notes) as Notes FROM dbo.tbSites\n" +
                    "WHERE CustomerID_FK=" + id);
            rs = ps.executeQuery();
            System.out.println(rs);
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col1 = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col1.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
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
                if (rs.getString(i) == null) {
                    row.add(" ");
                } else {
                    row.add(rs.getString(i));
                }
            }
            System.out.println("Row [1] added " + row);
            list.add(row);

        }
    }
    @FXML
    Label custIDLabel;
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
                        boolean migrated = newSelection.isMigrated();
                        boolean counts = newSelection.isCounts();
                        boolean inactive = newSelection.isInactive();
                        // do whatever you need with the data:
                        System.out.println("Selected customer id: " + id);
                        setCustText(customerName, notes, inactive, migrated, counts, id);
                        currentID = id;
                        try {
                            updateConfigList(id);
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
                        try {
                            loadCounts(id);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

        ObservableList<orgmodel> organizationObservableList = FXCollections.observableArrayList();

        try {
            PreparedStatement ps = connect(conn1).prepareStatement("SELECT CustomerName,CustomerID, Inactive, dbo.RTF2Text(Notes), MigratedToITG, ITGCountsMatch FROM dbo.tbCustomers ORDER BY CustomerName");
            ResultSet rs = ps.executeQuery();
            System.out.println(rs);

            while (rs.next()) {                     //1 customername 2 customerid 3 inactive 4 notes 5 migrated 6 counts
                orgmodel org = new orgmodel(rs.getString(2), rs.getString(1), rs.getString(4), rs.getBoolean(3), rs.getBoolean(5), rs.getBoolean(6));
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

    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("configView.fxml"));
        scene = new Scene(root, 1188, 480);
        stage.setScene(scene);
        System.out.println("too many times?");
        stage.setTitle("TechTracker Reborn");

        stage.getIcons().add(new Image("file:icon.png"));
        stage.show();


    }
    public void startQuery(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("queryRun.fxml"));
        scene = new Scene(root, 1400, 700);
        stage.setScene(scene);

        stage.setTitle("TechTracker Reborn");

        stage.getIcons().add(new Image("file:icon.png"));
        stage.show();


    }
    public void queryButton() throws IOException {
        startQuery(stage3);
    }
    Stage stage2 = new Stage();
    Stage stage3 = new Stage();
    @FXML
    private void updateConfigList(String id) throws SQLException, ClassNotFoundException {
        configList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSelection, newSelection) -> {

                    if (newSelection != null) {
                        // assuming names of property accessor methods:
                        String Model = newSelection.getModel();
                        currentHardwareID_fk = newSelection.getHardwareid_fk();
                        String idtag = newSelection.getHardware_name();
                        String serial = newSelection.getSerial();
                        String purchase = newSelection.getPurchasedate();
                        String renewal = newSelection.getRenewaldate();
                        String config = newSelection.getConfig();
                        String notes = newSelection.getNotes();
                        // do whatever you need with the data:

                        System.out.println("Selected config id: " + idtag);
                        try {
                            start(stage2);
                            System.out.println("Selected Config:"+currentHardwareID_fk);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //ConfigViewControl.setConfigInfo(Model, hardwareid_fk, idtag, serial, purchase, renewal, config, notes);


                    }
                });

        ObservableList<ConfigModel> configObservableList = FXCollections.observableArrayList();


        ResultSet rs1 = DBControl.getRs("SELECT      dbo.tbHardwareSite.hardwaresiteid,  dbo.tbCustomers.CustomerName AS Organization, \n" +
                "                         CASE WHEN HardwareType = 2 THEN 'Server' WHEN HardwareType = 1 THEN 'Workstation' WHEN HardwareType = 3 THEN 'Switch' WHEN HardwareType = 4 THEN 'Switch' WHEN HardwareType\n" +
                "                          = 5 THEN 'Router' WHEN HardwareType = 6 THEN 'Firewall' WHEN HardwareType = 7 THEN 'Miscellaneous' WHEN HardwareType = 8 THEN 'Printer' WHEN HardwareType = 9 THEN 'Bridge'\n" +
                "                          ELSE 'Managed Configuration' END AS configuration_type_name, dbo.tbManufacturers.MftrName+' '+dbo.tbHardware.HardwareName+' '+ dbo.tbHardware.ModelNo+'('+dbo.tbHardwareSite.IDTag+')'  AS model, dbo.tbHardwareSite.SerialNo AS serial_number, \n" +
                "                          dbo.tbHardwareSite.PurchaseDate AS purchased_at, dbo.tbHardwareSite.InstallDate AS installed_at, \n" +
                "                          dbo.tbHardwareSite.IDTag AS hardware_name, \n" +
                "                         CASE WHEN tbHardwareSite.Inactive = 'TRUE' THEN 'Inactive' WHEN tbHardwareSite.Inactive = 'FALSE' THEN 'Active' END AS configuration_status_name, dbo.tbManufacturers.MftrName AS manufacturer_name, \n" +
                "                         dbo.tbSites.SiteName AS location, dbo.tbCustomers.CustomerID AS ignore, dbo.rtf2Text(dbo.tbHardwareSite.Notes) as Notes, dbo.tbHardwareSite.switchrouterconfig\n" +
                "FROM            dbo.tbHardwareSite INNER JOIN\n" +
                "                         dbo.tbHardware ON dbo.tbHardwareSite.HardwareID_FK = dbo.tbHardware.HardwareID INNER JOIN\n" +
                "                         dbo.tbManufacturers ON dbo.tbHardware.ManufacturerID_FK = dbo.tbManufacturers.MftrID INNER JOIN\n" +
                "                         dbo.tbSites ON dbo.tbHardwareSite.SiteID_FK = dbo.tbSites.SiteID INNER JOIN\n" +
                "                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID\n" +
                "WHERE CustomerID=" + id);
        while (rs1.next()) {                     //1 customername 2 customerid 3 inactive 4 notes 5 migrated 6 counts
            ConfigModel config = new ConfigModel(rs1.getString("hardware_name"), rs1.getString("purchased_at"), rs1.getString("hardwaresiteid"), rs1.getString("installed_at"), rs1.getString("serial_number"), rs1.getString("Notes"), rs1.getString("manufacturer_name"), rs1.getString("model"), rs1.getString("switchrouterconfig"));
            configObservableList.add(config);
        }
        configList.setItems(configObservableList);

        // display customer name in listview:
        configList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ConfigModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(item.getModel());
                }
            }
        });
    }
    public static String gethwid(){
        return currentHardwareID_fk;
    }
    @FXML
    private void runITGConvert() throws SQLException, ClassNotFoundException {
        migrateMain.migrate(currentID);
    }

    @FXML
    private void updateSQL() throws SQLException, ClassNotFoundException {
        ResultSet resultSet;
        String newName, newNotes;
        newName = custNameBox.getText();
        newNotes = CustNotesBox.getText();
        int newInactive, newMigrated, newCounts;
        if (InactiveBox.isSelected()) {
            newInactive = 1;
        } else {
            newInactive = 0;
        }
        if (CountsBox.isSelected()) {
            newCounts = 1;
        } else {
            newCounts = 0;
        }
        if (MigratedBox.isSelected()) {
            newMigrated = 1;
        } else {
            newMigrated = 0;
        }
        System.out.println("UPDATE dbo.tbCustomers\nSET MigratedToITG = " + newMigrated + ", ITGCountsMatch=" + newCounts + ", Inactive=" + newInactive + "\n WHERE CustomerID = " + currentID + "\n");
        try {
            PreparedStatement preparedUpdateStatement = connect(conn1).prepareStatement("UPDATE dbo.tbCustomers\nSET MigratedToITG = " + newMigrated + ", ITGCountsMatch=" + newCounts + ", Inactive=" + newInactive + "\n WHERE CustomerID = " + currentID + "\n");
            PreparedStatement preparedUpdateStatement1 = connect(conn1).prepareStatement("UPDATE dbo.tbCustomers\n" +
                    "SET CustomerName = '" + newName + "'\n WHERE CustomerID = " + currentID);
            preparedUpdateStatement.execute();
            preparedUpdateStatement1.execute();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE);
            alert.showAndWait();
            e.printStackTrace();
        }

        initialize();
    }

    public void setCustText(String name, String notes, boolean inactive, boolean migrated, boolean counts, String id) {
        System.out.println(name);
        System.out.println(notes);
        System.out.println(inactive);
        System.out.println(migrated);
        System.out.println(counts);
        MigratedBox.setSelected(migrated);
        CountsBox.setSelected(counts);
        custNameBox.setText(name);
        CustNotesBox.setText(notes);
        InactiveBox.setSelected(inactive);
        custIDLabel.setText(id);
    }

}