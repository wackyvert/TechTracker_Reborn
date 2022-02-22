package com.verus.techtracker_2;

import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class DBHandler {

    /********
     * Handles the DB connection
     */
    private static Connection conn;
    private static final String url = "jdbc:sqlserver://10.9.32.46:1433;database=TechTracker;integratedSecurity=true";
    public static Connection connect() throws SQLException {
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
        }catch(ClassNotFoundException cnfe){
            System.err.println("Error: "+cnfe.getMessage());
        }catch(InstantiationException ie){
            System.err.println("Error: "+ie.getMessage());
        }catch(IllegalAccessException iae){
            System.err.println("Error: "+iae.getMessage());
        }

        conn = DriverManager.getConnection(url);
        return conn;
    }

    public static Connection getConnection() throws SQLException, ClassNotFoundException{
        if(conn !=null && !conn.isClosed())
            return conn;
        connect();
        return conn;


    }


    public static void getHardware() throws SQLException {
        ResultSet rs = null;

        try {

            PreparedStatement preparedStatement = connect().prepareStatement("SELECT        dbo.tbCustomers.CustomerName AS Organization, \n" +
                    "                         CASE WHEN HardwareType = 2 THEN 'Server' WHEN HardwareType = 1 THEN 'Workstation' WHEN HardwareType = 3 THEN 'Switch' WHEN HardwareType = 4 THEN 'Switch' WHEN HardwareType\n" +
                    "                          = 5 THEN 'Router' WHEN HardwareType = 6 THEN 'Firewall' WHEN HardwareType = 7 THEN 'Miscellaneous' WHEN HardwareType = 8 THEN 'Printer' WHEN HardwareType = 9 THEN 'Access Point'\n" +
                    "                          ELSE 'Managed Configuration' END AS configuration_type_name, { fn CONCAT(dbo.tbHardware.HardwareName, dbo.tbHardware.ModelNo) } AS model, dbo.tbHardwareSite.SerialNo AS serial_number, \n" +
                    "                         SUBSTRING(dbo.RTF2Text(dbo.tbHardwareSite.SwitchRouterConfig), 0, 20000)  AS operating_system_notes, dbo.tbHardwareSite.PurchaseDate AS purchased_at, dbo.tbHardwareSite.InstallDate AS installed_at, \n" +
                    "                         CASE WHEN CONVERT(VARCHAR,dbo.RTF2Text(dbo.tbHardwareSite.Notes)) = '{' THEN ' ' WHEN Hostname IS NULL THEN SUBSTRING(dbo.RTF2Text(dbo.tbHardwareSite.Notes), 0, 2000)" +
                    "                         ELSE CONCAT(SUBSTRING(dbo.RTF2Text(dbo.tbHardwareSite.Notes), 0, 2000),' Physical Host: ', dbo.tbHardwareSite.Hostname,' ', dbo.tbHardwareSite.HardwareURL) END AS notes, dbo.tbHardwareSite.IDTag AS hardware_name, \n" +
                    "                         CASE WHEN tbHardwareSite.Inactive = 'TRUE' THEN 'Inactive' WHEN tbHardwareSite.Inactive = 'FALSE' THEN 'Active' END AS configuration_status_name, dbo.tbManufacturers.MftrName AS manufacturer_name, \n" +
                    "                         dbo.tbSites.SiteName AS location, dbo.tbCustomers.CustomerID AS ignore\n" +
                    "FROM            dbo.tbHardwareSite INNER JOIN\n" +
                    "                         dbo.tbHardware ON dbo.tbHardwareSite.HardwareID_FK = dbo.tbHardware.HardwareID INNER JOIN\n" +
                    "                         dbo.tbManufacturers ON dbo.tbHardware.ManufacturerID_FK = dbo.tbManufacturers.MftrID INNER JOIN\n" +
                    "                         dbo.tbSites ON dbo.tbHardwareSite.SiteID_FK = dbo.tbSites.SiteID INNER JOIN\n" +
                    "                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID\n" +
                    migrateMain.idlist);
            preparedStatement.setMaxRows(100000);
            rs =  preparedStatement.executeQuery();
            System.out.println(rs.toString());
            FileOutputStream path = new FileOutputStream(new File("C:\\Users\\jtorgerson\\Desktop\\hard.csv"));
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(path, StandardCharsets.UTF_8));
            csvWriter.writeAll(rs, true);
            csvWriter.flush();
            csvWriter.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }}
    public static void getSoftware() throws SQLException {
        ResultSet rs = null;

        try {
            PreparedStatement preparedStatement = connect().prepareStatement("SELECT        CASE WHEN dbo.tbSoftwareSite.SerialNo IS NULL THEN ' ' ELSE SerialNo END AS serial_number, CASE WHEN dbo.tbSoftwareSite.ExpDate IS NULL THEN ' ' ELSE ExpDate END AS expiration_date, \n" +
                    "                         SUBSTRING(dbo.RTF2Text(dbo.tbSoftwareSite.Notes), 0, 2000) AS notes, CASE WHEN dbo.tbSoftwareSite.IDTag LIKE '%0.%' THEN CONCAT(IDTag, ' - ', SoftwareName) ELSE IDTag END AS name, dbo.tbSites.SiteName AS location, \n" +
                    "                         dbo.tbCustomers.CustomerName AS organization, CASE WHEN tbSites.Inactive = 'TRUE' THEN 'Inactive' ELSE 'Active' END AS status, dbo.tbManufacturers.MftrName AS manufacturer_name, \n" +
                    "                         dbo.tbCustomers.CustomerID AS ignore, dbo.tbSoftware.SoftwareName AS software_name, dbo.tbSoftwareSite.Quantity, CONCAT(dbo.tbSoftwareSite.CDKey, ' ', dbo.tbSoftwareSite.InstallationKey, ' ', \n" +
                    "                         dbo.tbSoftwareSite.RegistrationKey)  AS install_key, CASE WHEN dbo.tbSoftwareSite.WorkOrderID_FK IS NULL THEN 'Software' ELSE 'Software' END AS category," +
                    "CASE WHEN dbo.tbSoftwareSite.UpdatedBy IS NULL THEN 'On Premises' ELSE 'On Premises' END AS service_location\n" +
                    "FROM            dbo.tbSites INNER JOIN\n" +
                    "                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID INNER JOIN\n" +
                    "                         dbo.tbSoftwareSite ON dbo.tbSites.SiteID = dbo.tbSoftwareSite.SiteID_FK INNER JOIN\n" +
                    "                         dbo.tbSoftware ON dbo.tbSoftwareSite.SoftwareID_FK = dbo.tbSoftware.SoftwareID INNER JOIN\n" +
                    "                         dbo.tbManufacturers ON dbo.tbSoftware.ManufacturerID_FK = dbo.tbManufacturers.MftrID\n" +

                    migrateMain.idlist);
            rs = preparedStatement.executeQuery();
            assert false;

            System.out.println(rs.toString());
            FileOutputStream path = new FileOutputStream(new File("C:\\Users\\jtorgerson\\Desktop\\soft.csv"));
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(path, StandardCharsets.UTF_8));
            csvWriter.writeAll(rs, true);
            csvWriter.flush();
            csvWriter.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }

    public static void getIP() throws SQLException {
        ResultSet rs = null;

        try {

            PreparedStatement preparedStatement = connect().prepareStatement("SELECT        dbo.tbIPAddr.IPAddress, dbo.tbIPAddr.SubnetMask, dbo.tbIPAddr.InterfaceName, dbo.tbHardwareSite.IDTag, dbo.tbIPAddr.Description, dbo.tbCustomers.CustomerName, dbo.tbCustomers.CustomerID, \n" +
                    "                         dbo.tbIPAddr.RemoteGateway\n" +
                    "FROM            dbo.tbIPAddr INNER JOIN\n" +
                    "                         dbo.tbHardwareSite ON dbo.tbIPAddr.HardwareID_FK = dbo.tbHardwareSite.HardwareSiteID INNER JOIN\n" +
                    "                         dbo.tbSites ON dbo.tbIPAddr.SiteID_FK = dbo.tbSites.SiteID AND dbo.tbHardwareSite.SiteID_FK = dbo.tbSites.SiteID INNER JOIN\n" +
                    "                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID\n" +
                    migrateMain.idlist);
            rs =preparedStatement.executeQuery();
            assert false;

            System.out.println(rs.toString());
            FileOutputStream path = new FileOutputStream(new File("C:\\Users\\jtorgerson\\Desktop\\ip.csv"));
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(path, StandardCharsets.UTF_8));
            csvWriter.writeAll(rs, true);
            csvWriter.flush();
            csvWriter.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }
    public static void getWarranties() throws SQLException {
        ResultSet rs = null;

        try {

            PreparedStatement preparedStatement = connect().prepareStatement("SELECT        dbo.tbWarranties.EffectiveDate AS effective_date, dbo.tbWarranties.ExpDate AS expiration_date, dbo.tbWarranties.Licence AS license_key, dbo.RTF2Text(dbo.tbWarranties.Notes) AS notes, \n" +
                    "                         dbo.tbWarrantyTypes.WarrantyTypeName AS warranty_type, dbo.tbSites.SiteName AS location, dbo.tbCustomers.CustomerName AS organization, dbo.tbCustomers.CustomerID AS ignore, \n" +
                    "                         dbo.tbHardwareSite.IDTag AS configuration\n" +
                    "FROM            dbo.tbWarranties INNER JOIN\n" +
                    "                         dbo.tbWarrantyTypes ON dbo.tbWarranties.WarrantyTypeID_FK = dbo.tbWarrantyTypes.WarrantyTypeID INNER JOIN\n" +
                    "                         dbo.tbSites ON dbo.tbWarranties.SiteID_FK = dbo.tbSites.SiteID INNER JOIN\n" +
                    "                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID INNER JOIN\n" +
                    "                         dbo.tbHardwareSite ON dbo.tbWarranties.HardwareID_FK = dbo.tbHardwareSite.HardwareSiteID AND dbo.tbSites.SiteID = dbo.tbHardwareSite.SiteID_FK\n" +
                    migrateMain.idlist);
            rs =  preparedStatement. executeQuery();
            assert false;

            System.out.println(rs.toString());
            FileOutputStream path = new FileOutputStream(new File("C:\\Users\\jtorgerson\\Desktop\\warra.csv"));
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(path, StandardCharsets.UTF_8));
            csvWriter.writeAll(rs, true);
            csvWriter.flush();
            csvWriter.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }

    public static void getPasswords() throws SQLException {
        ResultSet rs = null;

        try {

            PreparedStatement preparedStatement = connect().prepareStatement("SELECT        CASE WHEN dbo.tbUserPass.Username IS NULL THEN 'No Username' ELSE dbo.tbUserPass.Username END AS username,CASE WHEN dbo.tbUserPassTypes.UserPassTypeName IS NULL THEN 'NO Username' else CONCAT(dbo.tbUserPassTypes.UserPassTypeName, ' - ',dbo.tbUserPass.Username) END as Name, CASE WHEN Password IS NULL THEN 'No Password' ELSE Password END AS password, dbo.tbHardwareSite.IDTag AS configuration, \n" +
                    "                         dbo.tbCustomers.CustomerID, dbo.tbCustomers.CustomerName AS organization, dbo.tbUserPassTypes.UserPassTypeName AS category\n" +
                    "FROM            dbo.tbHardwareUserPass INNER JOIN\n" +
                    "                         dbo.tbHardwareSite ON dbo.tbHardwareUserPass.HardwareID_FK = dbo.tbHardwareSite.HardwareSiteID INNER JOIN\n" +
                    "                         dbo.tbUserPass ON dbo.tbHardwareUserPass.UserPassID_FK = dbo.tbUserPass.UserPassID INNER JOIN\n" +
                    "                         dbo.tbUserPassTypes ON dbo.tbUserPass.UserPassTypeID_FK = dbo.tbUserPassTypes.UserPassTypeID INNER JOIN\n" +
                    "                         dbo.tbSites ON dbo.tbHardwareSite.SiteID_FK = dbo.tbSites.SiteID INNER JOIN\n" +
                    "                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID\n" +

                    migrateMain.idlist);
            rs =preparedStatement.executeQuery();
            assert false;

            System.out.println(rs.toString());
            FileOutputStream path = new FileOutputStream(new File("C:\\Users\\jtorgerson\\Desktop\\password.csv"));
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(path, StandardCharsets.UTF_8));
            csvWriter.writeAll(rs, true);
            csvWriter.flush();
            csvWriter.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }

    public static void getWebApps() throws SQLException {
        ResultSet rs = null;

        try {

            PreparedStatement preparedStatement = connect().prepareStatement("SELECT        dbo.tbCustomers.CustomerID AS ignore, CASE WHEN dbo.tbWebAppsSite.Inactive = 'True' THEN 'Inactive' ELSE 'Active' END AS status, dbo.tbCustomers.CustomerName AS organization, dbo.tbSites.SiteName AS location, \n" +
                    "                         CASE WHEN WebAppURL IS NULL THEN '  ' ELSE WebAppUrl END AS url, CASE WHEN dbo.RTF2Text(dbo.tbWebAppsSite.Notes) = '{' THEN '' ELSE dbo.RTF2Text(dbo.tbWebAppsSite.Notes) END AS notes, \n" +
                    "                        CONCAT(IDTag, ' - ', WebAppName) AS name, CASE WHEN dbo.tbWebApps.WebAppType = 1 THEN 'Online' ELSE 'Online' END AS service_location, \n" +
                    "                         CASE WHEN dbo.tbWebAppsSite.TimeStamp IS NULL THEN 'Web App' ELSE 'Web App' END AS category, dbo.tbManufacturers.MftrName AS manufacturer\n" +
                    "FROM            dbo.tbCustomers INNER JOIN\n" +
                    "                         dbo.tbSites ON dbo.tbCustomers.CustomerID = dbo.tbSites.CustomerID_FK INNER JOIN\n" +
                    "                         dbo.tbWebAppsSite ON dbo.tbSites.SiteID = dbo.tbWebAppsSite.SiteID_FK INNER JOIN\n" +
                    "                         dbo.tbWebApps ON dbo.tbWebAppsSite.WebAppID_FK = dbo.tbWebApps.WebAppID INNER JOIN\n" +
                    "                         dbo.tbManufacturers ON dbo.tbWebApps.ManufacturerID_FK = dbo.tbManufacturers.MftrID\n " +
                    migrateMain.idlist+"\nORDER BY name");
            rs =preparedStatement.executeQuery();
            System.out.println("SELECT        dbo.tbCustomers.CustomerID AS ignore, CASE WHEN dbo.tbWebAppsSite.Inactive = 'True' THEN 'Inactive' ELSE 'Active' END AS status, dbo.tbCustomers.CustomerName AS organization, dbo.tbSites.SiteName AS location, \n" +
                    "                         CASE WHEN WebAppURL IS NULL THEN '  ' ELSE WebAppUrl END AS url, CASE WHEN dbo.RTF2Text(dbo.tbWebAppsSite.Notes) = '{' THEN '' ELSE dbo.RTF2Text(dbo.tbWebAppsSite.Notes) END AS notes, \n" +
                    "                         CASE WHEN dbo.tbWebAppsSite.IDTag LIKE '%0.%' THEN CONCAT(WebAppName, ' - ', WebAppURL) ELSE CONCAT(IDTag, ' - ',WebAppURL) END AS name, CASE WHEN dbo.tbWebApps.WebAppType = 1 THEN 'Online' ELSE 'Online' END AS service_location, \n" +
                    "                         CASE WHEN dbo.tbWebAppsSite.TimeStamp IS NULL THEN 'Web App' ELSE 'Web App' END AS category, dbo.tbManufacturers.MftrName AS manufacturer\n" +
                    "FROM            dbo.tbCustomers INNER JOIN\n" +
                    "                         dbo.tbSites ON dbo.tbCustomers.CustomerID = dbo.tbSites.CustomerID_FK INNER JOIN\n" +
                    "                         dbo.tbWebAppsSite ON dbo.tbSites.SiteID = dbo.tbWebAppsSite.SiteID_FK INNER JOIN\n" +
                    "                         dbo.tbWebApps ON dbo.tbWebAppsSite.WebAppID_FK = dbo.tbWebApps.WebAppID INNER JOIN\n" +
                    "                         dbo.tbManufacturers ON dbo.tbWebApps.ManufacturerID_FK = dbo.tbManufacturers.MftrID");
            assert false;

            System.out.println(rs.toString());
            FileOutputStream path = new FileOutputStream(new File("C:\\Users\\jtorgerson\\Desktop\\webapp.csv"));
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(path, StandardCharsets.UTF_8));
            csvWriter.writeAll(rs, true);
            csvWriter.flush();
            csvWriter.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }
    public static void getWebAppsPW() throws SQLException {
        ResultSet rs = null;

        try {

            PreparedStatement preparedStatement = connect().prepareStatement("SELECT        dbo.tbUserPass.Username, dbo.tbUserPass.Password, dbo.tbCustomers.CustomerID, dbo.tbCustomers.CustomerName, CONCAT(IDTag, ' - ', WebAppName) AS name \n" +
                    "                       \n" +
                    "FROM            dbo.tbUserPass INNER JOIN\n" +
                    "                         dbo.tbWebAppUserPass ON dbo.tbUserPass.UserPassID = dbo.tbWebAppUserPass.UserPassID_FK INNER JOIN\n" +
                    "                         dbo.tbWebAppsSite ON dbo.tbWebAppUserPass.WebAppID_FK = dbo.tbWebAppsSite.WebAppSiteID INNER JOIN\n" +
                    "                         dbo.tbWebApps ON dbo.tbWebAppsSite.WebAppID_FK = dbo.tbWebApps.WebAppID INNER JOIN\n" +
                    "                         dbo.tbSites ON dbo.tbWebAppsSite.SiteID_FK = dbo.tbSites.SiteID INNER JOIN\n" +
                    "                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID\n " +
                    migrateMain.idlist+"\nORDER BY name");
            rs =preparedStatement.executeQuery();

            assert false;

            System.out.println(rs.toString());
            FileOutputStream path = new FileOutputStream(new File("C:\\Users\\jtorgerson\\Desktop\\webappPW.csv"));
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(path, StandardCharsets.UTF_8));
            csvWriter.writeAll(rs, true);
            csvWriter.flush();
            csvWriter.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }
    public static void getCircuits() throws SQLException {
        ResultSet rs = null;

        try {

            PreparedStatement preparedStatement = connect().prepareStatement("/****** Script for SelectTopNRows command from SSMS  ******/\n" +
                    "SELECT         dbo.tbCircuits.CircuitType, dbo.tbCircuits.Speed1, dbo.tbCircuits.Speed2, CASE WHEN dbo.tbCircuits.SpeedScale = 1 THEN 'KBPS' ELSE 'MBPS' END AS scale, dbo.RTF2Text(dbo.tbCircuits.Notes) AS Notes, \n" +
                    "                         dbo.tbCarriers.CarrierName, dbo.tbSites.SiteName, dbo.tbCircuits.InternetOrWAN, dbo.tbCircuits.Inactive, dbo.tbCustomers.CustomerName, dbo.tbCircuitTypes.CircuitTypeName\n" +
                    "FROM            dbo.tbCircuits INNER JOIN\n" +
                    "                         dbo.tbCarriers ON dbo.tbCircuits.CarrierID_FK = dbo.tbCarriers.CarrierID INNER JOIN\n" +
                    "                         dbo.tbSites ON dbo.tbCircuits.SiteID1_FK = dbo.tbSites.SiteID INNER JOIN\n" +
                    "                         dbo.tbCircuitTypes ON dbo.tbCircuits.CircuitType = dbo.tbCircuitTypes.CircuitTypeID INNER JOIN\n" +
                    "                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID\n " +
                    migrateMain.idlist);
            rs =preparedStatement.executeQuery();

            assert false;

            System.out.println(rs.toString());
            FileOutputStream path = new FileOutputStream(new File("C:\\Users\\jtorgerson\\Desktop\\circuit.csv"));
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(path, StandardCharsets.UTF_8));
            csvWriter.writeAll(rs, true);
            csvWriter.flush();
            csvWriter.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }


    }
}




