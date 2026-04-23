module rb.lms {
    requires javafx.controls;
    requires javafx.fxml;

    opens rb.lms to javafx.fxml;
    exports rb.lms;
}
