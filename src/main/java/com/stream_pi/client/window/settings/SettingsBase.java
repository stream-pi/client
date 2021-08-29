// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.window.settings;

import javafx.scene.CacheHint;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import com.stream_pi.client.window.settings.About.AboutTab;
import javafx.scene.control.Tab;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.event.Event;
import javafx.scene.input.SwipeEvent;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import javafx.application.HostServices;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

public class SettingsBase extends VBox
{
    private TabPane tabPane;
    private GeneralTab generalTab;
    private Button closeButton;
    private HostServices hostServices;
    private ExceptionAndAlertHandler exceptionAndAlertHandler;
    
    public SettingsBase(final HostServices hostServices, final ExceptionAndAlertHandler exceptionAndAlertHandler, final ClientListener clientListener) {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.hostServices = hostServices;
        (this.tabPane = new TabPane()).addEventFilter(SwipeEvent.ANY, Event::consume);
        this.tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow((Node)this.tabPane, Priority.ALWAYS);
        final Tab generalSettingsTab = new Tab("Settings");
        generalSettingsTab.setContent((Node)(this.generalTab = new GeneralTab(exceptionAndAlertHandler, clientListener, hostServices)));
        final Tab aboutTab = new Tab("About");
        aboutTab.setContent((Node)new AboutTab(clientListener));
        this.tabPane.getTabs().addAll((Object[])new Tab[] { generalSettingsTab, aboutTab });
        this.setAlignment(Pos.TOP_RIGHT);
        VBox.setMargin((Node)(this.closeButton = new Button("Close")), new Insets(5.0));
        this.getChildren().addAll((Object[])new Node[] { (Node)this.tabPane, (Node)this.closeButton });
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);
    }
    
    public void setDefaultTabToGeneral() {
        this.tabPane.getSelectionModel().selectFirst();
    }
    
    public Button getCloseButton() {
        return this.closeButton;
    }
    
    public GeneralTab getGeneralTab() {
        return this.generalTab;
    }
}
