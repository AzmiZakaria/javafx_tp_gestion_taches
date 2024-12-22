module ma.enset.tp7_gestion_taches {
    requires javafx.controls;
    requires javafx.fxml;


    opens ma.enset.tp7_gestion_taches to javafx.fxml;
    exports ma.enset.tp7_gestion_taches;
}