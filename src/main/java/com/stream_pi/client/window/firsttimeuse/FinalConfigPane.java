package com.stream_pi.client.window.firsttimeuse;

import com.gluonhq.attach.orientation.OrientationService;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.info.StartupFlags;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.info.ClientInfo;
import com.stream_pi.client.profile.ClientProfile;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.util.alert.StreamPiAlert;
import com.stream_pi.util.alert.StreamPiAlertType;
import com.stream_pi.util.exception.MinorException;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.uihelper.HBoxInputBox;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import javax.xml.transform.TransformerException;
import java.io.File;

public class FinalConfigPane extends VBox
{
    private TextField clientNameTextField;
    private TextField serverIPHostNameTextField;
    private TextField serverPortTextField;
    private Button nextButton;

    private ExceptionAndAlertHandler exceptionAndAlertHandler;
    private ClientListener clientListener;

    public FinalConfigPane(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener,
                           Button nextButton)
    {
        this.exceptionAndAlertHandler = exceptionAndAlertHandler;
        this.clientListener = clientListener;
        this.nextButton = nextButton;

        getStyleClass().add("first_time_use_pane_final_config");

        Label label = new Label(I18N.getString("firsttimeuse.FinalConfigPane.subHeading"));
        label.setWrapText(true);
        VBox.setVgrow(label, Priority.ALWAYS);
        label.getStyleClass().add("first_time_use_pane_final_config_label");

        clientNameTextField = new TextField();
        serverIPHostNameTextField = new TextField();
        serverPortTextField = new TextField();

        Label warningLabel = new Label(I18N.getString("firsttimeuse.FinalConfigPane.securityWarning"));
        warningLabel.setWrapText(true);
        warningLabel.getStyleClass().add("first_time_use_pane_final_config_warning_label");



        HBoxInputBox clientNameInputBox = new HBoxInputBox(I18N.getString("name"), clientNameTextField, 150);
        HBoxInputBox serverIPHostNameInputBox = new HBoxInputBox(I18N.getString("serverHostNameOrIP"), serverIPHostNameTextField, 150);
        HBoxInputBox serverIPPortInputBox = new HBoxInputBox(I18N.getString("serverPort"), serverPortTextField, 150);

        setAlignment(Pos.TOP_CENTER);
        getChildren().addAll(label, clientNameInputBox, serverIPHostNameInputBox, serverIPPortInputBox, warningLabel);

        setSpacing(10.0);

        setVisible(false);
    }

    public void makeChangesToNextButton()
    {
        nextButton.setText(I18N.getString("firsttimeuse.FinalConfigPane.confirm"));
        nextButton.setOnAction(actionEvent -> new Thread(new Task<Void>() {
            @Override
            protected Void call()
            {
                onConfirmButtonClicked();
                return null;
            }
        }).start());
    }

    private void onConfirmButtonClicked()
    {
        Platform.runLater(()->nextButton.setDisable(true));

        StringBuilder errors = new StringBuilder();

        if(clientNameTextField.getText().isBlank())
        {
            errors.append("* ").append(I18N.getString("nameCannotBeBlank")).append("\n");
        }

        if(serverIPHostNameTextField.getText().isBlank())
        {
            errors.append("* ").append(I18N.getString("serverHostNameOrIPCannotBeBlank")).append("\n");
        }

        int port = -1;
        try
        {
            port = Integer.parseInt(serverPortTextField.getText());

            if(port < 1024)
            {
                errors.append("* ").append(I18N.getString("serverPortMustBeGreaterThan1024")).append("\n");
            }
            else if(port > 65535)
            {
                errors.append("* ").append(I18N.getString("serverPortMustBeLesserThan65535")).append("\n");
            }
        }
        catch (NumberFormatException exception)
        {
            errors.append("* ").append(I18N.getString("serverPortMustBeInteger")).append("\n");
        }

        if(errors.toString().isEmpty())
        {
            try
            {
                Config.getInstance().setName(clientNameTextField.getText());
                Config.getInstance().setServerHostNameOrIP(serverIPHostNameTextField.getText());
                Config.getInstance().setServerPort(port);
                Config.getInstance().setFirstTimeUse(false);

                if(ClientInfo.getInstance().isPhone())
                {
                    Config.getInstance().setScreenMoverEnabled(true);
                }

                Config.getInstance().save();

                ClientProfile clientProfile = new ClientProfile(new File(Config.getInstance().getProfilesPath()+"/"+
                        Config.getInstance().getStartupProfileID()+".xml"), Config.getInstance().getIconsPath());

                double pre = clientProfile.getActionSize()+(clientProfile.getActionGap()*4);


                rowsToSet = (int) (clientListener.getStageHeight()/pre);
                colsToSet = (int) (clientListener.getStageWidth()/pre);

                if(ClientInfo.getInstance().isPhone())
                {
                    OrientationService.create().ifPresent(orientationService -> {
                        if(orientationService.getOrientation().isPresent() &&
                                orientationService.getOrientation().get().equals(Orientation.VERTICAL))
                        {
                            int tmp = rowsToSet;
                            rowsToSet = colsToSet;
                            colsToSet = tmp;
                        }
                    });
                }

                clientProfile.setCols(colsToSet);
                clientProfile.setRows(rowsToSet);

                clientProfile.saveProfileDetails();

                Platform.runLater(()-> {
                    clientListener.init();
                    clientListener.setupClientConnection();
                });
            }
            catch(Exception e)
            {
                e.printStackTrace();
                exceptionAndAlertHandler.handleSevereException(new SevereException(e.getMessage()));
            }
        }
        else
        {
            Platform.runLater(()->nextButton.setDisable(false));
            new StreamPiAlert(I18N.getString("validationError", errors), StreamPiAlertType.ERROR).show();
        }
    }

    private int rowsToSet,colsToSet;
}
