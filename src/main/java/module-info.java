module com.example {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive java.sql;

    opens com.example to javafx.fxml;
    exports com.example;
    exports com.example.IProtocol;
    exports com.example.dataBase;
}
