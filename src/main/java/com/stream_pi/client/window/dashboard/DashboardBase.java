// 
// Decompiled by Procyon v0.6-prerelease
// 

package com.stream_pi.client.window.dashboard;

import com.stream_pi.client.profile.ClientProfile;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import org.kordamp.ikonli.javafx.FontIcon;
import com.stream_pi.client.controller.ClientListener;
import javafx.scene.control.Button;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionGridPane;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import javafx.scene.layout.VBox;

public class DashboardBase extends VBox
{
    private ExceptionAndAlertHandler exceptionAndAlertHandler;
    private ActionGridPane actionGridPane;
    private Button settingsButton;
    
    public DashboardBase(final ExceptionAndAlertHandler exceptionAndAlertHandler, final ClientListener clientListener) {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.actionGridPane = new ActionGridPane(exceptionAndAlertHandler, clientListener);
        final FontIcon fontIcon = new FontIcon("fas-cog");
        fontIcon.getStyleClass().addAll((Object[])new String[] { "dashboard_settings_button_icon" });
        this.settingsButton = new Button();
        this.settingsButton.getStyleClass().addAll((Object[])new String[] { "dashboard_settings_button" });
        this.settingsButton.setGraphic((Node)fontIcon);
        final HBox hBox = new HBox(new Node[] { (Node)this.settingsButton });
        hBox.getStyleClass().add((Object)"dashboard_settings_button_parent");
        hBox.setPadding(new Insets(0.0, 5.0, 5.0, 0.0));
        hBox.setAlignment(Pos.CENTER_RIGHT);
        this.getChildren().addAll((Object[])new Node[] { (Node)this.actionGridPane, (Node)hBox });
        this.getStyleClass().add((Object)"dashboard");
    }
    
    public void renderProfile(final ClientProfile clientProfile, final boolean freshRender) {
        this.renderProfile(clientProfile, "root", freshRender);
    }
    
    public void renderProfile(final ClientProfile clientProfile, final String currentParent, final boolean freshRender) {
        this.actionGridPane.setClientProfile(clientProfile);
        this.actionGridPane.setCurrentParent(currentParent);
        this.actionGridPane.setFreshRender(freshRender);
        this.actionGridPane.renderGrid();
        this.actionGridPane.renderActions();
    }
    
    public void addBlankActionBox(final int col, final int row) {
        this.actionGridPane.addBlankActionBox(col, row);
    }
    
    public void clearActionBox(final int col, final int row) {
        this.actionGridPane.clearActionBox(col, row);
    }
    
    public ActionGridPane getActionGridPane() {
        return this.actionGridPane;
    }
    
    public Button getSettingsButton() {
        return this.settingsButton;
    }
}
