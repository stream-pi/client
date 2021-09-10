package com.stream_pi.client.window.settings.About;

import com.stream_pi.action_api.ActionAPI;
import com.stream_pi.client.Main;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.info.ClientInfo;
import javafx.application.HostServices;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.SwipeEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Logger;

public class AboutTab extends ScrollPane
{
    private ClientListener clientListener;

    private ContributorsTab contributorsTab;
    private VBox mainVBox;

    public AboutTab(ClientListener clientListener)
    {
        this.clientListener = clientListener;

        getStyleClass().add("about_parent");

        setPadding(new Insets(5));

        mainVBox = new VBox();
        mainVBox.getStyleClass().add("about");
        mainVBox.setSpacing(5.0);


        mainVBox.setAlignment(Pos.TOP_CENTER);

        Image appIcon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("app_icon.png")));
        ImageView appIconImageView = new ImageView(appIcon);
        appIconImageView.setFitHeight(146);
        appIconImageView.setFitWidth(132);


        Tab contributorsT = new Tab("Contributors");
        contributorsTab = new ContributorsTab(clientListener);
        contributorsT.setContent(contributorsTab);


        TabPane tabPane = new TabPane();
        tabPane.addEventFilter(SwipeEvent.ANY, Event::consume);

        tabPane.getStyleClass().add("settings_about_tab_internal");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setMaxWidth(600);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab licenseTab = new Tab("License");
        licenseTab.setContent(new LicenseTab());



        Tab contactTab = new Tab("Contact");
        contactTab.setContent(new ContactTab(clientListener));

        tabPane.getTabs().addAll(licenseTab, contributorsT, contactTab);


        Hyperlink donateButton = new Hyperlink("DONATE");
        donateButton.setOnAction(event -> openWebpage("https://www.patreon.com/streampi"));
        donateButton.getStyleClass().add("about_donate_hyperlink");


        ClientInfo clientInfo = ClientInfo.getInstance();

        Label versionText = new Label(clientInfo.getVersion().getText() + " - "+ clientInfo.getPlatform().getUIName() + " - "+ clientInfo.getReleaseStatus().getUIName());
        versionText.getStyleClass().add("about_version_label");

        Label commStandardLabel = new Label("Comm Standard "+clientInfo.getCommStandardVersion().getText());
        commStandardLabel.getStyleClass().add("about_comm_standard_label");

        Label minThemeAPILabel = new Label("Min ThemeAPI "+clientInfo.getMinThemeSupportVersion().getText());
        minThemeAPILabel.getStyleClass().add("about_min_theme_api_label");

        Label minActionAPILabel = new Label("Min ActionAPI "+clientInfo.getMinPluginSupportVersion().getText());
        minActionAPILabel.getStyleClass().add("about_min_action_api_label");

        Label currentActionAPILabel = new Label("ActionAPI "+ ActionAPI.API_VERSION.getText());
        currentActionAPILabel.getStyleClass().add("about_current_action_api_label");

        HBox hBox1 = new HBox(versionText);

        hBox1.setAlignment(Pos.CENTER);
        hBox1.setSpacing(10);

        Label javaVersionLabel = new Label("Java "+System.getProperty("java.version"));
        javaVersionLabel.getStyleClass().add("about_java_version");

        Label javafxVersionLabel = new Label("JavaFX "+System.getProperty("javafx.version"));
        javafxVersionLabel.getStyleClass().add("about_javafx_version");

        Label javaGCLabel = new Label("GC: "+ ManagementFactory.getGarbageCollectorMXBeans().get(0).getName());
        javaGCLabel.getStyleClass().add("about_java_gc");

        HBox hBox2 = new HBox(javaVersionLabel, getSep(),
                javafxVersionLabel);

        hBox2.setAlignment(Pos.CENTER);
        hBox2.setSpacing(10);


        Label disclaimerLabel = new Label("This contributor list shows only those who have contributed " +
                "to the Client Source code.\nTo know about the contributors of Action API, Theme API, Util, " +
                "visit the respective repositories. If you want to know about the Core Team instead, please visit the website.");

        disclaimerLabel.getStyleClass().add("about_license_contributors_disclaimer_label");

        disclaimerLabel.prefWidthProperty().bind(tabPane.widthProperty());

        disclaimerLabel.setWrapText(true);


        mainVBox.getChildren().addAll(appIconImageView, tabPane, disclaimerLabel,
                donateButton, hBox1, hBox2,javaGCLabel);
        mainVBox.prefWidthProperty().bind(widthProperty().subtract(30));

        setContent(mainVBox);

        InputStream inputStream = Main.class.getResourceAsStream("build-date");
        if(inputStream != null)
        {
            try
            {
                Logger.getLogger(getClass().getName()).info("build-date present");
                Label buildDateLabel = new Label("Build date/time: " +  new String(inputStream.readAllBytes()));
                buildDateLabel.getStyleClass().add("build-date-label");
                mainVBox.getChildren().add(buildDateLabel);
            }
            catch (IOException e)
            {
                Logger.getLogger(getClass().getName()).info("build-date not present");
            }
        }

        setCache(true);
        setCacheHint(CacheHint.SPEED);
    }

    private Label getSep()
    {
        Label label = new Label("|");
        label.getStyleClass().add("separator_ui_label");
        return label;
    }

    public void openWebpage(String url)
    {
        clientListener.openURL(url);
    }
}
