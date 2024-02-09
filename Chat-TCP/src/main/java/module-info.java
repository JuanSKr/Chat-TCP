module org.sk.chattcp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens org.sk.chattcp to javafx.fxml;
    exports org.sk.chattcp;
}