package com.stream_pi.client.window.firsttimeuse;

import com.stream_pi.client.Main;
import com.stream_pi.client.combobox.LanguageChooserComboBox;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.util.combobox.StreamPiComboBoxListener;
import com.stream_pi.util.exception.SevereException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import com.stream_pi.util.i18n.language.Language;

import java.util.Objects;

public class WelcomePane extends VBox
{
    public WelcomePane(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener)
    {
        getStyleClass().add("first_time_use_pane_welcome");

        Image appIcon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/256x256.png")));
        ImageView appIconImageView = new ImageView(appIcon);
        VBox.setMargin(appIconImageView, new Insets(10, 0, 10, 0));
        appIconImageView.setFitHeight(128);
        appIconImageView.setFitWidth(128);


        Label welcomeLabel = new Label(I18N.getString("firsttimeuse.WelcomePane.welcome"));
        welcomeLabel.setWrapText(true);
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.getStyleClass().add("first_time_use_welcome_pane_welcome_label");

        Label nextToContinue = new Label(I18N.getString("firsttimeuse.WelcomePane.nextToContinue"));
        nextToContinue.setWrapText(true);
        nextToContinue.setAlignment(Pos.CENTER);
        nextToContinue.getStyleClass().add("first_time_use_welcome_pane_next_to_continue_label");

        LanguageChooserComboBox languageChooserComboBox = new LanguageChooserComboBox();
        languageChooserComboBox.getStyleClass().add("first_time_use_welcome_pane_language_chooser_combo_box");

        try
        {
            languageChooserComboBox.setCurrentSelectedItem(I18N.getLanguage(Config.getInstance().getCurrentLanguageLocale()));
        }
        catch (SevereException e)
        {
            exceptionAndAlertHandler.handleSevereException(e);
        }

        languageChooserComboBox.setStreamPiComboBoxListener(new StreamPiComboBoxListener<>() {
            @Override
            public void onNewItemSelected(Language oldLanguage, Language newLanguage) {
                try
                {
                    if (oldLanguage != newLanguage)
                    {
                        Config.getInstance().setCurrentLanguageLocale(newLanguage.getLocale());
                        Config.getInstance().save();

                        clientListener.initBase();
                        clientListener.init();
                    }
                }
                catch (SevereException e)
                {
                    exceptionAndAlertHandler.handleSevereException(e);
                }

            }
        });

        setAlignment(Pos.CENTER);
        setSpacing(5.0);
        getChildren().addAll(appIconImageView, welcomeLabel, nextToContinue, languageChooserComboBox);

        setVisible(false);
    }
}
