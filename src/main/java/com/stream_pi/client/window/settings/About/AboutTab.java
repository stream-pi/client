// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.window.settings.About;

import javafx.event.ActionEvent;
import javafx.scene.CacheHint;
import java.io.IOException;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import java.lang.management.ManagementFactory;
import java.lang.management.GarbageCollectorMXBean;
import javafx.scene.layout.HBox;
import com.stream_pi.action_api.ActionAPI;
import javafx.scene.control.Label;
import com.stream_pi.client.info.ClientInfo;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Priority;
import javafx.event.Event;
import javafx.scene.input.SwipeEvent;
import javafx.scene.control.TabPane;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.Objects;
import com.stream_pi.client.Main;
import java.io.InputStream;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import com.stream_pi.client.controller.ClientListener;
import javafx.scene.control.ScrollPane;

public class AboutTab extends ScrollPane
{
    private ClientListener clientListener;
    private ContributorsTab contributorsTab;
    private VBox mainVBox;
    
    public AboutTab(final ClientListener clientListener) {
        this.clientListener = clientListener;
        this.getStyleClass().add((Object)"about_parent");
        this.setPadding(new Insets(5.0));
        this.mainVBox = new VBox();
        this.mainVBox.getStyleClass().add((Object)"about");
        this.mainVBox.setSpacing(5.0);
        this.mainVBox.setAlignment(Pos.TOP_CENTER);
        final Image appIcon = new Image((InputStream)Objects.requireNonNull(Main.class.getResourceAsStream("app_icon.png")));
        final ImageView appIconImageView = new ImageView(appIcon);
        appIconImageView.setFitHeight(146.0);
        appIconImageView.setFitWidth(132.0);
        final Tab contributorsT = new Tab("Contributors");
        contributorsT.setContent((Node)(this.contributorsTab = new ContributorsTab()));
        final TabPane tabPane = new TabPane();
        tabPane.addEventFilter(SwipeEvent.ANY, Event::consume);
        tabPane.getStyleClass().add((Object)"settings_about_tab_internal");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setMaxWidth(600.0);
        VBox.setVgrow((Node)tabPane, Priority.ALWAYS);
        final Tab licenseTab = new Tab("License");
        licenseTab.setContent((Node)new LicenseTab());
        final Tab contactTab = new Tab("Contact");
        contactTab.setContent((Node)new ContactTab(clientListener));
        tabPane.getTabs().addAll((Object[])new Tab[] { licenseTab, contributorsT, contactTab });
        final Hyperlink donateButton = new Hyperlink("DONATE");
        donateButton.setOnAction(event -> this.openWebpage("https://www.patreon.com/streampi"));
        donateButton.getStyleClass().add((Object)"about_donate_hyperlink");
        final ClientInfo clientInfo = ClientInfo.getInstance();
        final Label versionText = new Label(clientInfo.getVersion().getText() + " - " + clientInfo.getPlatform().getUIName() + " - " + clientInfo.getReleaseStatus().getUIName());
        versionText.getStyleClass().add((Object)"about_version_label");
        final Label commStandardLabel = new Label("Comm Standard " + clientInfo.getCommStandardVersion().getText());
        commStandardLabel.getStyleClass().add((Object)"about_comm_standard_label");
        final Label minThemeAPILabel = new Label("Min ThemeAPI " + clientInfo.getMinThemeSupportVersion().getText());
        minThemeAPILabel.getStyleClass().add((Object)"about_min_theme_api_label");
        final Label minActionAPILabel = new Label("Min ActionAPI " + clientInfo.getMinPluginSupportVersion().getText());
        minActionAPILabel.getStyleClass().add((Object)"about_min_action_api_label");
        final Label currentActionAPILabel = new Label("ActionAPI " + ActionAPI.API_VERSION.getText());
        currentActionAPILabel.getStyleClass().add((Object)"about_current_action_api_label");
        final HBox hBox1 = new HBox(new Node[] { (Node)versionText });
        hBox1.setAlignment(Pos.CENTER);
        hBox1.setSpacing(10.0);
        final Label javaVersionLabel = new Label("Java " + System.getProperty("java.version"));
        javaVersionLabel.getStyleClass().add((Object)"about_java_version");
        final Label javafxVersionLabel = new Label("JavaFX " + System.getProperty("javafx.version"));
        javafxVersionLabel.getStyleClass().add((Object)"about_javafx_version");
        final Label javaGCLabel = new Label("GC: " + ManagementFactory.getGarbageCollectorMXBeans().get(0).getName());
        javaGCLabel.getStyleClass().add((Object)"about_java_gc");
        final HBox hBox2 = new HBox(new Node[] { (Node)javaVersionLabel, (Node)this.getSep(), (Node)javafxVersionLabel });
        hBox2.setAlignment(Pos.CENTER);
        hBox2.setSpacing(10.0);
        final Label disclaimerLabel = new Label("This contributor list shows only those who have contributed to the Client Source code.\nTo know about the contributors of Action API, Theme API, Util, visit the respective repositories. If you want to know about the Core Team instead, please visit the website.");
        disclaimerLabel.getStyleClass().add((Object)"about_license_contributors_disclaimer_label");
        disclaimerLabel.prefWidthProperty().bind((ObservableValue)tabPane.widthProperty());
        disclaimerLabel.setWrapText(true);
        this.mainVBox.getChildren().addAll((Object[])new Node[] { (Node)appIconImageView, (Node)tabPane, (Node)disclaimerLabel, (Node)donateButton, (Node)hBox1, (Node)hBox2, (Node)javaGCLabel });
        this.mainVBox.prefWidthProperty().bind((ObservableValue)this.widthProperty().subtract(30));
        this.setContent((Node)this.mainVBox);
        final InputStream inputStream = Main.class.getResourceAsStream("build-date");
        if (inputStream != null) {
            try {
                Logger.getLogger(this.getClass().getName()).info("build-date present");
                final Label buildDateLabel = new Label("Build date/time: " + new String(inputStream.readAllBytes()));
                buildDateLabel.getStyleClass().add((Object)"build-date-label");
                this.mainVBox.getChildren().add((Object)buildDateLabel);
            }
            catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).info("build-date not present");
            }
        }
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
    }
    
    private Label getSep() {
        final Label label = new Label("|");
        label.getStyleClass().add((Object)"separator_ui_label");
        return label;
    }
    
    public void openWebpage(final String url) {
        this.clientListener.openURL(url);
    }
}
