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

package com.stream_pi.client.window.settings.about;

import com.stream_pi.client.controller.ClientListener;
import com.stream_pi.client.i18n.I18N;
import com.stream_pi.util.links.Links;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;


public class ContactTab extends ScrollPane
{
    private final ClientListener clientListener;

    public ContactTab(ClientListener clientListener)
    {
        this.clientListener = clientListener;

        getStyleClass().add("about_contact_tab_scroll_pane");

        Hyperlink github = new Hyperlink(I18N.getString("window.settings.about.ContactTab.github"));
        github.setOnAction(event -> openWebpage(Links.getGitHub()));

        Hyperlink discord = new Hyperlink(I18N.getString("window.settings.about.ContactTab.discord"));
        discord.setOnAction(event -> openWebpage(Links.getDiscord()));

        Hyperlink website = new Hyperlink(I18N.getString("window.settings.about.ContactTab.website"));
        website.setOnAction(event -> openWebpage(Links.getWebsite()));

        Hyperlink twitter = new Hyperlink(I18N.getString("window.settings.about.ContactTab.twitter"));
        twitter.setOnAction(event -> openWebpage(Links.getTwitter()));

        Hyperlink matrix = new Hyperlink(I18N.getString("window.settings.about.ContactTab.matrix"));
        matrix.setOnAction(event -> openWebpage(Links.getMatrix()));


        VBox vBox = new VBox(github, discord, website, twitter, matrix);
        vBox.setSpacing(10.0);

        setContent(vBox);
    }


    public void openWebpage(String url)
    {
        clientListener.openURL(url);
    }

}
