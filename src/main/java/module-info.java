module com.vanier.easygrapher {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires commons.math3;
    requires matheclipse.core;
    requires jas;

    exports Model;
    opens Model to javafx.fxml;
    exports Main;
    opens Main to javafx.fxml;
    exports Controller;
    opens Controller to javafx.fxml;
}
