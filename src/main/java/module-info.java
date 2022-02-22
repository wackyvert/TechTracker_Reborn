module com.verus.techtracker_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

                        requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires com.opencsv;


    opens com.verus.techtracker_2 to javafx.fxml;
    exports com.verus.techtracker_2;
}