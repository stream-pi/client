package com.stream_pi.client.window.dashboard;

import com.stream_pi.client.connection.ClientListener;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.client.window.dashboard.actiongridpane.ActionGridPane;
import com.stream_pi.util.exception.SevereException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class DashboardBase extends VBox {
    private ExceptionAndAlertHandler exceptionAndAlertHandler;

    private ActionGridPane actionGridPane;
    private Button settingsButton;

    public DashboardBase(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener)
    {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;

        actionGridPane = new ActionGridPane(exceptionAndAlertHandler, clientListener);

        FontIcon fontIcon = new FontIcon("fas-cog");
        fontIcon.getStyleClass().addAll("dashboard_settings_button_icon");

        settingsButton = new Button();
        settingsButton.getStyleClass().addAll("dashboard_settings_button");
        settingsButton.setGraphic(fontIcon);

        HBox hBox = new HBox(settingsButton);
        hBox.getStyleClass().add("dashboard_settings_button_parent");
        hBox.setPadding(new Insets(0,5,5,0));
        hBox.setAlignment(Pos.CENTER_RIGHT);


        getChildren().addAll(actionGridPane,hBox);

        getStyleClass().add("dashboard");
    }

    public void renderProfile(ClientProfile clientProfile, boolean freshRender)
    {
        renderProfile(clientProfile, "root", freshRender);
    }

    public void renderProfile(ClientProfile clientProfile, String currentParent, boolean freshRender)
    {
        actionGridPane.setClientProfile(clientProfile);
        actionGridPane.setCurrentParent(currentParent);
        actionGridPane.setFreshRender(freshRender);

        actionGridPane.renderGrid();
        actionGridPane.renderActions();
    }

    public void addBlankActionBox(int col, int row)
    {
        actionGridPane.addBlankActionBox(col, row);
    }

    public void clearActionBox(int col, int row)
    {
        actionGridPane.clearActionBox(col, row);
    }

    public ActionGridPane getActionGridPane() {
        return actionGridPane;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }
}
