// 
// Decompiled by Procyon v0.5.36
// 

package com.stream_pi.client.window.firsttimeuse;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.exception.SevereException;
import javafx.geometry.Orientation;
import com.gluonhq.attach.orientation.OrientationService;
import com.stream_pi.client.profile.ClientProfile;
import java.io.File;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.io.Config;
import javafx.application.Platform;
import com.stream_pi.util.uihelper.HBoxInputBox;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.control.Label;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class FinalConfigPane extends VBox
{
    private TextField clientNicknameTextField;
    private TextField serverIPHostNameTextField;
    private TextField serverPortTextField;
    private Button nextButton;
    private ExceptionAndAlertHandler exceptionAndAlertHandler;
    private ClientListener clientListener;
    private int rowsToSet;
    private int colsToSet;
    
    public FinalConfigPane(final ExceptionAndAlertHandler exceptionAndAlertHandler, final ClientListener clientListener, final Button nextButton) {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.clientListener = clientListener;
        this.nextButton = nextButton;
        this.getStyleClass().add((Object)"first_time_use_pane_final_config");
        final Label label = new Label("That's it. Now just a little bit and then you're set!");
        label.setWrapText(true);
        VBox.setVgrow((Node)label, Priority.ALWAYS);
        label.getStyleClass().add((Object)"first_time_use_pane_final_config_label");
        this.clientNicknameTextField = new TextField();
        this.serverIPHostNameTextField = new TextField();
        this.serverPortTextField = new TextField();
        final HBoxInputBox clientNickNameInputBox = new HBoxInputBox("Nickname", this.clientNicknameTextField, 150);
        final HBoxInputBox serverIPHostNameInputBox = new HBoxInputBox("Server IP", this.serverIPHostNameTextField, 150);
        final HBoxInputBox serverIPPortInputBox = new HBoxInputBox("Server Port", this.serverPortTextField, 150);
        this.getChildren().addAll((Object[])new Node[] { (Node)label, (Node)clientNickNameInputBox, (Node)serverIPHostNameInputBox, (Node)serverIPPortInputBox });
        this.setSpacing(10.0);
        this.setVisible(false);
    }
    
    public void makeChangesToNextButton() {
        this.nextButton.setText("Confirm");
        this.nextButton.setOnAction(actionEvent -> new Thread((Runnable)new Task<Void>() {
            protected Void call() {
                FinalConfigPane.this.onConfirmButtonClicked();
                return null;
            }
        }).start());
    }
    
    private void onConfirmButtonClicked() {
        Platform.runLater(() -> this.nextButton.setDisable(true));
        final StringBuilder errors = new StringBuilder();
        if (this.clientNicknameTextField.getText().isBlank()) {
            errors.append("* Nick name cannot be blank.\n");
        }
        if (this.serverIPHostNameTextField.getText().isBlank()) {
            errors.append("* Server IP cannot be empty.\n");
        }
        int port = -1;
        try {
            port = Integer.parseInt(this.serverPortTextField.getText());
            if (port < 1024) {
                errors.append("* Server Port should be above 1024.\n");
            }
            else if (port > 65535) {
                errors.append("* Server Port must be lesser than 65535\n");
            }
        }
        catch (NumberFormatException exception) {
            errors.append("* Server Port should be a number.\n");
        }
        if (errors.toString().isEmpty()) {
            try {
                Config.getInstance().setNickName(this.clientNicknameTextField.getText());
                Config.getInstance().setServerHostNameOrIP(this.serverIPHostNameTextField.getText());
                Config.getInstance().setServerPort(port);
                Config.getInstance().setFirstTimeUse(false);
                if (ClientInfo.getInstance().isPhone()) {
                    Config.getInstance().setScreenMoverEnabled(true);
                }
                Config.getInstance().save();
                final ClientProfile clientProfile = new ClientProfile(new File(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, Config.getInstance().getProfilesPath(), Config.getInstance().getStartupProfileID())), Config.getInstance().getIconsPath());
                final int pre = clientProfile.getActionSize() + clientProfile.getActionGap() * 4;
                this.rowsToSet = (int)(this.clientListener.getStageHeight() / pre);
                this.colsToSet = (int)(this.clientListener.getStageWidth() / pre);
                if (ClientInfo.getInstance().isPhone()) {
                    int tmp;
                    OrientationService.create().ifPresent(orientationService -> {
                        if (orientationService.getOrientation().isPresent() && ((Orientation)orientationService.getOrientation().get()).equals((Object)Orientation.VERTICAL)) {
                            tmp = this.rowsToSet;
                            this.rowsToSet = this.colsToSet;
                            this.colsToSet = tmp;
                        }
                        return;
                    });
                }
                clientProfile.setCols(this.colsToSet);
                clientProfile.setRows(this.rowsToSet);
                clientProfile.saveProfileDetails();
                Platform.runLater(() -> {
                    this.clientListener.init();
                    this.clientListener.setupClientConnection();
                });
            }
            catch (Exception e) {
                e.printStackTrace();
                this.exceptionAndAlertHandler.handleSevereException(new SevereException(e.getMessage()));
            }
        }
        else {
            Platform.runLater(() -> this.nextButton.setDisable(false));
            new StreamPiAlert("Uh Oh", invokedynamic(makeConcatWithConstants:(Ljava/lang/StringBuilder;)Ljava/lang/String;, errors), StreamPiAlertType.ERROR).show();
        }
    }
}
