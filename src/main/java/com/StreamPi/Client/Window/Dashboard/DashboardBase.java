package com.StreamPi.Client.Window.Dashboard;

import com.StreamPi.Client.Connection.ClientListener;
import com.StreamPi.Client.IO.Config;
import com.StreamPi.Client.Profile.ClientProfile;
import com.StreamPi.Client.Window.ExceptionAndAlertHandler;
import com.StreamPi.Client.Window.Dashboard.ActionGridPane.ActionGridPane;
import com.StreamPi.Util.Exception.SevereException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
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
        settingsButton.setGraphic(fontIcon);

        HBox hBox = new HBox(settingsButton);
        hBox.setPadding(new Insets(0,5,5,0));
        hBox.setAlignment(Pos.CENTER_RIGHT);


        getChildren().addAll(actionGridPane,hBox);

        getStyleClass().add("dashboard");

        setCache(true);
        setCacheHint(CacheHint.SPEED);
    }

    public void renderProfile(ClientProfile clientProfile) throws SevereException
    {
        renderProfile(clientProfile, "root");
    }

    public void renderProfile(ClientProfile clientProfile, String currentParent) throws SevereException
    {
        actionGridPane.setClientProfile(clientProfile);
        actionGridPane.setCurrentParent(currentParent);

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
