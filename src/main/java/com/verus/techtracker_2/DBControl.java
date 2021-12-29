package com.verus.techtracker_2;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.sql.*;

public class DBControl {
    private static Connection conn2;
    private static final String url = "jdbc:sqlserver://10.9.32.46:1433;database=TechTracker;integratedSecurity=true";
    public static ResultSet rs = null;

    private static void dupe(ObservableList list) throws SQLException {
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

    public static ResultSet getRs(String query) throws SQLException, ClassNotFoundException {
        getConnection(conn2);
        try {
            PreparedStatement ps = connect(conn2).prepareStatement(query);
            rs = ps.executeQuery();

    }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    return rs;}


    public static void setTableView( TableView SoftTbl, ObservableList softDataList, String query) throws SQLException, ClassNotFoundException {
        getConnection( conn2);
        SoftTbl.getColumns().clear();
        SoftTbl.getItems().clear();
        try {
            PreparedStatement ps = connect(conn2).prepareStatement(query);
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
}

