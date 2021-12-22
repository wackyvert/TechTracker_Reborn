package com.verus.techtracker_2;

import java.sql.SQLException;
import java.util.Scanner;

public class migrateMain {
        public static String idlist;

        public static void migrate (String id) throws SQLException, ClassNotFoundException {
                Scanner scan = new Scanner(System.in);
                System.out.println("SELECT        CASE WHEN dbo.tbUserPass.Username IS NULL THEN 'No Username' ELSE dbo.tbUserPass.Username END AS username,CASE WHEN dbo.tbUserPassTypes.UserPassTypeName IS NULL THEN 'NO Username' else CONCAT(dbo.tbUserPassTypes.UserPassTypeName, ' - ',dbo.tbUserPass.Username) END as Name, CASE WHEN Password IS NULL THEN 'No Password' ELSE Password END AS password, dbo.tbHardwareSite.IDTag AS configuration, \\n\" +\n" +
                        "                    \"                         dbo.tbCustomers.CustomerID, dbo.tbCustomers.CustomerName AS organization, dbo.tbUserPassTypes.UserPassTypeName AS category\\n\" +\n" +
                        "                    \"FROM            dbo.tbHardwareUserPass INNER JOIN\\n\" +\n" +
                        "                    \"                         dbo.tbHardwareSite ON dbo.tbHardwareUserPass.HardwareID_FK = dbo.tbHardwareSite.HardwareSiteID INNER JOIN\\n\" +\n" +
                        "                    \"                         dbo.tbUserPass ON dbo.tbHardwareUserPass.UserPassID_FK = dbo.tbUserPass.UserPassID INNER JOIN\\n\" +\n" +
                        "                    \"                         dbo.tbUserPassTypes ON dbo.tbUserPass.UserPassTypeID_FK = dbo.tbUserPassTypes.UserPassTypeID INNER JOIN\\n\" +\n" +
                        "                    \"                         dbo.tbSites ON dbo.tbHardwareSite.SiteID_FK = dbo.tbSites.SiteID INNER JOIN\\n\" +\n" +
                        "                    \"                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID");


                idlist ="WHERE CustomerID = "+ id;

                System.out.println("SELECT         dbo.tbCircuits.CircuitType, dbo.tbCircuits.Speed1, dbo.tbCircuits.Speed2, CASE WHEN dbo.tbCircuits.SpeedScale = 1 THEN 'KBPS' ELSE 'MBPS' END AS scale, dbo.RTF2Text(dbo.tbCircuits.Notes) AS Notes, \n" +
                        "                         dbo.tbCarriers.CarrierName, dbo.tbSites.SiteName, dbo.tbCircuits.InternetOrWAN, dbo.tbCircuits.Inactive, dbo.tbCustomers.CustomerName, dbo.tbCircuitTypes.CircuitTypeName\n" +
                        "FROM            dbo.tbCircuits INNER JOIN\n" +
                        "                         dbo.tbCarriers ON dbo.tbCircuits.CarrierID_FK = dbo.tbCarriers.CarrierID INNER JOIN\n" +
                        "                         dbo.tbSites ON dbo.tbCircuits.SiteID1_FK = dbo.tbSites.SiteID INNER JOIN\n" +
                        "                         dbo.tbCircuitTypes ON dbo.tbCircuits.CircuitType = dbo.tbCircuitTypes.CircuitTypeID INNER JOIN\n" +
                        "                         dbo.tbCustomers ON dbo.tbSites.CustomerID_FK = dbo.tbCustomers.CustomerID\n ");
                DBHandler.getConnection();

                DBHandler.getHardware();
                DBHandler.getIP();
                DBHandler.getPasswords();
                DBHandler.getSoftware();
                DBHandler.getWarranties();
                DBHandler.getWebApps();
                DBHandler.getWebAppsPW();
                DBHandler.getCircuits();


                int[] ids = {29200503, 30315639, 30315641, 30315643, 30315646, 30315647, 30315650, 30315651, 30388293, 31658860, 31658924, 31659099, 31659193, 31659194, 32114112, 32114144, 32114156, 32114192, 32114195, 32114196, 32114208, 32114209, 32114241, 32114254, 32114263, 32114265, 32114266, 32114267, 32114268, 32114271, 32114272, 32114273, 32114274, 32114275, 32114276, 32114277, 32114278, 32114279, 32114280, 32114281, 32114282, 32114283, 32114286, 32114287, 32114288, 32114332, 32114333, 32114334, 32114336, 32114337};
                for (int i = 0; i < ids.length; i++) {
                        System.out.print("{\n" +
                                "    \"type\": \"configurations\",\n" +
                                "    \"attributes\": {\n" +
                                "        \"id\": ");
                        System.out.print(ids[i]);
                        System.out.print("    }\n" +
                                "  },"
                        );
                }

        }

        ;

        {
        }
}