package com.stream_pi.client.window.settings.about;

import com.stream_pi.action_api.ActionAPI;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.theme_api.ThemeAPI;
import com.stream_pi.util.Util;
import com.stream_pi.client.Main;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.info.ClientInfo;
import javafx.event.Event;
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
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class About extends ScrollPane
{
    private ClientListener clientListener;

    private ContributorsTab contributorsTab;
    private VBox mainVBox;

    public About(ClientListener clientListener)
    {
        this.clientListener = clientListener;

        getStyleClass().add("about_parent");

        setPadding(new Insets(5));

        mainVBox = new VBox();
        mainVBox.getStyleClass().add("about");
        mainVBox.setSpacing(5.0);


        mainVBox.setAlignment(Pos.TOP_CENTER);

        Image appIcon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/256x256.png")));
        ImageView appIconImageView = new ImageView(appIcon);
        VBox.setMargin(appIconImageView, new Insets(10, 0, 10, 0));
        appIconImageView.setFitHeight(128);
        appIconImageView.setFitWidth(128);

        Tab contributorsT = new Tab(I18N.getString("window.settings.about.About.contributorsTabHeading"));
        contributorsTab = new ContributorsTab(clientListener);
        contributorsT.setContent(contributorsTab);


        TabPane tabPane = new TabPane();
        tabPane.addEventFilter(SwipeEvent.ANY, Event::consume);

        tabPane.getStyleClass().add("settings_about_tab_internal");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setMaxWidth(600);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab licenseTab = new Tab(I18N.getString("window.settings.about.About.licenseTabHeading"));
        licenseTab.setContent(new LicenseTab());



        Tab contactTab = new Tab(I18N.getString("window.settings.about.About.contactTabHeading"));
        contactTab.setContent(new ContactTab(clientListener));

        tabPane.getTabs().addAll(licenseTab, contributorsT, contactTab);


        Hyperlink donateButton = new Hyperlink(I18N.getString("window.settings.about.About.donate"));
        donateButton.setOnAction(event -> openWebpage("https://www.patreon.com/streampi"));
        donateButton.getStyleClass().add("about_donate_hyperlink");


        ClientInfo clientInfo = ClientInfo.getInstance();

        Label versionText = new Label(clientInfo.getVersion().getText() + " - "+ clientInfo.getPlatform().getUIName() + " - "+ clientInfo.getReleaseStatus().getUIName());
        versionText.getStyleClass().add("about_version_label");

        Label commStandardLabel = new Label(I18N.getString("window.settings.about.About.commStandard", clientInfo.getCommStandardVersion().getText()));
        commStandardLabel.getStyleClass().add("about_comm_standard_label");

        Label minThemeAPILabel = new Label(I18N.getString("window.settings.about.About.minThemeAPI", ThemeAPI.MIN_VERSION_SUPPORTED.getText()));
        minThemeAPILabel.getStyleClass().add("about_min_theme_api_label");

        Label minActionAPILabel = new Label(I18N.getString("window.settings.about.About.minActionAPI", clientInfo.getMinPluginSupportVersion().getText()));
        minActionAPILabel.getStyleClass().add("about_min_action_api_label");

        Label currentActionAPILabel = new Label(I18N.getString("window.settings.about.About.currentActionAPI", ActionAPI.VERSION.getText()));
        currentActionAPILabel.getStyleClass().add("about_current_action_api_label");

        Label currentUtilLabel = new Label(I18N.getString("window.settings.about.About.currentUtil", Util.VERSION.getText()));
        currentUtilLabel.getStyleClass().add("about_current_util_label");

        VBox vBox1 = new VBox(
                versionText,
                commStandardLabel,
                minThemeAPILabel,
                minActionAPILabel,
                currentActionAPILabel,
                currentUtilLabel
        );

        vBox1.setAlignment(Pos.CENTER);
        vBox1.setSpacing(10);

        Label javaVersionLabel = new Label(I18N.getString("window.settings.about.About.java", System.getProperty("java.version")));
        javaVersionLabel.getStyleClass().add("about_java_version");

        Label javafxVersionLabel = new Label(I18N.getString("window.settings.about.About.javafx", System.getProperty("javafx.version")));
        javafxVersionLabel.getStyleClass().add("about_javafx_version");

        Label javaGCLabel = new Label(I18N.getString("window.settings.about.About.gc", ManagementFactory.getGarbageCollectorMXBeans().get(0).getName()));
        javaGCLabel.getStyleClass().add("about_java_gc");

        HBox hBox1 = new HBox(javaVersionLabel, getSep(),
                javafxVersionLabel);

        hBox1.setAlignment(Pos.CENTER);
        hBox1.setSpacing(10);


        Label disclaimerLabel = new Label("This contributor list shows only those who have contributed " +
                "to the Client Source code.\nTo know about the contributors of Action API, Theme API, Util, " +
                "visit the respective repositories. If you want to know about the Core Team instead, please visit the website.");

        disclaimerLabel.getStyleClass().add("about_license_contributors_disclaimer_label");

        disclaimerLabel.prefWidthProperty().bind(tabPane.widthProperty());

        disclaimerLabel.setWrapText(true);


        mainVBox.getChildren().addAll(appIconImageView, tabPane, disclaimerLabel,
                donateButton, vBox1, hBox1,javaGCLabel);
        mainVBox.prefWidthProperty().bind(widthProperty().subtract(30));

        setContent(mainVBox);

        InputStream inputStream = Main.class.getResourceAsStream("build.properties");
        if(inputStream != null)
        {
            try
            {
                Properties properties = new Properties();
                properties.load(inputStream);
                Label buildDateLabel = new Label(I18N.getString("window.settings.about.About.buildDate", properties.getProperty("build.date")));
                buildDateLabel.getStyleClass().add("about_build_date_label");
                mainVBox.getChildren().add(buildDateLabel);
            }
            catch (IOException e)
            {
                Logger.getLogger(getClass().getName()).warning("build.properties not present");
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
