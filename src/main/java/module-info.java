module com.verus.techtracker_2 {
    requires javafx.controls;
    requires javafx.fxml;

                        requires org.kordamp.bootstrapfx.core;
    requires java.sql;


    opens com.verus.techtracker_2 to javafx.fxml;

    exports com.verus.techtracker_2;
}