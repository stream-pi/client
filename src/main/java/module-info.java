module com.StreamPi.Client {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.javafx;

    requires com.gluonhq.attach.lifecycle;
    requires com.gluonhq.attach.util;

    requires java.xml;

    requires com.StreamPi.Util;
    requires com.StreamPi.ActionAPI;
    requires com.StreamPi.ThemeAPI;

    requires org.kordamp.ikonli.fontawesome5;

    exports com.StreamPi.Client;
}