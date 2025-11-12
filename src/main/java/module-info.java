module com.example.proyecto3_ {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.proyecto3_.controller to javafx.fxml;
    opens com.example.proyecto3_.view to javafx.fxml;

    exports com.example.proyecto3_;
    exports com.example.proyecto3_.controller;
    exports com.example.proyecto3_.view;
}
