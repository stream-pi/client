/*
 * Stream-Pi - Free, Open-Source, Modular, Cross-Platform and Programmable Macro Pad
 * Copyright (C) 2019-2022 Debayan Sutradhar (rnayabed),  Samuel Quiñones (SamuelQuinones)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.stream_pi.client.window.firsttimeuse;

import com.stream_pi.client.Main;
import com.stream_pi.client.combobox.LanguageChooserComboBox;
import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.client.io.Config;
import com.stream_pi.client.window.ExceptionAndAlertHandler;
import com.stream_pi.util.combobox.StreamPiComboBoxListener;
import com.stream_pi.util.exception.SevereException;
import com.stream_pi.util.i18n.language.Language;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.Objects;

public class WelcomePane extends VBox
{
    private ImageView appIconImageView;
    public WelcomePane(ExceptionAndAlertHandler exceptionAndAlertHandler, ClientListener clientListener)
    {
        getStyleClass().add("first_time_use_welcome_pane");

        Image appIcon = new Image(Objects.requireNonNull(Main.class.getResourceAsStream("icons/256x256.png")));
        appIconImageView = new ImageView(appIcon);
        appIconImageView.managedProperty().bind(appIconImageView.visibleProperty());
        VBox.setMargin(appIconImageView, new Insets(10, 0, 10, 0));
        appIconImageView.setFitHeight(128);
        appIconImageView.setFitWidth(128);

        Label welcomeLabel = new Label(I18N.getString("firsttimeuse.WelcomePane.welcome"));
        welcomeLabel.setWrapText(true);
        welcomeLabel.setAlignment(Pos.CENTER);
        welcomeLabel.getStyleClass().add("first_time_use_welcome_pane_welcome_label");

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

                        clientListener.setFirstRun(true);
                        clientListener.init();
                    }
                }
                catch (SevereException e)
                {
                    exceptionAndAlertHandler.handleSevereException(e);
                }

            }
        });

        heightProperty().addListener((observableValue, oldVal, newVal) -> appIconImageView.setVisible(clientListener.getStageHeight() >= 450));

        getChildren().addAll(appIconImageView, welcomeLabel, languageChooserComboBox);



        setVisible(false);
    }
}
