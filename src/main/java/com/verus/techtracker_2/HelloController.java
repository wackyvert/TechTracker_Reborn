package com.verus.techtracker_2;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class HelloController {
    ObservableList<ObservableList> data = FXCollections.observableArrayList();
    @FXML
    private Label welcomeText;
    @FXML
    private TableView<ObservableList> OrgTbl;

    @FXML
    private static Connection conn;
    private static final String url = "jdbc:sqlserver://10.9.32.46:1433;database=TechTracker;integratedSecurity=true";

    public HelloController() {
    }

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

    public void initialize (URL url, ResourceBundle resourceBundle) throws SQLException, ClassNotFoundException {
        loadData();
    }
    ResultSet rs = null;
    public void loadData() throws SQLException, ClassNotFoundException {
        getConnection();



        try{
            PreparedStatement ps = connect().prepareStatement("SELECT CustomerID, CustomerName, Inactive FROM dbo.tbCustomers");
            rs=ps.executeQuery();
            System.out.println(rs);
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                OrgTbl.getColumns().addAll(col);
                System.out.println("Column [" + i + "] ");
            }

            /**
             * ******************************
             * Data added to ObservableList *
             *******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                System.out.println("Row [1] added " + row);
                data.add(row);

            }

            //FINALLY ADDED TO TableView
            OrgTbl.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }


    }
