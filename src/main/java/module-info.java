module com.stream_pi.client {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.kordamp.iconli.core;
    requires org.kordamp.ikonli.javafx;

    requires com.gluonhq.attach.lifecycle;
    requires com.gluonhq.attach.util;

    requires java.xml;

    requires com.stream_pi.util;
    requires com.stream_pi.actionapi;
    requires com.stream_pi.themeapi;

    requires org.kordamp.ikonli.fontawesome5;

    exports com.stream_pi.client;
}