module eckofox.efhomefinancialdb {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;

    opens eckofox.efhomefinancialdb.application to javafx.fxml;
    exports eckofox.efhomefinancialdb.application;
    opens eckofox.efhomefinancialdb.controller to javafx.fxml;
}