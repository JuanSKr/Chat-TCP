module org.sk.chattcp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires static lombok;
    requires java.desktop;

    opens org.sk.chattcp to javafx.fxml;
    exports org.sk.chattcp;
}