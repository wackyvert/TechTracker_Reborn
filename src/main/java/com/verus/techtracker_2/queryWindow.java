package com.verus.techtracker_2;

import com.opencsv.CSVWriter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;



public class queryWindow {
    public static Connection getConnection(Connection connection) throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed())
            return connection;
        connection = connect(connection);
        return connection;
    }

    private void dupe(ObservableList list, ResultSet rs) throws SQLException {
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
    private static final String url = "jdbc:sqlserver://10.9.32.46:1433;database=TechTracker;integratedSecurity=true";
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
    @FXML
    Button exportData = new Button();
    private static Connection conn;
    @FXML
    Button runButton = new Button();
    @FXML
    TableView tableview = new TableView();

    ObservableList<ObservableList> softDataList = FXCollections.observableArrayList();

    public void loadSoftData() throws SQLException, ClassNotFoundException {
        ResultSet rs = null;
        getConnection(conn);
        tableview.getItems().clear();
        tableview.getColumns().clear();


        try {
            PreparedStatement ps = connect(conn).prepareStatement("SELECT dbo.tbCustomers.CustomerID, dbo.tbCustomers.CustomerName, dbo.tbCustomers.MigratedToITG, dbo.tbCustomers.ITGCountsMatch, dbo.tbCustomers.Inactive\n" +
                    "FROM dbo.tbCustomers \n"
                    );
            rs = ps.executeQuery();
            FileOutputStream path = new FileOutputStream(new File("C:\\TechTrackerExports\\exportedData.csv"));
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(path, StandardCharsets.UTF_8));
            csvWriter.writeAll(rs, true);
            csvWriter.flush();
            csvWriter.close();
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

                tableview.getColumns().addAll(col1);
                System.out.println("Column [" + i + "] ");
            }

            /**
             * ******************************
             * Data added to ObservableList *
             *******************************
             */
            dupe(softDataList, rs);

            //FINALLY ADDED TO TableView
            tableview.setItems(softDataList);


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }
    public void exportData() throws IOException, SQLException {

    }
    public void runQuery() throws SQLException, ClassNotFoundException{
        loadSoftData();

    }
}
