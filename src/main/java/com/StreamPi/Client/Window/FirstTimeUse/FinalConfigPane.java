package com.StreamPi.Client.Window.FirstTimeUse;

import com.StreamPi.Client.Connection.Client;
import com.StreamPi.Client.Connection.ClientListener;
import com.StreamPi.Client.IO.Config;
import com.StreamPi.Client.Info.ClientInfo;
import com.StreamPi.Client.Window.ExceptionAndAlertHandler;
import com.StreamPi.Util.Alert.StreamPiAlert;
import com.StreamPi.Util.Alert.StreamPiAlertListener;
import com.StreamPi.Util.Alert.StreamPiAlertType;
import com.StreamPi.Util.Exception.SevereException;
import com.StreamPi.Util.FormHelper.HBoxInputBox;
import com.StreamPi.Util.FormHelper.SpaceFiller;
import com.StreamPi.Util.FormHelper.SpaceFiller.FillerType;
import com.StreamPi.Util.Platform.Platform;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FinalConfigPane extends VBox
{
    private TextField clientNicknameTextField;
    private TextField serverIPHostNameTextField;
    private TextField serverPortTextField;
    private TextField displayWidthTextField;
    private TextField displayHeightTextField;

    private ExceptionAndAlertHandler exceptionAndAlertHandler;
    private ClientListener clientListener;

    public FinalConfigPane(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener)
    {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.clientListener = clientListener;

        getStyleClass().add("first_time_use_pane_final_config");

        Label label = new Label("Thats it. Now just a little bit and then youre set!");
        label.setWrapText(true);
        VBox.setVgrow(label, Priority.ALWAYS);
        label.getStyleClass().add("first_time_use_pane_final_config_label");

        clientNicknameTextField = new TextField();
        serverIPHostNameTextField = new TextField();
        serverPortTextField = new TextField();

        displayWidthTextField = new TextField();
        displayHeightTextField = new TextField();

        HBoxInputBox clientNickNameInputBox = new HBoxInputBox("Nickname", clientNicknameTextField);
        HBoxInputBox serverIPHostNameInputBox = new HBoxInputBox("Server IP", serverIPHostNameTextField);
        HBoxInputBox serverIPPortInputBox = new HBoxInputBox("Server Port", serverPortTextField);
        HBoxInputBox displayWidthInputBox = new HBoxInputBox("Display Width", displayWidthTextField);
        HBoxInputBox displayHeightInputBox = new HBoxInputBox("Display Height", displayHeightTextField);

        if(ClientInfo.getInstance().getPlatformType() == Platform.ANDROID)
        {
            displayWidthInputBox.setVisible(false);
            displayHeightInputBox.setVisible(false);
        }

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(event -> onConfirmButtonClicked());
        HBox bBar = new HBox(confirmButton);
        bBar.setAlignment(Pos.CENTER_RIGHT);

        VBox v = new VBox(clientNickNameInputBox, serverIPHostNameInputBox, serverIPPortInputBox,
        displayWidthInputBox, displayHeightInputBox);
        v.setSpacing(10.0);

        ScrollPane scrollPane = new ScrollPane(v);
        v.prefWidthProperty().bind(scrollPane.widthProperty().subtract(25));

        getChildren().addAll(label, scrollPane,new SpaceFiller(FillerType.VBox), bBar);

        setSpacing(10.0);

        setVisible(false);
    }

    private void onConfirmButtonClicked()
    {
        StringBuilder errors = new StringBuilder();

        if(clientNicknameTextField.getText().isBlank())
        {
            errors.append("* Nick name cannot be blank.\n");
        }

        if(serverIPHostNameTextField.getText().isBlank())
        {
            errors.append("* Server IP cannot be empty.\n");
        }

        int port = -1;
        try
        {
            port = Integer.parseInt(serverPortTextField.getText());

            if(port < 1024)
                errors.append("* Server IP should be above 1024.\n");
        }
        catch (NumberFormatException exception)
        {
            errors.append("* Server IP should be a number.\n");
        }

        double width=-1,height=-1;

        if(ClientInfo.getInstance().getPlatformType() != Platform.ANDROID)
        {
            try
            {
                width = Double.parseDouble(displayWidthTextField.getText());
    
                if(width < 0)
                    errors.append("* Display Width should be above 0.\n");
            }
            catch (NumberFormatException exception)
            {
                errors.append("* Display Width should be a number.\n");
            }
    
            try
            {
                height = Double.parseDouble(displayHeightTextField.getText());
    
                if(height < 0)
                    errors.append("* Display Height should be above 0.\n");
            }
            catch (NumberFormatException exception)
            {
                errors.append("* Display Height should be a number.\n");
            }
        }

        if(errors.toString().isEmpty())
        {
            try
            {
                Config.getInstance().setNickName(clientNicknameTextField.getText());
                Config.getInstance().setServerHostNameOrIP(serverIPHostNameTextField.getText());
                Config.getInstance().setServerPort(port);
                Config.getInstance().setFirstTimeUse(false);

                if(ClientInfo.getInstance().getPlatformType() != Platform.ANDROID)
                {
                    Config.getInstance().setStartupWindowSize(
                        width, height
                    );
                }


                Config.getInstance().save();

                if(ClientInfo.getInstance().isFrameBufferMode())
                {
                    StreamPiAlert alert = new StreamPiAlert("Done!","Start StreamPi Again to see changes",StreamPiAlertType.INFORMATION);
                    alert.setOnClicked(new StreamPiAlertListener()
                    {
                        @Override
                        public void onClick(String buttonClicked)
                        {
                            javafx.application.Platform.exit();
                        }
                    });
                }
                else
                {
                    clientListener.init();
                }


            }
            catch(SevereException e)
            {
                exceptionAndAlertHandler.handleSevereException(e);
            }
        }
        else
        {
            new StreamPiAlert("Uh Oh", "Please rectify the following errors and try again:\n"+errors.toString(), StreamPiAlertType.ERROR).show();
        }
    }
}
